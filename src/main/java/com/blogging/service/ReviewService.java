package com.blogging.service;

import com.blogging.dao.ReviewDAO;
import com.blogging.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for Review management.
 * Implements validation and rating calculations.
 */
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
    }

    /**
     * Create a new review with validation.
     */
    public Review createReview(Review review) throws SQLException {
        validateRating(review.getRating());
        
        // Check if user has already reviewed this post
        if (reviewDAO.hasUserReviewedPost(review.getUserId(), review.getPostId())) {
            throw new IllegalArgumentException("You have already reviewed this post");
        }
        
        Review createdReview = reviewDAO.create(review);
        logger.info("Created review with ID: {} for post: {}", 
                   createdReview.getReviewId(), createdReview.getPostId());
        return createdReview;
    }

    /**
     * Get review by ID.
     */
    public Review getReviewById(int reviewId) throws SQLException {
        return reviewDAO.findById(reviewId);
    }

    /**
     * Get all reviews for a post.
     */
    public List<Review> getReviewsByPost(int postId) throws SQLException {
        return reviewDAO.findByPost(postId);
    }

    /**
     * Get all reviews by a user.
     */
    public List<Review> getReviewsByUser(int userId) throws SQLException {
        return reviewDAO.findByUser(userId);
    }

    /**
     * Update review.
     */
    public void addReview(int postId, int userId, int rating, String comment) throws SQLException {
        Review review = new Review();
        review.setPostId(postId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        
        reviewDAO.create(review);
    }
    
    public List<Review> getReviewsForPost(int postId) throws SQLException {
        return reviewDAO.findByPost(postId);
    }
    
    public double getAverageRating(int postId) throws SQLException {
        return reviewDAO.getAverageRating(postId);
    }
    
    public boolean hasUserReviewed(int userId, int postId) throws SQLException {
        return reviewDAO.hasUserReviewedPost(userId, postId);
    }

    /**
     * Get rating statistics for a post.
     */
    public RatingStats getRatingStats(int postId) throws SQLException {
        List<Review> reviews = reviewDAO.findByPost(postId);
        
        if (reviews.isEmpty()) {
            return new RatingStats(0, 0, 0, 0, 0, 0);
        }
        
        int[] ratingCounts = new int[6]; // Index 0 unused, 1-5 for ratings
        for (Review review : reviews) {
            ratingCounts[review.getRating()]++;
        }
        
        double average = reviewDAO.getAverageRating(postId);
        
        return new RatingStats(
            reviews.size(),
            average,
            ratingCounts[5],
            ratingCounts[4],
            ratingCounts[3],
            ratingCounts[2] + ratingCounts[1]
        );
    }

    /**
     * Validate rating value.
     */
    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    /**
     * Rating statistics class.
     */
    public static class RatingStats {
        private final int totalReviews;
        private final double averageRating;
        private final int fiveStars;
        private final int fourStars;
        private final int threeStars;
        private final int twoOrLessStars;

        public RatingStats(int totalReviews, double averageRating, int fiveStars, 
                          int fourStars, int threeStars, int twoOrLessStars) {
            this.totalReviews = totalReviews;
            this.averageRating = averageRating;
            this.fiveStars = fiveStars;
            this.fourStars = fourStars;
            this.threeStars = threeStars;
            this.twoOrLessStars = twoOrLessStars;
        }

        public int getTotalReviews() { return totalReviews; }
        public double getAverageRating() { return averageRating; }
        public int getFiveStars() { return fiveStars; }
        public int getFourStars() { return fourStars; }
        public int getThreeStars() { return threeStars; }
        public int getTwoOrLessStars() { return twoOrLessStars; }
    }
}
