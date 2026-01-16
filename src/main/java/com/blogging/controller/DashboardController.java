package com.blogging.controller;

import com.blogging.dao.PostDAO;
import com.blogging.service.PostService;
import com.blogging.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

/**
 * Controller for dashboard view.
 */
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML private Label totalPostsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label cacheHitsLabel;
    @FXML private Label cacheMissesLabel;
    @FXML private Label hitRateLabel;
    @FXML private Label welcomeMessageLabel;

    private PostService postService;
    private UserService userService;

    public void setServices(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public void loadDashboardData() {
        try {
            // Load post count
            int totalPosts = postService.getTotalPostCount();
            totalPostsLabel.setText(String.valueOf(totalPosts));

            // Load user count
            int totalUsers = userService.getAllUsers().size();
            totalUsersLabel.setText(String.valueOf(totalUsers));

            // Load cache statistics
            Map<String, Object> cacheStats = postService.getCacheStats();
            cacheHitsLabel.setText(String.valueOf(cacheStats.get("cacheHits")));
            cacheMissesLabel.setText(String.valueOf(cacheStats.get("cacheMisses")));
            hitRateLabel.setText(String.valueOf(cacheStats.get("hitRate")));

            // Welcome message
            if (userService.isLoggedIn()) {
                welcomeMessageLabel.setText("Welcome back, " + userService.getCurrentUser().getUsername() + "!");
            } else {
                welcomeMessageLabel.setText("Welcome to Smart Blogging Platform!");
            }

        } catch (SQLException e) {
            logger.error("Failed to load dashboard data", e);
        }
    }
}
