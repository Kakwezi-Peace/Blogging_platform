package com.blogging.service;

import com.blogging.dao.PostDAO;
import com.blogging.dao.TagDAO;
import com.blogging.model.Post;
import com.blogging.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostDAO postDAO;
    private final TagDAO tagDAO;
    
    // In-memory cache for frequently accessed posts
    private final Map<Integer, Post> postCache;
    private final Map<Integer, Long> cacheTimestamps;
    private static final long CACHE_TTL = 300000; // 5 minutes
    
    // Cache statistics
    private int cacheHits = 0;
    private int cacheMisses = 0;

    public PostService() {
        this.postDAO = new PostDAO();
        this.tagDAO = new TagDAO();
        this.postCache = new HashMap<>();
        this.cacheTimestamps = new HashMap<>();
    }

    public Post createPost(Post post, List<String> tagNames) throws SQLException {
        // Create the post
        Post createdPost = postDAO.create(post);
        
        // Add tags
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagDAO.findByName(tagName);
                if (tag == null) {
                    tag = tagDAO.create(new Tag(tagName));
                }
                tagDAO.addTagToPost(createdPost.getPostId(), tag.getTagId());
            }
        }
        
        logger.info("Created post with ID: {} and {} tags", createdPost.getPostId(), tagNames.size());
        return createdPost;
    }

    public Post getPost(int postId) throws SQLException {
        // Check cache first
        if (isCacheValid(postId)) {
            cacheHits++;
            logger.debug("Cache hit for post ID: {}", postId);
            return postCache.get(postId);
        }
        
        // Cache miss - fetch from database
        cacheMisses++;
        logger.debug("Cache miss for post ID: {}", postId);
        Post post = postDAO.findById(postId);
        
        if (post != null) {
            // Update cache
            postCache.put(postId, post);
            cacheTimestamps.put(postId, System.currentTimeMillis());
        }
        
        return post;
    }


     //  Search posts by keyword.

    public List<Post> searchPosts(String keyword) throws SQLException {
        long startTime = System.currentTimeMillis();
        List<Post> results = postDAO.search(keyword);
        long endTime = System.currentTimeMillis();
        
        logger.info("Search for '{}' returned {} results in {}ms", 
                   keyword, results.size(), (endTime - startTime));
        return results;
    }


     //  Get posts by tag.

    public List<Post> getPostsByTag(String tagName) throws SQLException {
        Tag tag = tagDAO.findByName(tagName);
        if (tag == null) {
            return new ArrayList<>();
        }
        return postDAO.findByTag(tag.getTagId());
    }


     // Get posts with pagination.

    public List<Post> getPosts(int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        return postDAO.findAll(pageSize, offset);
    }


     // Get sorted posts using QuickSort algorithm.

    public List<Post> getSortedPosts(int limit, String sortBy) throws SQLException {
        List<Post> posts = postDAO.findAll(limit, 0);
        
        long startTime = System.currentTimeMillis();
        quickSort(posts, 0, posts.size() - 1, sortBy);
        long endTime = System.currentTimeMillis();
        
        logger.info("Sorted {} posts by {} in {}ms", posts.size(), sortBy, (endTime - startTime));
        return posts;
    }


     // QuickSort implementation for posts.

    private void quickSort(List<Post> posts, int low, int high, String sortBy) {
        if (low < high) {
            int pi = partition(posts, low, high, sortBy);
            quickSort(posts, low, pi - 1, sortBy);
            quickSort(posts, pi + 1, high, sortBy);
        }
    }

    private int partition(List<Post> posts, int low, int high, String sortBy) {
        Post pivot = posts.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (compare(posts.get(j), pivot, sortBy) <= 0) {
                i++;
                Collections.swap(posts, i, j);
            }
        }
        Collections.swap(posts, i + 1, high);
        return i + 1;
    }

    private int compare(Post p1, Post p2, String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "views":
                return Integer.compare(p2.getViewCount(), p1.getViewCount()); // Descending
            case "title":
                return p1.getTitle().compareToIgnoreCase(p2.getTitle());
            case "date":
            default:
                LocalDateTime d1 = p1.getCreatedAt();
                LocalDateTime d2 = p2.getCreatedAt();
                if (d1 == null && d2 == null) return 0;
                if (d1 == null) return 1;
                if (d2 == null) return -1;
                return d2.compareTo(d1); // Most recent first
        }
    }


     // Get most viewed posts.

    public List<Post> getMostViewedPosts(int limit) throws SQLException {
        return postDAO.getMostViewed(limit);
    }


     // Update post.

    public void updatePost(Post post) throws SQLException {
        boolean updated = postDAO.update(post);
        if (updated) {
            // Invalidate cache
            postCache.remove(post.getPostId());
            cacheTimestamps.remove(post.getPostId());
        }
    }


     //  Delete post.

    public boolean deletePost(int postId) throws SQLException {
        boolean deleted = postDAO.delete(postId);
        if (deleted) {
            // Invalidate cache
            postCache.remove(postId);
            cacheTimestamps.remove(postId);
        }
        return deleted;
    }


     // Increment view count.

    public void incrementViewCount(int postId) throws SQLException {
        postDAO.incrementViewCount(postId);
        
        // Update cache instead of removing to allow hits
        if (postCache.containsKey(postId)) {
            Post cachedPost = postCache.get(postId);
            cachedPost.setViewCount(cachedPost.getViewCount() + 1);
            // Optionally update timestamp to keep it fresh
            cacheTimestamps.put(postId, System.currentTimeMillis());
            logger.debug("Updated cache for post ID: {} (new views: {})", postId, cachedPost.getViewCount());
        }
    }


     // Get total post count.

    public int getTotalPostCount() throws SQLException {
        return postDAO.getTotalCount();
    }


     // Clear cache.

    public void clearCache() {
        postCache.clear();
        cacheTimestamps.clear();
        logger.info("Cache cleared");
    }


     // Get cache statistics.

    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", postCache.size());
        stats.put("cacheHits", cacheHits);
        stats.put("cacheMisses", cacheMisses);
        
        int totalRequests = cacheHits + cacheMisses;
        double hitRate = totalRequests > 0 ? (double) cacheHits / totalRequests * 100 : 0;
        stats.put("hitRate", String.format("%.2f%%", hitRate));
        
        return stats;
    }


     // Check if cached post is still valid.

    private boolean isCacheValid(int postId) {
        if (!postCache.containsKey(postId)) {
            return false;
        }
        
        Long timestamp = cacheTimestamps.get(postId);
        if (timestamp == null) {
            return false;
        }
        
        return (System.currentTimeMillis() - timestamp) < CACHE_TTL;
    }
}
