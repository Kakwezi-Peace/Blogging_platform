package com.blogging.dao;

import com.blogging.model.Tag;
import com.blogging.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


 // Data Access Object for Tag entity.
 //  Handles all database operations for tags and post-tag associations.

public class TagDAO {
    private static final Logger logger = LoggerFactory.getLogger(TagDAO.class);


     //  Create a new tag.

    public Tag create(Tag tag) throws SQLException {
        String sql = "INSERT INTO tags (tag_name) VALUES (?) RETURNING tag_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tag.getName().toLowerCase().trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tag.setTagId(rs.getInt("tag_id"));
                }
            }
            
            logger.info("Created tag with ID: {}", tag.getTagId());
            return tag;
        }
    }


     // Find a tag by ID.

    public Tag findById(int tagId) throws SQLException {
        String sql = "SELECT * FROM tags WHERE tag_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tagId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTag(rs);
                }
            }
        }
        return null;
    }


     // Find a tag by name.

    public Tag findByName(String tagName) throws SQLException {
        String sql = "SELECT * FROM tags WHERE tag_name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tagName.toLowerCase().trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTag(rs);
                }
            }
        }
        return null;
    }


     // Find all tags.

    public List<Tag> findAll() throws SQLException {
        String sql = "SELECT * FROM tags ORDER BY tag_name";
        List<Tag> tags = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tags.add(mapResultSetToTag(rs));
            }
        }
        return tags;
    }


     //  Find all tags for a specific post.

    public List<Tag> findTagsByPost(int postId) throws SQLException {
        String sql = "SELECT t.* FROM tags t " +
                    "JOIN post_tags pt ON t.tag_id = pt.tag_id " +
                    "WHERE pt.post_id = ? " +
                    "ORDER BY t.tag_name";
        
        List<Tag> tags = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(mapResultSetToTag(rs));
                }
            }
        }
        return tags;
    }


     // Add a tag to a post.

    public boolean addTagToPost(int postId, int tagId) throws SQLException {
        String sql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            stmt.setInt(2, tagId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Added tag {} to post {}", tagId, postId);
            return rowsAffected > 0;
        }
    }


      // Remove a tag from a post.

    public boolean removeTagFromPost(int postId, int tagId) throws SQLException {
        String sql = "DELETE FROM post_tags WHERE post_id = ? AND tag_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            stmt.setInt(2, tagId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Removed tag {} from post {}", tagId, postId);
            return rowsAffected > 0;
        }
    }


     //  Delete a tag (and all its associations).

    public boolean delete(int tagId) throws SQLException {
        String sql = "DELETE FROM tags WHERE tag_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tagId);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Deleted tag ID: {}", tagId);
            return rowsAffected > 0;
        }
    }


     // Get popular tags with post counts.

    public List<Tag> getPopularTags(int limit) throws SQLException {
        String sql = "SELECT t.*, COUNT(pt.post_id) as post_count " +
                    "FROM tags t " +
                    "LEFT JOIN post_tags pt ON t.tag_id = pt.tag_id " +
                    "GROUP BY t.tag_id " +
                    "ORDER BY post_count DESC " +
                    "LIMIT ?";
        
        List<Tag> tags = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(mapResultSetToTag(rs));
                }
            }
        }
        return tags;
    }


     // Map ResultSet to Tag object.

    private Tag mapResultSetToTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setTagId(rs.getInt("tag_id"));
        tag.setName(rs.getString("tag_name"));
        return tag;
    }
}
