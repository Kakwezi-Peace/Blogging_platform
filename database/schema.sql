-- ============================================
-- Smart Blogging Platform Database Schema
-- Database: PostgreSQL
-- Normalization: 3NF (Third Normal Form)
-- ============================================

-- Drop existing database if exists and create new
DROP DATABASE IF EXISTS blogging_platform;
CREATE DATABASE blogging_platform;

-- Connect to the database
\c blogging_platform;

-- ============================================
-- Table: users
-- Description: Stores user account information
-- ============================================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_user_role CHECK (role IN ('USER', 'ADMIN'))
);

-- Index on username for fast lookup
CREATE INDEX idx_users_username ON users(username);

-- Index on email for fast lookup
CREATE INDEX idx_users_email ON users(email);

-- ============================================
-- Table: posts
-- Description: Stores blog post content
-- ============================================
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    view_count INTEGER DEFAULT 0,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_title_length CHECK (LENGTH(title) >= 5),
    CONSTRAINT chk_content_length CHECK (LENGTH(content) >= 10),
    CONSTRAINT chk_view_count CHECK (view_count >= 0)
);

-- Index on author_id for fast lookup of posts by author
CREATE INDEX idx_posts_user ON posts(user_id);

-- Index on title for text search
CREATE INDEX idx_posts_title ON posts(title);

-- Index on created_at for sorting by date
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);

-- Index on view_count for popular posts queries
CREATE INDEX idx_posts_view_count ON posts(view_count DESC);

-- Full-text search index on title and content
CREATE INDEX idx_posts_fulltext ON posts USING GIN(to_tsvector('english', title || ' ' || content));

-- ============================================
-- Table: comments
-- Description: Stores user comments on posts
-- ============================================
CREATE TABLE comments (
    comment_id SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) 
        REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_comment_length CHECK (LENGTH(content) >= 1)
);

-- Index on post_id for fast retrieval of comments for a post
CREATE INDEX idx_comments_post ON comments(post_id);

-- Index on user_id for fast retrieval of comments by user
CREATE INDEX idx_comments_user ON comments(user_id);

-- Index on created_at for sorting comments by date
CREATE INDEX idx_comments_created_at ON comments(created_at DESC);

-- ============================================
-- Table: comment_likes
-- Description: Stores likes on comments
-- ============================================
CREATE TABLE comment_likes (
    user_id INTEGER NOT NULL,
    comment_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, comment_id),
    CONSTRAINT fk_comment_likes_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_likes_comment FOREIGN KEY (comment_id) 
        REFERENCES comments(comment_id) ON DELETE CASCADE
);

-- Index on comment_id to count likes
CREATE INDEX idx_comment_likes_comment ON comment_likes(comment_id);

-- ============================================
-- Table: tags
-- Description: Stores unique tags for categorization
-- ============================================
CREATE TABLE tags (
    tag_id SERIAL PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT chk_tag_name_length CHECK (LENGTH(tag_name) >= 2)
);

-- Index on tag_name for fast lookup and uniqueness
CREATE INDEX idx_tags_name ON tags(tag_name);

-- ============================================
-- Table: post_tags
-- Description: Junction table for many-to-many relationship between posts and tags
-- ============================================
CREATE TABLE post_tags (
    post_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) 
        REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) 
        REFERENCES tags(tag_id) ON DELETE CASCADE
);

-- Index on tag_id for fast lookup of posts by tag
CREATE INDEX idx_post_tags_tag ON post_tags(tag_id);

-- Index on post_id for fast lookup of tags for a post
CREATE INDEX idx_post_tags_post ON post_tags(post_id);

-- ============================================
-- Table: reviews
-- Description: Stores user reviews and ratings for posts
-- ============================================
CREATE TABLE reviews (
    review_id SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    rating INTEGER NOT NULL,
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_post FOREIGN KEY (post_id) 
        REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_rating_range CHECK (rating >= 1 AND rating <= 5),
    CONSTRAINT uq_user_post_review UNIQUE (user_id, post_id)
);

-- Index on post_id for fast retrieval of reviews for a post
CREATE INDEX idx_reviews_post ON reviews(post_id);

-- Index on user_id for fast retrieval of reviews by user
CREATE INDEX idx_reviews_user ON reviews(user_id);

-- Index on rating for filtering by rating
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- ============================================
-- Views for Common Queries
-- ============================================

-- View: Post details with author information
CREATE VIEW vw_post_details AS
SELECT 
    p.post_id,
    p.title,
    p.content,
    p.created_at,
    p.updated_at,
    p.view_count,
    u.user_id AS author_id,
    u.username AS author_name,
    u.email AS author_email
FROM posts p
JOIN users u ON p.user_id = u.user_id;

-- View: Post statistics
CREATE VIEW vw_post_statistics AS
SELECT 
    p.post_id,
    p.title,
    p.view_count,
    COUNT(DISTINCT c.comment_id) AS comment_count,
    COUNT(DISTINCT r.review_id) AS review_count,
    COALESCE(AVG(r.rating), 0) AS average_rating
FROM posts p
LEFT JOIN comments c ON p.post_id = c.post_id
LEFT JOIN reviews r ON p.post_id = r.post_id
GROUP BY p.post_id, p.title, p.view_count;

-- ============================================
-- Functions and Triggers
-- ============================================

-- Function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update updated_at on posts
CREATE TRIGGER trg_posts_updated_at
BEFORE UPDATE ON posts
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Summary of Database Objects
-- ============================================

-- Display all tables
SELECT 'Tables Created:' AS info;
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Display all indexes
SELECT 'Indexes Created:' AS info;
SELECT 
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- Display all foreign keys
SELECT 'Foreign Keys Created:' AS info;
SELECT
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'public'
ORDER BY tc.table_name;
