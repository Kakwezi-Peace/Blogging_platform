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
 * Controller for the signup page.
 */
public class SignupController {
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signupButton;
    @FXML private Hyperlink loginLink;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        try {
            userService.registerUser(username, email, password);
            logger.info("User registered successfully: {}", username);
            
            showSuccess("Account created successfully! Redirecting to login...");
            
            // Wait a moment then redirect to login
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleLoginLink);
                } catch (InterruptedException e) {
                    logger.error("Sleep interrupted", e);
                }
            }).start();
            
        } catch (Exception e) {
            logger.error("Signup failed", e);
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleLoginLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login - Smart Blogging Platform");
            
        } catch (IOException e) {
            logger.error("Failed to load login page", e);
            showError("Failed to load login page");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
}
