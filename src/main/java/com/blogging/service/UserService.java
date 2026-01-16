package com.blogging.service;

import com.blogging.dao.UserDAO;
import com.blogging.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service layer for User management.
 * Implements validation and authentication logic.
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private User currentUser; // Simple session management

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Register a new user.
     */
    public User registerUser(String username, String email, String password) throws SQLException {
        // Validate input
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
        
        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Hash password (simplified - in production use BCrypt)
        String passwordHash = hashPassword(password);
        
        // Create user
        User user = new User(username, email, passwordHash);
        User createdUser = userDAO.create(user);
        
        logger.info("Registered new user: {}", username);
        return createdUser;
    }

    /**
     * Authenticate user (simplified).
     */
    public void login(String username, String password) throws SQLException, IllegalArgumentException {
        User user = userDAO.findByUsername(username);
        
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Hash password before comparison
        String hashedInput = hashPassword(password);
        if (!hashedInput.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }
        
        this.currentUser = user;
        logger.info("User logged in: {}", username);
    }
    
    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }
    
    public void deleteUser(int userId) throws SQLException {
        userDAO.delete(userId);
        if (currentUser != null && currentUser.getUserId() == userId) {
            currentUser = null; // Log out if current user is deleted
        }
    }
    
    public void updateUser(User user) throws SQLException {
        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        
        userDAO.update(user);
        if (currentUser != null && currentUser.getUserId() == user.getUserId()) {
            currentUser = user; // Update current session info
        }
    }

    /**
     * Logout current user.
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
            currentUser = null;
        }
    }

    /**
     * Get current logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if user is logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Get user by ID.
     */
    public User getUserById(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    /**
     * Validate username.
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
    }

    /**
     * Validate email.
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validate password.
     */
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    /**
     * Hash password (simplified - use BCrypt in production).
     */
    private String hashPassword(String password) {
        // This is a simplified hash - in production, use BCrypt or similar
        return "$2a$10$" + Integer.toHexString(password.hashCode());
    }
}
