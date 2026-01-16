
package com.blogging.controller;

import com.blogging.model.Post;
import com.blogging.service.PostService;
import com.blogging.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for post creation/editing form.
 */
public class PostFormController {
    private static final Logger logger = LoggerFactory.getLogger(PostFormController.class);

    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField tagsField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private PostService postService;
    private UserService userService;
    private Stage dialogStage;
    private boolean postCreated = false;

    public void setServices(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isPostCreated() {
        return postCreated;
    }

    private Post postToEdit;

    public void setPost(Post post) {
        this.postToEdit = post;
        if (post != null) {
            titleField.setText(post.getTitle());
            contentArea.setText(post.getContent());
            saveButton.setText("Update");
            // Load tags if possible, for now just basic info
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String tagsText = tagsField.getText().trim();
            
            List<String> tags = Arrays.stream(tagsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

            if (postToEdit == null) {
                // Create new
                Post post = new Post(
                    userService.getCurrentUser().getUserId(),
                    title,
                    content
                );
                postService.createPost(post, tags);
                showInfo("Success", "Post created successfully!");
            } else {
                // Update existing
                postToEdit.setTitle(title);
                postToEdit.setContent(content);
                postService.updatePost(postToEdit);
                // TODO: Update tags for existing post
                showInfo("Success", "Post updated successfully!");
            }

            postCreated = true;
            dialogStage.close();
            
        } catch (SQLException e) {
            logger.error("Failed to save post", e);
            showError("Error", "Failed to save post: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean validateInput() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            showError("Validation Error", "Title cannot be empty");
            return false;
        }

        if (title.length() < 5) {
            showError("Validation Error", "Title must be at least 5 characters");
            return false;
        }

        if (content.isEmpty()) {
            showError("Validation Error", "Content cannot be empty");
            return false;
        }

        if (content.length() < 10) {
            showError("Validation Error", "Content must be at least 10 characters");
            return false;
        }

        return true;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
