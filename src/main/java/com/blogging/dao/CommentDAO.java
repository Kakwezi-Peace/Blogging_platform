package com.blogging.dao;

import com.blogging.model.Comment;
import com.blogging.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private static final Logger logger = LoggerFactory.getLogger(CommentDAO.class);

    public Comment create(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?) " +
                    "RETURNING comment_id, created_at";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comment.getPostId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getContent());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comment.setCommentId(rs.getInt("comment_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        comment.setCreatedAt(ts.toLocalDateTime());
                    }
                }
            }
            
            logger.info("Created comment with ID: {}", comment.getCommentId());
            return comment;
        }
    }

    public Comment findById(int commentId) throws SQLException {
        String sql = "SELECT c.*, u.username, p.title AS post_title " +
                    "FROM comments c " +
                    "JOIN users u ON c.user_id = u.user_id " +
                    "JOIN posts p ON c.post_id = p.post_id " +
                    "WHERE c.comment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }
        }
        return null;
    }

    public List<Comment> findByPost(int postId) throws SQLException {
        String sql = "SELECT c.*, u.username, p.title AS post_title, " +
                    "(SELECT COUNT(*) FROM comment_likes cl WHERE cl.comment_id = c.comment_id) as like_count " +
                    "FROM comments c " +
                    "JOIN users u ON c.user_id = u.user_id " +
                    "JOIN posts p ON c.post_id = p.post_id " +
                    "WHERE c.post_id = ? " +
                    "ORDER BY c.created_at ASC";
        
        List<Comment> comments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = mapResultSetToComment(rs);
                    comment.setLikesCount(rs.getInt("like_count"));
                    comments.add(comment);
                }
            }
        }
        return comments;
    }

    /**
     * Find all comments by a specific user.
     */
    public List<Comment> findByUser(int userId) throws SQLException {
        String sql = "SELECT c.*, u.username, p.title AS post_title " +
                    "FROM comments c " +
                    "JOIN users u ON c.user_id = u.user_id " +
                    "JOIN posts p ON c.post_id = p.post_id " +
                    "WHERE c.user_id = ? " +
                    "ORDER BY c.created_at DESC";
        
        List<Comment> comments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        }
        return comments;
    }

    /**
     * Update an existing comment.
     */
    public boolean update(Comment comment) throws SQLException {
        String sql = "UPDATE comments SET content = ? WHERE comment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, comment.getContent());
            stmt.setInt(2, comment.getCommentId());
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Updated comment ID: {}", comment.getCommentId());
            return rowsAffected > 0;
        }
    }

    /**
     * Delete a comment by ID.
     */
    public boolean delete(int commentId) throws SQLException {
        String sql = "DELETE FROM comments WHERE comment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commentId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Deleted comment ID: {}", commentId);
            return rowsAffected > 0;
        }
    }

    /**
     * Get comment count for a post.
     */
    public int getCommentCountByPost(int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?";
        
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
     * Like a comment.
     */
    public boolean likeComment(int userId, int commentId) throws SQLException {
        String sql = "INSERT INTO comment_likes (user_id, comment_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, commentId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Unlike a comment.
     */
    public boolean unlikeComment(int userId, int commentId) throws SQLException {
        String sql = "DELETE FROM comment_likes WHERE user_id = ? AND comment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, commentId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Check if user has liked a comment.
     */
    public boolean hasUserLiked(int userId, int commentId) throws SQLException {
        String sql = "SELECT 1 FROM comment_likes WHERE user_id = ? AND comment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, commentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Map ResultSet to Comment object.
     */
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setContent(rs.getString("content"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            comment.setCreatedAt(ts.toLocalDateTime());
        }
        comment.setUsername(rs.getString("username"));
        comment.setPostTitle(rs.getString("post_title"));
        return comment;
    }
}
