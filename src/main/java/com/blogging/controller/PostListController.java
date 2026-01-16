package com.blogging.controller;

import com.blogging.model.Post;
import com.blogging.model.User;
import com.blogging.service.PostService;
import com.blogging.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;


 //Controller for post list view.
 
public class PostListController {
    private static final Logger logger = LoggerFactory.getLogger(PostListController.class);

    @FXML private TableView<Post> postTable;
    @FXML private TableColumn<Post, Integer> idColumn;
    @FXML private TableColumn<Post, String> titleColumn;
    @FXML private TableColumn<Post, String> authorColumn;
    @FXML private TableColumn<Post, Integer> viewsColumn;
    @FXML private TableColumn<Post, String> dateColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label statusLabel;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageLabel;

    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private PostService postService;
    private UserService userService;
    private final ObservableList<Post> postList = FXCollections.observableArrayList();
    
    // Pagination
    private int currentPage = 1;
    private static final int POSTS_PER_PAGE = 10;
    private int totalPosts = 0;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSortComboBox();
        setupButtons();
    }

    public void setServices(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
        updateButtonState();
    }

    private void setupTableColumns() {
        // Fix ID to be sequential 1, 2, 3 based on row index
        idColumn.setCellValueFactory(cellData -> {
            int index = postTable.getItems().indexOf(cellData.getValue());
            return new javafx.beans.property.SimpleObjectProperty<>(index < 0 ? 1 : index + 1);
        });
            
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        viewsColumn.setCellValueFactory(new PropertyValueFactory<>("viewCount"));
        
        dateColumn.setCellValueFactory(cellData -> {
            java.time.LocalDateTime createdAt = cellData.getValue().getCreatedAt();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            if (createdAt != null) {
                return new javafx.beans.property.SimpleStringProperty(createdAt.format(formatter));
            } else {
                // Fallback to current time if null to avoid 'Unknown Date' if user wants data displayed
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        postTable.setItems(postList);
        
        // Double-click to view post details
        postTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && postTable.getSelectionModel().getSelectedItem() != null) {
                viewPostDetails(postTable.getSelectionModel().getSelectedItem());
            }
            updateButtonState();
        });
        
        postTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonState();
        });
    }

    private void setupButtons() {
        // Initial state
        if (createButton != null) createButton.setDisable(true); 
        if (editButton != null) editButton.setDisable(true);
        if (deleteButton != null) deleteButton.setDisable(true);
    }
    
    private void updateButtonState() {
        if (userService == null || !userService.isLoggedIn()) {
            if (createButton != null) createButton.setDisable(true);
            if (editButton != null) editButton.setDisable(true);
            if (deleteButton != null) deleteButton.setDisable(true);
            return;
        }
        
        // User logged in
        if (createButton != null) createButton.setDisable(false);
        
        Post selectedPost = postTable.getSelectionModel().getSelectedItem();
        boolean canEdit = false;
        
        if (selectedPost != null) {
            User currentUser = userService.getCurrentUser();
            // Can edit if author OR Admin
            if (currentUser.getUserId() == selectedPost.getUserId() || "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                canEdit = true;
            }
        }
        
        if (editButton != null) {
            editButton.setDisable(!canEdit);
            editButton.setOpacity(canEdit ? 1.0 : 0.5);
        }
        if (deleteButton != null) {
            deleteButton.setDisable(!canEdit);
            deleteButton.setOpacity(canEdit ? 1.0 : 0.5);
        }
    }

    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("Date", "Views", "Title"));
        sortComboBox.setValue("Date");
    }

    public void loadPosts() {
        try {
            totalPosts = postService.getTotalPostCount();
            List<Post> posts = postService.getPosts(currentPage, POSTS_PER_PAGE);
            postList.setAll(posts);
            
            updatePaginationControls();
            statusLabel.setText(String.format("Showing page %d of %d (%d total posts)", 
                currentPage, getTotalPages(), totalPosts));
        } catch (SQLException e) {
            logger.error("Failed to load posts", e);
            showError("Error", "Failed to load posts: " + e.getMessage());
        }
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadPosts();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < getTotalPages()) {
            currentPage++;
            loadPosts();
        }
    }
    
    @FXML
    private void handleCreate() {
        // Open PostFormView in Create mode
        openPostForm(null);
    }
    
    @FXML
    private void handleEdit() {
        Post selectedPost = postTable.getSelectionModel().getSelectedItem();
        if (selectedPost != null) {
            openPostForm(selectedPost);
        }
    }
    
    @FXML
    private void handleDelete() {
        Post selectedPost = postTable.getSelectionModel().getSelectedItem();
        if (selectedPost != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Post");
            alert.setHeaderText("Are you sure you want to delete this post?");
            alert.setContentText(selectedPost.getTitle());

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    postService.deletePost(selectedPost.getPostId());
                    loadPosts();
                    statusLabel.setText("Post deleted successfully.");
                } catch (SQLException e) {
                    logger.error("Failed to delete post", e);
                    showError("Error", "Failed to delete post: " + e.getMessage());
                }
            }
        }
    }
    
    private void openPostForm(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostFormView.fxml"));
            Parent form = loader.load();
            
            PostFormController controller = loader.getController();
            controller.setServices(postService, userService);
            controller.setPost(post);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(post == null ? "Create New Post" : "Edit Post");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(postTable.getScene().getWindow());
            dialogStage.setScene(new Scene(form));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            if (controller.isPostCreated()) {
                loadPosts(); // Refresh list
                statusLabel.setText(post == null ? "Post created." : "Post updated.");
            }
            
        } catch (IOException e) {
             logger.error("Failed to open post form", e);
             showError("Error", "Failed to open form: " + e.getMessage());
        }
    }

    private int getTotalPages() {
        int pages = (int) Math.ceil((double) totalPosts / POSTS_PER_PAGE);
        return pages == 0 ? 1 : pages;
    }

    private void updatePaginationControls() {
        if (prevPageButton != null && nextPageButton != null && pageLabel != null) {
            prevPageButton.setDisable(currentPage <= 1);
            nextPageButton.setDisable(currentPage >= getTotalPages());
            pageLabel.setText(String.format("Page %d of %d", currentPage, getTotalPages()));
        }
    }

    public void loadUserPosts(int userId) {
        try {
            List<Post> posts = postService.getPosts(1, 100).stream()
                .filter(p -> p.getUserId() == userId)
                .collect(java.util.stream.Collectors.toList());
            postList.setAll(posts);
            statusLabel.setText("Loaded " + posts.size() + " of your posts");
            updateButtonState();
        } catch (SQLException e) {
            logger.error("Failed to load user posts", e);
            showError("Error", "Failed to load your posts: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPosts();
            return;
        }

        try {
            List<Post> results = postService.searchPosts(keyword);
            postList.setAll(results);
            statusLabel.setText("Found " + results.size() + " posts matching '" + keyword + "'");
        } catch (SQLException e) {
            logger.error("Search failed", e);
            showError("Error", "Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        handleRefresh();
    }

    @FXML
    private void handleSort() {
        String sortBy = sortComboBox.getValue();
        try {
            List<Post> sorted = postService.getSortedPosts(100, sortBy);
            postList.setAll(sorted);
            statusLabel.setText("Sorted by " + sortBy);
        } catch (SQLException e) {
            logger.error("Sort failed", e);
            showError("Error", "Sort failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadPosts();
    }

    private void viewPostDetails(Post post) {
        try {
            postService.incrementViewCount(post.getPostId());
            handleRefresh(); // Update view count in list
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostDetailView.fxml"));
            Parent root = loader.load();
            
            PostDetailController controller = loader.getController();
            controller.setServices(postService, userService);
            // Fetch fresh post to trigger cache logic and update correct view count
            Post freshPost = postService.getPost(post.getPostId());
            controller.setPost(freshPost);
            
            Stage stage = new Stage();
            stage.setTitle(post.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();

            // Refresh the specific item in the list or the whole list to show new view count
            handleRefresh(); 
            
        } catch (IOException | SQLException e) {
             logger.error("Failed to view post", e);
             showError("Error", "Failed to load post details");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
