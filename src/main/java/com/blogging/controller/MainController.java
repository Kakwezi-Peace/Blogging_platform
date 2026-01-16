package com.blogging.controller;

import com.blogging.model.Post;
import com.blogging.model.User;
import com.blogging.service.PostService;
import com.blogging.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


 // Main controller for the blogging platform.
 //Manages navigation and overall application state.
 
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private BorderPane mainBorderPane;
    @FXML private Label welcomeLabel;
    @FXML private Button loginButton;
    @FXML private Button logoutButton;
    @FXML private Button dashboardButton;
    @FXML private Button postsButton;
    @FXML private Button myPostsButton;
    @FXML private Button createPostButton;
    @FXML private Button cacheStatsButton;
    @FXML private Button manageUsersButton; // New Admin Button
    
    private UserService userService;
    private final PostService postService;

    public MainController() {
        this.postService = new PostService();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        updateUIForLoginState();
        loadDashboard();
    }

    @FXML
    public void initialize() {
        // Initialization dependent on userService is deferred to setUserService
    }

    @FXML
    private void handleLogin() {
        try {
            // Create login dialog
            Dialog<User> dialog = createLoginDialog();
            Optional<User> result = dialog.showAndWait();
            
            result.ifPresent(user -> {
                updateUIForLoginState();
                showInfo("Login Successful", "Welcome, " + user.getUsername() + "!");
                loadDashboard();
            });
            
        } catch (Exception e) {
            logger.error("Login error", e);
            showError("Login Error", e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        userService.logout();
        updateUIForLoginState();
        showInfo("Logged Out", "You have been logged out successfully.");
        loadDashboard();
    }

    @FXML
    private void handleDashboard() {
        loadDashboard();
    }

    @FXML
    private void handleViewPosts() {
        loadPostList();
    }

    @FXML
    private void handleMyPosts() {
        if (!userService.isLoggedIn()) {
            showError("Not Logged In", "Please log in to view your posts.");
            return;
        }
        loadMyPosts();
    }

    @FXML
    private void handleCreatePost() {
        if (!userService.isLoggedIn()) {
            showError("Not Logged In", "Please log in to create a post.");
            return;
        }
        showCreatePostDialog();
    }

    @FXML
    private void handleCacheStats() {
        showCacheStatistics();
    }

    @FXML
    private void handleManageUsers() {
        if (userService.isLoggedIn() && "ADMIN".equalsIgnoreCase(userService.getCurrentUser().getRole())) {
            loadUserManagement();
        } else {
            showError("Access Denied", "You must be an admin to access this page.");
        }
    }

    /**
     * Load dashboard view.
     */
    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
            Parent dashboard = loader.load();
            
            DashboardController controller = loader.getController();
            controller.setServices(postService, userService);
            controller.loadDashboardData();
            
            mainBorderPane.setCenter(dashboard);
            
        } catch (IOException e) {
            logger.error("Failed to load dashboard", e);
            showError("Error", "Failed to load dashboard: " + e.getMessage());
        }
    }

    /**
     * Load post list view.
     */
    private void loadPostList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostListView.fxml"));
            Parent postList = loader.load();
            
            PostListController controller = loader.getController();
            controller.setServices(postService, userService);
            controller.loadPosts();
            
            mainBorderPane.setCenter(postList);
            
        } catch (IOException e) {
            logger.error("Failed to load post list", e);
            showError("Error", "Failed to load posts: " + e.getMessage());
        }
    }

    /**
     * Load user's posts.
     */
    private void loadMyPosts() {
        try {
            User currentUser = userService.getCurrentUser();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostListView.fxml"));
            Parent postList = loader.load();
            
            PostListController controller = loader.getController();
            controller.setServices(postService, userService);
            controller.loadUserPosts(currentUser.getUserId());
            
            mainBorderPane.setCenter(postList);
            
        } catch (IOException e) {
            logger.error("Failed to load user posts", e);
            showError("Error", "Failed to load your posts: " + e.getMessage());
        }
    }

    /**
     * Show create post dialog.
     */
    private void showCreatePostDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostFormView.fxml"));
            Parent form = loader.load();
            
            PostFormController controller = loader.getController();
            controller.setServices(postService, userService);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Post");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainBorderPane.getScene().getWindow());
            dialogStage.setScene(new Scene(form));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            // Refresh post list if post was created
            if (controller.isPostCreated()) {
                loadPostList();
            }
            
        } catch (IOException e) {
            logger.error("Failed to show create post dialog", e);
            showError("Error", "Failed to open create post form: " + e.getMessage());
        }
    }

    /**
     * Create login dialog.
     */
    private Dialog<User> createLoginDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Enter your credentials");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                try {
                    userService.login(usernameField.getText(), passwordField.getText());
                    return userService.getCurrentUser();
                } catch (SQLException | IllegalArgumentException e) {
                    showError("Login Failed", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    /**
     * Show cache statistics.
     */
    private void showCacheStatistics() {
        Map<String, Object> stats = postService.getCacheStats();
        
        String message = String.format(
            "Cache Size: %d posts\n" +
            "Cache Hits: %d\n" +
            "Cache Misses: %d\n" +
            "Hit Rate: %s",
            stats.get("cacheSize"),
            stats.get("cacheHits"),
            stats.get("cacheMisses"),
            stats.get("hitRate")
        );
        
        showInfo("Cache Statistics", message);
    }

    /**
     * Update UI based on login state.
     */
    private void updateUIForLoginState() {
        boolean isLoggedIn = userService.isLoggedIn();
        
        loginButton.setVisible(!isLoggedIn);
        loginButton.setManaged(!isLoggedIn);
        
        logoutButton.setVisible(isLoggedIn);
        logoutButton.setManaged(isLoggedIn);
        
        myPostsButton.setDisable(!isLoggedIn);
        createPostButton.setDisable(!isLoggedIn);
        
        // Handle Admin Button
        if (isLoggedIn && "ADMIN".equalsIgnoreCase(userService.getCurrentUser().getRole())) {
            if (manageUsersButton != null) {
                manageUsersButton.setVisible(true);
                manageUsersButton.setManaged(true);
            }
        } else {
             if (manageUsersButton != null) {
                manageUsersButton.setVisible(false);
                manageUsersButton.setManaged(false);
            }
        }
        
        if (isLoggedIn) {
            User currentUser = userService.getCurrentUser();
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        } else {
            welcomeLabel.setText("Welcome, Guest");
        }
    }

    /**
     * Show error alert.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info alert.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        showInfo("About", 
            "Smart Blogging Platform v1.0\n\n" +
            "A comprehensive blogging platform demonstrating:\n" +
            "- PostgreSQL database with 3NF normalization\n" +
            "- JavaFX user interface\n" +
            "- Performance optimization through caching and indexing\n" +
            "- Full CRUD operations\n\n" +
            "Developed as a database fundamentals project.");
    }

    /**
     * Load user management view.
     */
    private void loadUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserManagementView.fxml"));
            Parent view = loader.load();
            
            UserManagementController controller = loader.getController();
            controller.setUserService(userService);
            
            mainBorderPane.setCenter(view);
            
        } catch (IOException e) {
             logger.error("Failed to load user management", e);
             showError("Error", "Failed to load user management: " + e.getMessage());
        }
    }
}
