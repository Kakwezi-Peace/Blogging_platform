package com.blogging.controller;

import com.blogging.model.Comment;
import com.blogging.model.Post;
import com.blogging.model.User;
import com.blogging.service.CommentService;
import com.blogging.service.PostService;
import com.blogging.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostDetailController {
    private static final Logger logger = LoggerFactory.getLogger(PostDetailController.class);

    @FXML private Label titleLabel;
    @FXML private Label metadataLabel;
    @FXML private TextArea contentArea; // Use TextArea for read-only content or Text
    @FXML private ListView<Comment> commentsListView;
    @FXML private TextArea newCommentArea;
    @FXML private Button postCommentButton;
    @FXML private Label commentStatusLabel;
    @FXML private Button editPostButton;

    private Post currentPost;
    private PostService postService;
    private UserService userService;
    private CommentService commentService;

    public PostDetailController() {
        this.commentService = new CommentService();
    }

    public void setServices(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
        updateUIState();
    }

    public void setPost(Post post) {
        this.currentPost = post;
        loadPostData();
        loadComments();
    }

    private void loadPostData() {
        if (currentPost == null) return;
        
        titleLabel.setText(currentPost.getTitle());
        
        String dateStr = "Unknown Date";
        if (currentPost.getCreatedAt() != null) {
            dateStr = currentPost.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        }
        
        metadataLabel.setText(String.format("By %s | %s | %d Views", 
            currentPost.getAuthorName(), dateStr, currentPost.getViewCount()));
            
        updateEditButtonVisibility();
        contentArea.setText(currentPost.getContent());
    }

    private void updateEditButtonVisibility() {
        if (userService != null && userService.isLoggedIn() && currentPost != null) {
            User user = userService.getCurrentUser();
            boolean canEdit = user.getUserId() == currentPost.getUserId() || "ADMIN".equalsIgnoreCase(user.getRole());
            editPostButton.setVisible(canEdit);
            editPostButton.setManaged(canEdit);
        } else {
            editPostButton.setVisible(false);
            editPostButton.setManaged(false);
        }
    }

    @FXML
    private void handleEditPost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostFormView.fxml"));
            Parent root = loader.load();
            PostFormController controller = loader.getController();
            controller.setServices(postService, userService);
            controller.setPost(currentPost);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Post");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            controller.setDialogStage(stage);
            stage.showAndWait();
            
            if (controller.isPostCreated()) {
                // Refresh data
                currentPost = postService.getPost(currentPost.getPostId());
                setPost(currentPost);
            }
        } catch (IOException | SQLException e) {
            logger.error("Failed to open edit form", e);
        }
    }        

    private void loadComments() {
        if (currentPost == null) return;
        
        try {
            List<Comment> comments;
            if (userService != null && userService.isLoggedIn()) {
                comments = commentService.getCommentsByPost(currentPost.getPostId(), userService.getCurrentUser().getUserId());
            } else {
                comments = commentService.getCommentsByPost(currentPost.getPostId());
            }
            
            commentsListView.getItems().setAll(comments);
            
        } catch (SQLException e) {
            logger.error("Failed to load comments", e);
            commentStatusLabel.setText("Error loading comments");
        }
    }

    @FXML
    public void initialize() {
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        
        setupCommentsList();
        commentsListView.setFixedCellSize(70);
    }

    private void setupCommentsList() {
        commentsListView.setCellFactory(new Callback<ListView<Comment>, ListCell<Comment>>() {
            @Override
            public ListCell<Comment> call(ListView<Comment> param) {
                return new ListCell<Comment>() {
                    @Override
                    protected void updateItem(Comment comment, boolean empty) {
                        super.updateItem(comment, empty);
                        
                        if (empty || comment == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            VBox container = new VBox(2);
                            
                            HBox header = new HBox(10);
                            Label author = new Label(comment.getUsername());
                            author.setStyle("-fx-font-weight: bold;");
                            
                            String time = "Just now";
                            if (comment.getCreatedAt() != null) {
                                time = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
                            }
                            Label date = new Label(time);
                            date.setStyle("-fx-text-fill: #888; -fx-font-size: 0.9em;");
                            
                            header.getChildren().addAll(author, date);
                            
                            Label content = new Label(comment.getContent());
                            content.setWrapText(true);
                            
                            HBox actions = new HBox(10);
                            Button likeBtn = new Button("Like (" + comment.getLikesCount() + ")");
                            likeBtn.getStyleClass().add("like-button");
                            if (comment.isLikedByCurrentUser()) {
                                likeBtn.setStyle("-fx-background-color: #e0f7fa; -fx-text-fill: #006064;");
                                likeBtn.setText("Unlike (" + comment.getLikesCount() + ")");
                            }
                            
                            likeBtn.setOnAction(e -> handleLike(comment));
                            
                            actions.getChildren().add(likeBtn);
                            
                            container.getChildren().addAll(header, content, actions);
                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }
    
    private void handleLike(Comment comment) {
        if (userService == null || !userService.isLoggedIn()) {
            commentStatusLabel.setText("Login to like comments");
            return;
        }
        
        try {
            int userId = userService.getCurrentUser().getUserId();
            boolean success;
            if (comment.isLikedByCurrentUser()) {
                success = commentService.unlikeComment(userId, comment.getCommentId());
                if (success) {
                    comment.setLikesCount(comment.getLikesCount() - 1);
                    comment.setLikedByCurrentUser(false);
                    loadComments(); // Refresh from DB
                }
            } else {
                success = commentService.likeComment(userId, comment.getCommentId());
                if (success) {
                    comment.setLikesCount(comment.getLikesCount() + 1);
                    comment.setLikedByCurrentUser(true);
                    loadComments(); // Refresh from DB
                }
            }
            commentsListView.refresh(); // Update UI
        } catch (SQLException e) {
            logger.error("Like failed", e);
        }
    }

    @FXML
    private void handlePostComment() {
        if (userService == null || !userService.isLoggedIn()) {
            commentStatusLabel.setText("Please login to comment");
            return;
        }
        
        String content = newCommentArea.getText().trim();
        if (content.isEmpty()) return;
        
        Comment comment = new Comment(currentPost.getPostId(), userService.getCurrentUser().getUserId(), content);
        
        try {
            commentService.createComment(comment);
            newCommentArea.clear();
            loadComments();
            commentStatusLabel.setText("Comment posted!");
        } catch (Exception e) {
            logger.error("Failed to post comment", e);
            commentStatusLabel.setText("Failed: " + e.getMessage());
        }
    }
    
    private void updateUIState() {
        boolean loggedIn = userService != null && userService.isLoggedIn();
        newCommentArea.setDisable(!loggedIn);
        postCommentButton.setDisable(!loggedIn);
        if (!loggedIn) {
            newCommentArea.setPromptText("Log in to join the discussion...");
        } else {
            newCommentArea.setPromptText("Write a comment...");
        }
    }
}
