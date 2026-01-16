package com.blogging.controller;

import com.blogging.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for the login page.
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink signupLink;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            userService.login(username, password);
            logger.info("Login successful for user: {}", username);
            
            // Navigate to main application
            loadMainApplication();
            
        } catch (Exception e) {
            logger.error("Login failed", e);
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleSignupLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignupView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sign Up - Smart Blogging Platform");
            
        } catch (IOException e) {
            logger.error("Failed to load signup page", e);
            showError("Failed to load signup page");
        }
    }

    private void loadMainApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Pass user service to main controller
            MainController mainController = loader.getController();
            mainController.setUserService(userService);
            
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Smart Blogging Platform");
            
        } catch (IOException e) {
            logger.error("Failed to load main application", e);
            showError("Failed to load application");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
