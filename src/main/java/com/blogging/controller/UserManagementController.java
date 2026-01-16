
package com.blogging.controller;

import com.blogging.model.User;
import com.blogging.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserManagementController {
    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> dateColumn;
    @FXML private Label statusLabel;

    private UserService userService;
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    public void setUserService(UserService userService) {
        this.userService = userService;
        loadUsers();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        dateColumn.setCellValueFactory(cellData -> {
            java.time.LocalDateTime createdAt = cellData.getValue().getCreatedAt();
            if (createdAt != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                return new javafx.beans.property.SimpleStringProperty(createdAt.format(formatter));
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        userTable.setItems(userList);
    }

    private void loadUsers() {
        if (userService == null) return;
        try {
            List<User> users = userService.getAllUsers();
            userList.setAll(users);
            statusLabel.setText("Total users: " + users.size());
        } catch (SQLException e) {
            logger.error("Failed to load users", e);
            showError("Error", "Failed to load users: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No Selection", "Please select a user to delete.");
            return;
        }

        if (userService.getCurrentUser().getUserId() == selectedUser.getUserId()) {
            showError("Action Denied", "You cannot delete your own account here.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete user " + selectedUser.getUsername() + "?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                userService.deleteUser(selectedUser.getUserId());
                loadUsers();
                statusLabel.setText("User deleted.");
            } catch (SQLException e) {
                logger.error("Failed to delete user", e);
                showError("Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleMakeAdmin() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No Selection", "Please select a user.");
            return;
        }

        try {
            // Toggle role or set to Admin. For now, set to ADMIN.
            selectedUser.setRole("ADMIN");
            userService.updateUser(selectedUser);
            loadUsers(); // Refresh to show change
            statusLabel.setText(selectedUser.getUsername() + " is now an Admin.");
        } catch (SQLException e) {
            logger.error("Failed to update user role", e);
            showError("Error", "Failed to update role: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
