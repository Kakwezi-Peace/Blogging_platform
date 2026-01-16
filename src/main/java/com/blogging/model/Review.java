
package com.blogging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int reviewId;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "user_id")
    private int userId;

    @Column(nullable = false)
    private int rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Additional fields for joined queries
    @Transient
    private String username;
    @Transient
    private String postTitle;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Review() {
    }

    public Review(int reviewId, int postId, int userId, int rating,
                  String comment, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.postId = postId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Review(int postId, int userId, int rating, String comment) {
        this.postId = postId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return reviewId == review.reviewId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                '}';
    }
}
