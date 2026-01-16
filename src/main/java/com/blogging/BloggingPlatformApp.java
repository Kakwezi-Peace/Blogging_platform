package com.blogging;

import com.blogging.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main JavaFX Application class for Smart Blogging Platform.
 */
public class BloggingPlatformApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(BloggingPlatformApp.class);
    private static final String APP_TITLE = "Smart Blogging Platform";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize Hibernate (this will create tables automatically)
            logger.info("Initializing Hibernate and creating database tables...");
            com.blogging.util.HibernateUtil.getSessionFactory();
            logger.info("Hibernate initialized - tables created/updated successfully");
            
            // Test database connection
            if (!DatabaseConnection.testConnection()) {
                logger.error("Failed to connect to database");
                showErrorAndExit("Database Connection Error", 
                    "Could not connect to the database. Please check your configuration.");
                return;
            }
            // Manual Migration: Ensure 'role' column exists
            ensureRoleColumnExists();
            
            logger.info("Database connection successful");
            
            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            // Set up scene
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            // Configure stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
            logger.info("Application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            showErrorAndExit("Application Error", 
                "Failed to start the application: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        // Clean up resources
        com.blogging.util.HibernateUtil.shutdown();
        DatabaseConnection.close();
        logger.info("Application stopped");
    }

    private void showErrorAndExit(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        System.exit(1);
    }

    private void ensureRoleColumnExists() {
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            // Check if column exists
            java.sql.ResultSet rs = conn.getMetaData().getColumns(null, null, "users", "role");
            if (!rs.next()) {
                logger.info("Migrating database: Adding 'role' column to 'users' table...");
                // Add column with default value to handle existing records
                stmt.execute("ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER'");
                logger.info("Migration successful: Added 'role' column");
            }
            
            // Manual Migration: Ensure 'comment_likes' table exists
            java.sql.ResultSet tables = conn.getMetaData().getTables(null, null, "comment_likes", null);
            if (!tables.next()) {
                logger.info("Migrating database: Creating 'comment_likes' table...");
                String createTableSQL = "CREATE TABLE comment_likes (" +
                        "LIKE_ID SERIAL PRIMARY KEY, " +
                        "USER_ID INT NOT NULL, " +
                        "COMMENT_ID INT NOT NULL, " +
                        "CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID), " +
                        "FOREIGN KEY (COMMENT_ID) REFERENCES COMMENTS(COMMENT_ID), " +
                        "UNIQUE(USER_ID, COMMENT_ID))";
                stmt.execute(createTableSQL);
                logger.info("Migration successful: Created 'comment_likes' table");
            }

        } catch (Exception e) {
            logger.error("Database migration failed: " + e.getMessage());
            // Don't exit, might already exist or other issue, let app try to run
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
