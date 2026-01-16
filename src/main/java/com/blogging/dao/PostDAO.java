package com.blogging.dao;

import com.blogging.model.Post;
import com.blogging.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    private static final Logger logger = LoggerFactory.getLogger(PostDAO.class);

    public Post create(Post post) throws SQLException {
        String sql = "INSERT INTO posts (user_id, title, content, created_at, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING post_id, created_at, updated_at, view_count";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    post.setPostId(rs.getInt("post_id"));
                    Timestamp cat = rs.getTimestamp("created_at");
                    if (cat != null) post.setCreatedAt(cat.toLocalDateTime());
                    Timestamp uat = rs.getTimestamp("updated_at");
                    if (uat != null) post.setUpdatedAt(uat.toLocalDateTime());
                    post.setViewCount(rs.getInt("view_count"));
                }
            }
            
            logger.info("Created post with ID: {}", post.getPostId());
            return post;
        }
    }

    public Post findById(int postId) throws SQLException {
        String sql = "SELECT p.*, u.username as author_name " +
                    "FROM posts p " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "WHERE p.post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPost(rs);
                }
            }
        }
        return null;
    }


     // Get posts by user (author).

    public List<Post> findByUser(int userId) throws SQLException {
        String sql = "SELECT p.*, u.username as author_name " +
                    "FROM posts p " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "WHERE p.user_id = ? " +
                    "ORDER BY p.created_at DESC";
        
        List<Post> posts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        }
        return posts;
    }


     // Search posts by keyword (title or properties).

    public List<Post> search(String keyword) throws SQLException {
        String sql = "SELECT p.*, u.username as author_name " +
                    "FROM posts p " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "WHERE LOWER(p.title) LIKE ? OR LOWER(p.content) LIKE ? " +
                    "ORDER BY p.created_at DESC";
        
        List<Post> posts = new ArrayList<>();
        String searchPattern = "%" + keyword.toLowerCase().trim() + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        }
        return posts;
    }


     // Get posts by tag.

    public List<Post> findByTag(int tagId) throws SQLException {
        String sql = "SELECT p.*, u.username as author_name " +
                    "FROM posts p " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "JOIN post_tags pt ON p.post_id = pt.post_id " +
                    "WHERE pt.tag_id = ? " +
                    "ORDER BY p.created_at DESC";
        
        List<Post> posts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tagId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        }
        return posts;
    }


     // Get all posts with pagination.

    public List<Post> findAll(int limit, int offset) throws SQLException {
        String sql = "SELECT p.*, u.username as author_name " +
                    "FROM posts p " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "ORDER BY p.created_at DESC " +
                    "LIMIT ? OFFSET ?";
        
        List<Post> posts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        }
        return posts;
    }


     // Update an existing post.

    public boolean update(Post post) throws SQLException {
        String sql = "UPDATE posts SET title = ?, content = ?, updated_at = CURRENT_TIMESTAMP WHERE post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, post.getPostId());
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Updated post ID: {}", post.getPostId());
            return rowsAffected > 0;
        }
    }




      // Delete a post by ID.

    public boolean delete(int postId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Delete from post_tags
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM post_tags WHERE post_id = ?")) {
                stmt.setInt(1, postId);
                stmt.executeUpdate();
            }


            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM comments WHERE post_id = ?")) {
                stmt.setInt(1, postId);
                stmt.executeUpdate();
            }

            // 3. Delete the post
            int rowsAffected;
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM posts WHERE post_id = ?")) {
                stmt.setInt(1, postId);
                rowsAffected = stmt.executeUpdate();
            }

            conn.commit();
            logger.info("Deleted post ID: {}", postId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Failed to rollback transaction", ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Failed to close connection", e);
                }
            }
        }
    }


     // Increment view count for a post.

    public void incrementViewCount(int postId) throws SQLException {
        String sql = "UPDATE posts SET view_count = view_count + 1 WHERE post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            stmt.executeUpdate();
        }
    }


     // Get total count of posts.

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM posts";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


     // Get most viewed posts.

    public List<Post> getMostViewed(int limit) throws SQLException {
        String sql = "SELECT p.*, u.username AS author_name " +
                    "FROM posts p " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "ORDER BY p.view_count DESC " +
                    "LIMIT ?";
        
        List<Post> posts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapResultSetToPost(rs));
                }
            }
        }
        return posts;
    }


     // Map ResultSet to Post object.

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getInt("post_id"));
        post.setUserId(rs.getInt("user_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            post.setCreatedAt(createdAt.toLocalDateTime());
        }

        java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            post.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        post.setViewCount(rs.getInt("view_count"));
        post.setAuthorName(rs.getString("author_name"));
        return post;
    }
}
