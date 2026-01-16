# Application Startup Guide

## Quick Start

### 1. Database Setup

```bash
# Start PostgreSQL (if not running)
# Then create the database and load data

psql -U postgres -f database/schema.sql
psql -U postgres -d blogging_platform -f database/sample_data.sql
```

### 2. Configure Database Connection

Edit `src/main/resources/database.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/blogging_platform
db.username=postgres
db.password=YOUR_PASSWORD
```

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn javafx:run
```

## Default Login Credentials

You can log in with any user from the sample data:

- **Username:** `john_doe`
- **Password:** `password` (all sample users use this)

Other usernames: `jane_smith`, `tech_guru`, `code_master`, etc.

## Features to Test

### 1. View Posts
- Click "All Posts" to see all blog posts
- Use the search box to find posts by keyword
- Sort posts by Date, Views, or Title
- Double-click a post to view details

### 2. Create a Post
- Click "Login" and enter credentials
- Click "Create Post"
- Fill in title, content, and tags (comma-separated)
- Click "Save Post"

### 3. View Dashboard
- Click "Dashboard" to see platform statistics
- View total posts, users, and cache performance

### 4. Cache Statistics
- Click "Cache Stats" to see caching performance
- Watch hit rate improve as you browse posts

## Troubleshooting

### Database Connection Failed
- Ensure PostgreSQL is running
- Check credentials in `database.properties`
- Verify database exists: `psql -U postgres -l`

### JavaFX Not Found
- Use `mvn javafx:run` instead of `java -jar`
- Ensure Java 11+ is installed

### Build Errors
- Run `mvn clean` first
- Check Java version: `java -version`
- Ensure Maven is installed: `mvn -version`

## Project Structure Summary

```
src/main/
├── java/com/blogging/
│   ├── BloggingPlatformApp.java    # Main application
│   ├── controller/                  # JavaFX controllers
│   │   ├── MainController.java
│   │   ├── PostListController.java
│   │   ├── PostFormController.java
│   │   └── DashboardController.java
│   ├── service/                     # Business logic
│   │   ├── PostService.java        # With caching
│   │   ├── UserService.java
│   │   ├── CommentService.java
│   │   └── ReviewService.java
│   ├── dao/                         # Data access
│   │   ├── PostDAO.java
│   │   ├── UserDAO.java
│   │   ├── CommentDAO.java
│   │   ├── TagDAO.java
│   │   └── ReviewDAO.java
│   ├── model/                       # Entities
│   │   ├── Post.java
│   │   ├── User.java
│   │   ├── Comment.java
│   │   ├── Tag.java
│   │   └── Review.java
│   ├── util/                        # Utilities
│   │   └── DatabaseConnection.java
│   └── config/                      # Configuration
│       └── DatabaseConfig.java
└── resources/
    ├── fxml/                        # UI layouts
    │   ├── MainView.fxml
    │   ├── DashboardView.fxml
    │   ├── PostListView.fxml
    │   └── PostFormView.fxml
    ├── css/
    │   └── application.css
    └── database.properties
```

## Key Features Implemented

**Database Layer**
- 6 normalized tables (3NF)
- Comprehensive indexing (B-tree + GIN)
- Foreign keys and constraints
- Sample data (25 users, 43 posts)

**Service Layer**
- In-memory caching (5-minute TTL)
- QuickSort for custom sorting
- Cache statistics tracking
- Input validation

**JavaFX UI**
- Dashboard with statistics
- Post listing with search/sort
- Post creation form
- Login/logout functionality

**Performance Optimization**
- Connection pooling (HikariCP)
- Prepared statement caching
- Full-text search indexing
- Application-level caching

## Next Steps for Enhancement

1. Add comment and review UI
2. Implement user registration
3. Add post editing/deletion
4. Create performance report
5. Add unit tests
6. Implement pagination
7. Add image upload support
8. Create admin panel

Enjoy exploring the Smart Blogging Platform!
