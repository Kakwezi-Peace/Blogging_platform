package com.blogging.dao;

import com.blogging.model.Review;
import com.blogging.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Review entity.
 * Handles all database operations for reviews and ratings.
 */
public class ReviewDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReviewDAO.class);

    /**
     * Create a new review.
     */
    public Review create(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (post_id, user_id, rating, review_text) " +
                    "VALUES (?, ?, ?, ?) RETURNING review_id, created_at";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, review.getPostId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    review.setReviewId(rs.getInt("review_id"));
                    review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            
            logger.info("Created review with ID: {}", review.getReviewId());
            return review;
        }
    }

    /**
     * Find a review by ID.
     */
    public Review findById(int reviewId) throws SQLException {
        String sql = "SELECT r.*, u.username, p.title AS post_title " +
                    "FROM reviews r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN posts p ON r.post_id = p.post_id " +
                    "WHERE r.review_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reviewId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }
        return null;
    }

    /**
     * Find all reviews for a specific post.
     */
    public List<Review> findByPost(int postId) throws SQLException {
        String sql = "SELECT r.*, u.username, p.title AS post_title " +
                    "FROM reviews r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN posts p ON r.post_id = p.post_id " +
                    "WHERE r.post_id = ? " +
                    "ORDER BY r.created_at DESC";
        
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        return reviews;
    }

    /**
     * Find all reviews by a specific user.
     */
    public List<Review> findByUser(int userId) throws SQLException {
        String sql = "SELECT r.*, u.username, p.title AS post_title " +
                    "FROM reviews r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN posts p ON r.post_id = p.post_id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.created_at DESC";
        
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        return reviews;
    }

    /**
     * Update an existing review.
     */
    public boolean update(Review review) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, review_text = ? WHERE review_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setInt(3, review.getReviewId());
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Updated review ID: {}", review.getReviewId());
            return rowsAffected > 0;
        }
    }

    /**
     * Delete a review by ID.
     */
    public boolean delete(int reviewId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reviewId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Deleted review ID: {}", reviewId);
            return rowsAffected > 0;
        }
    }

    /**
     * Get average rating for a post.
     */
    public double getAverageRating(int postId) throws SQLException {
        String sql = "SELECT COALESCE(AVG(rating), 0) as avg_rating FROM reviews WHERE post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }
        return 0.0;
    }

    /**
     * Get review count for a post.
     */
    public int getReviewCountByPost(int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reviews WHERE post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Check if user has already reviewed a post.
     */
    public boolean hasUserReviewedPost(int userId, int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Map ResultSet to Review object.
     */
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setPostId(rs.getInt("post_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("review_text"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        review.setUsername(rs.getString("username"));
        review.setPostTitle(rs.getString("post_title"));
        return review;
    }
}
