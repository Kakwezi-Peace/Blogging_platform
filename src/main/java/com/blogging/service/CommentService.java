package com.blogging.service;

import com.blogging.dao.CommentDAO;
import com.blogging.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;


 // Service layer for Comment management.
 // Implements validation and spam detection.

public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentDAO commentDAO;
    
    private static final int MIN_COMMENT_LENGTH = 1;
    private static final int MAX_COMMENT_LENGTH = 1000;
    private static final String[] SPAM_KEYWORDS = {"spam", "click here", "buy now", "free money"};

    public CommentService() {
        this.commentDAO = new CommentDAO();
    }


     // Create a new comment with validation.

    public Comment createComment(Comment comment) throws SQLException {
        validateComment(comment.getContent());
        
        if (isSpam(comment.getContent())) {
            logger.warn("Spam comment detected and blocked");
            throw new IllegalArgumentException("Comment appears to be spam");
        }
        
        Comment createdComment = commentDAO.create(comment);
        logger.info("Created comment with ID: {}", createdComment.getCommentId());
        return createdComment;
    }


     // Get comment by ID.

    public Comment getCommentById(int commentId) throws SQLException {
        return commentDAO.findById(commentId);
    }


     // Get all comments for a post.

    public List<Comment> getCommentsByPost(int postId) throws SQLException {
        return commentDAO.findByPost(postId);
    }


     // Get all comments for a post with user like status.

    public List<Comment> getCommentsByPost(int postId, int currentUserId) throws SQLException {
        List<Comment> comments = commentDAO.findByPost(postId);
        for (Comment comment : comments) {
            comment.setLikedByCurrentUser(commentDAO.hasUserLiked(currentUserId, comment.getCommentId()));
        }
        return comments;
    }


     // Get all comments by a user.

    public List<Comment> getCommentsByUser(int userId) throws SQLException {
        return commentDAO.findByUser(userId);
    }


     // Update comment.

    public boolean updateComment(Comment comment) throws SQLException {
        validateComment(comment.getContent());
        return commentDAO.update(comment);
    }


     // Delete comment.

    public boolean deleteComment(int commentId) throws SQLException {
        return commentDAO.delete(commentId);
    }


     // Get comment count for a post.

    public int getCommentCount(int postId) throws SQLException {
        return commentDAO.getCommentCountByPost(postId);
    }


     // Like a comment.

    public boolean likeComment(int userId, int commentId) throws SQLException {
        return commentDAO.likeComment(userId, commentId);
    }


     //Unlike a comment.

    public boolean unlikeComment(int userId, int commentId) throws SQLException {
        return commentDAO.unlikeComment(userId, commentId);
    }


     // Validate comment content.

    private void validateComment(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        if (content.length() < MIN_COMMENT_LENGTH) {
            throw new IllegalArgumentException("Comment is too short");
        }
        if (content.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException("Comment is too long (max " + MAX_COMMENT_LENGTH + " characters)");
        }
    }


     // Simple spam detection.

    private boolean isSpam(String content) {
        String lowerContent = content.toLowerCase();
        for (String keyword : SPAM_KEYWORDS) {
            if (lowerContent.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
