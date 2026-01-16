-- ============================================
-- Smart Blogging Platform Sample Data
-- Database: PostgreSQL
-- ============================================

\c blogging_platform;

-- ============================================
-- Insert Users
-- ============================================
INSERT INTO users (username, email, password_hash) VALUES
('john_doe', 'john.doe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('jane_smith', 'jane.smith@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('tech_guru', 'tech.guru@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('code_master', 'code.master@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('data_scientist', 'data.scientist@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('web_developer', 'web.dev@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('mobile_dev', 'mobile.dev@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('ai_enthusiast', 'ai.enthusiast@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('cloud_architect', 'cloud.arch@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('security_expert', 'security.expert@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('devops_ninja', 'devops.ninja@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('ui_designer', 'ui.designer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('backend_pro', 'backend.pro@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('frontend_wizard', 'frontend.wizard@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('database_admin', 'db.admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('qa_tester', 'qa.tester@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('scrum_master', 'scrum.master@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('product_owner', 'product.owner@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('tech_writer', 'tech.writer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('system_admin', 'sys.admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('ml_engineer', 'ml.engineer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('blockchain_dev', 'blockchain.dev@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('game_developer', 'game.dev@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('iot_specialist', 'iot.specialist@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('cyber_security', 'cyber.security@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- ============================================
-- Insert Tags
-- ============================================
INSERT INTO tags (tag_name) VALUES
('java'),
('python'),
('javascript'),
('database'),
('postgresql'),
('tutorial'),
('best-practices'),
('performance'),
('security'),
('web-development'),
('mobile'),
('cloud'),
('devops'),
('ai'),
('machine-learning'),
('data-science'),
('algorithms'),
('design-patterns'),
('testing'),
('agile'),
('microservices'),
('docker'),
('kubernetes'),
('react'),
('angular');

-- ============================================
-- Insert Posts
-- ============================================
INSERT INTO posts (author_id, title, content, view_count, created_at) VALUES
(1, 'Getting Started with Java Programming', 'Java is a powerful, versatile programming language that has been around for decades. In this comprehensive guide, we will explore the fundamentals of Java programming, including object-oriented principles, syntax basics, and best practices for writing clean, maintainable code. Whether you are a beginner or looking to refresh your knowledge, this tutorial will provide you with a solid foundation.', 1250, NOW() - INTERVAL '30 days'),

(2, 'Python for Data Science: A Complete Guide', 'Python has become the de facto language for data science and machine learning. This article covers essential libraries like NumPy, Pandas, and Matplotlib, and demonstrates how to perform data analysis, visualization, and statistical modeling. We will work through real-world examples to help you understand how to apply these concepts in practice.', 2100, NOW() - INTERVAL '28 days'),

(3, 'Understanding PostgreSQL Indexing', 'Database performance is critical for modern applications. In this deep dive, we explore PostgreSQL indexing strategies, including B-tree, Hash, GiST, and GIN indexes. Learn when to use each type, how to analyze query performance, and best practices for maintaining optimal database speed.', 1850, NOW() - INTERVAL '25 days'),

(4, 'Modern JavaScript: ES6 and Beyond', 'JavaScript has evolved significantly with ES6 and subsequent versions. This tutorial covers arrow functions, destructuring, promises, async/await, modules, and other modern features that make JavaScript development more efficient and enjoyable. Code examples included throughout.', 3200, NOW() - INTERVAL '22 days'),

(5, 'Building RESTful APIs with Spring Boot', 'Spring Boot simplifies the development of production-ready applications. This guide walks through creating a RESTful API from scratch, covering controllers, services, repositories, exception handling, validation, and security. Perfect for backend developers looking to master Spring Boot.', 2750, NOW() - INTERVAL '20 days'),

(6, 'Docker Containerization Best Practices', 'Containerization has revolutionized application deployment. Learn how to create efficient Docker images, optimize layer caching, implement multi-stage builds, and manage container orchestration. This article includes practical examples and common pitfalls to avoid.', 1920, NOW() - INTERVAL '18 days'),

(7, 'Introduction to Machine Learning with Python', 'Machine learning is transforming industries worldwide. This beginner-friendly introduction covers supervised and unsupervised learning, common algorithms like linear regression and decision trees, and hands-on implementation using scikit-learn. No prior ML experience required.', 4100, NOW() - INTERVAL '15 days'),

(8, 'Microservices Architecture Patterns', 'Microservices offer scalability and flexibility but come with complexity. This comprehensive guide explores architectural patterns including API Gateway, Service Discovery, Circuit Breaker, and Event Sourcing. Learn when microservices are appropriate and how to implement them effectively.', 2890, NOW() - INTERVAL '14 days'),

(9, 'Securing Web Applications: OWASP Top 10', 'Security should be a top priority for every developer. This article examines the OWASP Top 10 security risks, including SQL injection, XSS, CSRF, and authentication vulnerabilities. Practical examples demonstrate how to identify and mitigate these threats in your applications.', 3450, NOW() - INTERVAL '12 days'),

(10, 'React Hooks: A Practical Guide', 'React Hooks have changed how we write React components. This tutorial covers useState, useEffect, useContext, useReducer, and custom hooks. Learn how to manage state, handle side effects, and create reusable logic in functional components.', 5200, NOW() - INTERVAL '10 days'),

(11, 'Database Normalization Explained', 'Proper database design is fundamental to application success. This article explains normalization forms (1NF, 2NF, 3NF, BCNF) with clear examples. Understand when to normalize and when denormalization might be appropriate for performance optimization.', 1680, NOW() - INTERVAL '9 days'),

(12, 'Kubernetes for Beginners', 'Kubernetes is the leading container orchestration platform. This beginner guide covers pods, deployments, services, ingress, and persistent volumes. Step-by-step instructions help you deploy your first application to a Kubernetes cluster.', 2340, NOW() - INTERVAL '8 days'),

(13, 'Agile Development Methodologies', 'Agile has become the standard for software development. This article explores Scrum, Kanban, and XP methodologies, discussing sprint planning, daily standups, retrospectives, and continuous improvement. Learn how to implement agile practices in your team.', 1560, NOW() - INTERVAL '7 days'),

(14, 'Advanced SQL Query Optimization', 'Writing efficient SQL queries is an essential skill. This advanced tutorial covers query execution plans, index usage, join optimization, subquery performance, and common table expressions. Includes real-world examples with performance comparisons.', 2120, NOW() - INTERVAL '6 days'),

(15, 'Building Progressive Web Apps', 'Progressive Web Apps combine the best of web and mobile applications. Learn how to implement service workers, offline functionality, push notifications, and app-like experiences. This guide includes a complete PWA implementation from scratch.', 2890, NOW() - INTERVAL '5 days'),

(16, 'Git Workflow Best Practices', 'Effective version control is crucial for team collaboration. This article covers Git branching strategies (Git Flow, GitHub Flow), commit message conventions, pull request reviews, and conflict resolution. Improve your team workflow with these proven practices.', 1740, NOW() - INTERVAL '4 days'),

(17, 'Cloud Computing with AWS', 'Amazon Web Services offers a vast array of cloud services. This introduction covers EC2, S3, RDS, Lambda, and CloudFormation. Learn how to architect scalable, reliable applications in the cloud with practical deployment examples.', 3120, NOW() - INTERVAL '3 days'),

(18, 'Test-Driven Development in Practice', 'TDD improves code quality and design. This hands-on guide demonstrates the red-green-refactor cycle, writing effective unit tests, mocking dependencies, and achieving high test coverage. Includes examples in Java and Python.', 1890, NOW() - INTERVAL '2 days'),

(19, 'Understanding Design Patterns', 'Design patterns provide proven solutions to common problems. This comprehensive guide covers creational, structural, and behavioral patterns including Singleton, Factory, Observer, Strategy, and more. Each pattern includes UML diagrams and code examples.', 2650, NOW() - INTERVAL '1 day'),

(20, 'Introduction to GraphQL', 'GraphQL is revolutionizing API development. Learn how GraphQL differs from REST, how to define schemas, write queries and mutations, and implement a GraphQL server. This tutorial includes a complete example application.', 3780, NOW()),

(3, 'PostgreSQL Performance Tuning Tips', 'Maximize your PostgreSQL database performance with these expert tips. Topics include configuration tuning, connection pooling, vacuum strategies, and monitoring tools. Real-world scenarios demonstrate measurable performance improvements.', 1420, NOW() - INTERVAL '26 days'),

(4, 'Asynchronous Programming in JavaScript', 'Master async JavaScript with this comprehensive guide. Learn about callbacks, promises, async/await, and event loop mechanics. Understand how to handle concurrent operations and avoid common pitfalls like callback hell and race conditions.', 2980, NOW() - INTERVAL '24 days'),

(5, 'Spring Security Implementation Guide', 'Secure your Spring Boot applications with Spring Security. This tutorial covers authentication, authorization, JWT tokens, OAuth2, and role-based access control. Step-by-step implementation ensures your applications are protected.', 2210, NOW() - INTERVAL '21 days'),

(6, 'Docker Compose for Multi-Container Apps', 'Orchestrate multiple containers with Docker Compose. Learn how to define services, networks, and volumes in YAML configuration. This guide includes examples for common application stacks including web servers, databases, and caching layers.', 1650, NOW() - INTERVAL '19 days'),

(7, 'Deep Learning with TensorFlow', 'TensorFlow is a powerful framework for deep learning. This tutorial covers neural network basics, convolutional networks for image recognition, and recurrent networks for sequence data. Includes hands-on examples with real datasets.', 3890, NOW() - INTERVAL '17 days'),

(8, 'Event-Driven Microservices', 'Event-driven architecture enables loose coupling in microservices. Learn about message brokers, event sourcing, CQRS pattern, and eventual consistency. This guide includes implementation examples using RabbitMQ and Kafka.', 2340, NOW() - INTERVAL '16 days'),

(9, 'Web Application Security Testing', 'Comprehensive security testing is essential. This article covers penetration testing, vulnerability scanning, security headers, SSL/TLS configuration, and automated security testing tools. Protect your applications from common attacks.', 1980, NOW() - INTERVAL '13 days'),

(10, 'Advanced React Patterns', 'Take your React skills to the next level with advanced patterns. Learn about render props, higher-order components, compound components, and controlled vs uncontrolled components. Includes performance optimization techniques.', 4320, NOW() - INTERVAL '11 days'),

(11, 'SQL vs NoSQL: Choosing the Right Database', 'Understand the differences between SQL and NoSQL databases. This comparison covers use cases, scalability, consistency models, and performance characteristics. Learn when to choose PostgreSQL, MongoDB, Redis, or other database systems.', 2150, NOW() - INTERVAL '8 days'),

(12, 'CI/CD Pipeline with Jenkins', 'Automate your software delivery with Jenkins. This guide covers pipeline creation, automated testing, deployment strategies, and integration with Git, Docker, and Kubernetes. Build a complete CI/CD workflow for your projects.', 1870, NOW() - INTERVAL '7 days'),

(1, 'Java Streams API Tutorial', 'The Streams API revolutionized Java programming. Learn how to process collections functionally using map, filter, reduce, and collectors. This tutorial includes practical examples and performance considerations for stream operations.', 2560, NOW() - INTERVAL '6 days'),

(2, 'Data Visualization with Python', 'Create stunning visualizations with Python libraries. This guide covers Matplotlib, Seaborn, and Plotly for creating charts, graphs, and interactive dashboards. Transform raw data into meaningful insights through effective visualization.', 3120, NOW() - INTERVAL '5 days'),

(15, 'Mobile App Development with React Native', 'Build cross-platform mobile apps with React Native. This tutorial covers component creation, navigation, state management, native modules, and deployment to iOS and Android. Create professional mobile applications with JavaScript.', 3650, NOW() - INTERVAL '4 days'),

(16, 'Version Control Strategies for Teams', 'Effective version control goes beyond basic Git commands. Learn about monorepo vs polyrepo, semantic versioning, changelog generation, and release management. Implement workflows that scale with your team.', 1420, NOW() - INTERVAL '3 days'),

(17, 'Serverless Architecture with AWS Lambda', 'Serverless computing eliminates infrastructure management. This guide covers AWS Lambda functions, API Gateway, DynamoDB, and S3 integration. Build scalable, cost-effective applications without managing servers.', 2780, NOW() - INTERVAL '2 days'),

(18, 'Unit Testing Best Practices', 'Write effective unit tests that catch bugs early. This article covers test structure, assertion libraries, mocking frameworks, and test coverage analysis. Learn how to write maintainable tests that provide real value.', 1690, NOW() - INTERVAL '1 day'),

(19, 'SOLID Principles in Object-Oriented Design', 'SOLID principles lead to better software design. This comprehensive guide explains Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion with practical examples in multiple languages.', 2340, NOW()),

(20, 'Building Real-Time Applications with WebSockets', 'WebSockets enable real-time bidirectional communication. Learn how to implement WebSocket servers and clients, handle connection management, and build applications like chat systems, live notifications, and collaborative tools.', 3210, NOW() - INTERVAL '1 day');

-- Continue with more posts to reach 100+
INSERT INTO posts (author_id, title, content, view_count, created_at) VALUES
(21, 'Blockchain Technology Fundamentals', 'Understand the core concepts of blockchain technology including distributed ledgers, consensus mechanisms, smart contracts, and cryptocurrency. This introduction covers both technical and business perspectives of blockchain applications.', 2890, NOW() - INTERVAL '23 days'),

(22, 'Game Development with Unity', 'Create engaging games with Unity engine. This tutorial covers game physics, animation, UI design, scripting with C#, and deployment to multiple platforms. Build your first 2D and 3D games from scratch.', 3450, NOW() - INTERVAL '20 days'),

(23, 'IoT Development with Raspberry Pi', 'Internet of Things is connecting the physical and digital worlds. Learn how to build IoT projects with Raspberry Pi, sensors, actuators, and cloud connectivity. Includes practical projects like home automation and environmental monitoring.', 1920, NOW() - INTERVAL '18 days'),

(24, 'Cybersecurity Threat Landscape 2026', 'Stay informed about current cybersecurity threats. This article analyzes recent attack vectors, ransomware trends, social engineering tactics, and defensive strategies. Learn how to protect your organization from evolving threats.', 2650, NOW() - INTERVAL '15 days'),

(25, 'API Design Best Practices', 'Design APIs that developers love to use. This guide covers RESTful principles, versioning strategies, documentation, error handling, rate limiting, and authentication. Create consistent, intuitive APIs for your applications.', 2120, NOW() - INTERVAL '12 days');

-- ============================================
-- Insert Post-Tag Associations
-- ============================================
INSERT INTO post_tags (post_id, tag_id) VALUES
-- Post 1: Getting Started with Java Programming
(1, 1), (1, 6), (1, 18),
-- Post 2: Python for Data Science
(2, 2), (2, 16), (2, 6),
-- Post 3: Understanding PostgreSQL Indexing
(3, 4), (3, 5), (3, 8),
-- Post 4: Modern JavaScript
(4, 3), (4, 6), (4, 10),
-- Post 5: Building RESTful APIs
(5, 1), (5, 7), (5, 10),
-- Post 6: Docker Best Practices
(6, 13), (6, 22), (6, 7),
-- Post 7: Machine Learning
(7, 2), (7, 14), (7, 15),
-- Post 8: Microservices Architecture
(8, 21), (8, 18), (8, 8),
-- Post 9: Web Security
(9, 9), (9, 10), (9, 7),
-- Post 10: React Hooks
(10, 3), (10, 24), (10, 6),
-- Post 11: Database Normalization
(11, 4), (11, 7), (11, 18),
-- Post 12: Kubernetes
(12, 23), (12, 13), (12, 6),
-- Post 13: Agile Development
(13, 20), (13, 7),
-- Post 14: SQL Optimization
(14, 4), (14, 5), (14, 8),
-- Post 15: Progressive Web Apps
(15, 3), (15, 10), (15, 11),
-- Post 16: Git Workflow
(16, 7), (16, 13),
-- Post 17: Cloud Computing AWS
(17, 12), (17, 13), (17, 8),
-- Post 18: Test-Driven Development
(18, 19), (18, 7), (18, 1),
-- Post 19: Design Patterns
(19, 18), (19, 7), (19, 1),
-- Post 20: GraphQL
(20, 3), (20, 10), (20, 6),
-- Post 21: PostgreSQL Performance
(21, 4), (21, 5), (21, 8),
-- Post 22: Async JavaScript
(22, 3), (22, 10), (22, 7),
-- Post 23: Spring Security
(23, 1), (23, 9), (23, 7),
-- Post 24: Docker Compose
(24, 13), (24, 22), (24, 7),
-- Post 25: Deep Learning
(25, 2), (25, 14), (25, 15),
-- Post 26: Event-Driven Microservices
(26, 21), (26, 18), (26, 8),
-- Post 27: Security Testing
(27, 9), (27, 19), (27, 7),
-- Post 28: Advanced React
(28, 3), (28, 24), (28, 8),
-- Post 29: SQL vs NoSQL
(29, 4), (29, 7), (29, 18),
-- Post 30: CI/CD Jenkins
(30, 13), (30, 19), (30, 7),
-- Post 31: Java Streams
(31, 1), (31, 6), (31, 8),
-- Post 32: Data Visualization
(32, 2), (32, 16), (32, 6),
-- Post 33: React Native
(33, 3), (33, 11), (33, 6),
-- Post 34: Version Control
(34, 7), (34, 13),
-- Post 35: Serverless AWS Lambda
(35, 12), (35, 3), (35, 8),
-- Post 36: Unit Testing
(36, 19), (36, 7), (36, 1),
-- Post 37: SOLID Principles
(37, 18), (37, 7), (37, 1),
-- Post 38: WebSockets
(38, 3), (38, 10), (38, 6),
-- Post 39: Blockchain
(39, 9), (39, 6),
-- Post 40: Unity Game Development
(40, 6), (40, 11),
-- Post 41: IoT Raspberry Pi
(41, 6), (41, 2),
-- Post 42: Cybersecurity 2026
(42, 9), (42, 7),
-- Post 43: API Design
(43, 7), (43, 10), (43, 18);

-- ============================================
-- Insert Comments
-- ============================================
INSERT INTO comments (post_id, user_id, content, created_at) VALUES
-- Comments on Post 1
(1, 2, 'Great introduction to Java! Very helpful for beginners.', NOW() - INTERVAL '29 days'),
(1, 3, 'Could you add more examples on inheritance?', NOW() - INTERVAL '28 days'),
(1, 5, 'This is exactly what I needed to get started. Thank you!', NOW() - INTERVAL '27 days'),
(1, 8, 'Clear explanations and well-structured content.', NOW() - INTERVAL '26 days'),

-- Comments on Post 2
(2, 1, 'Pandas is such a powerful library. Thanks for the guide!', NOW() - INTERVAL '27 days'),
(2, 4, 'The visualization examples were particularly useful.', NOW() - INTERVAL '26 days'),
(2, 6, 'Would love to see more on machine learning integration.', NOW() - INTERVAL '25 days'),
(2, 9, 'Excellent resource for data science beginners.', NOW() - INTERVAL '24 days'),

-- Comments on Post 3
(3, 2, 'This helped me optimize my slow queries significantly!', NOW() - INTERVAL '24 days'),
(3, 5, 'The GIN index explanation was very clear.', NOW() - INTERVAL '23 days'),
(3, 7, 'Could you cover partial indexes in a follow-up?', NOW() - INTERVAL '22 days'),
(3, 10, 'Performance improved by 10x after applying these techniques.', NOW() - INTERVAL '21 days'),

-- Comments on Post 4
(4, 1, 'ES6 features make JavaScript so much more enjoyable!', NOW() - INTERVAL '21 days'),
(4, 3, 'The async/await section was particularly helpful.', NOW() - INTERVAL '20 days'),
(4, 6, 'Great examples throughout. Very practical.', NOW() - INTERVAL '19 days'),
(4, 11, 'This should be required reading for all JS developers.', NOW() - INTERVAL '18 days'),

-- Comments on Post 5
(5, 2, 'Spring Boot makes backend development so much easier!', NOW() - INTERVAL '19 days'),
(5, 4, 'The exception handling section was very useful.', NOW() - INTERVAL '18 days'),
(5, 7, 'Could you add examples with database integration?', NOW() - INTERVAL '17 days'),
(5, 12, 'Comprehensive guide. Bookmarked for future reference.', NOW() - INTERVAL '16 days'),

-- Comments on Post 10
(10, 3, 'React Hooks changed everything for me!', NOW() - INTERVAL '9 days'),
(10, 5, 'The custom hooks section was brilliant.', NOW() - INTERVAL '8 days'),
(10, 8, 'Very clear explanations with great examples.', NOW() - INTERVAL '7 days'),
(10, 13, 'This is the best React Hooks tutorial I have found.', NOW() - INTERVAL '6 days'),
(10, 15, 'Would love to see more advanced patterns.', NOW() - INTERVAL '5 days'),

-- Comments on Post 20
(20, 4, 'GraphQL is the future of APIs!', NOW() - INTERVAL '12 hours'),
(20, 7, 'The schema definition examples were very helpful.', NOW() - INTERVAL '10 hours'),
(20, 9, 'How does GraphQL handle caching?', NOW() - INTERVAL '8 hours'),
(20, 14, 'Excellent introduction. Looking forward to more.', NOW() - INTERVAL '6 hours'),

-- Additional comments across various posts
(6, 8, 'Docker has revolutionized our deployment process.', NOW() - INTERVAL '17 days'),
(6, 11, 'Multi-stage builds saved us so much space!', NOW() - INTERVAL '16 days'),
(7, 9, 'Machine learning is fascinating. Great tutorial!', NOW() - INTERVAL '14 days'),
(7, 12, 'The scikit-learn examples were perfect.', NOW() - INTERVAL '13 days'),
(8, 10, 'Microservices are complex but worth it.', NOW() - INTERVAL '13 days'),
(8, 13, 'The Circuit Breaker pattern saved our system.', NOW() - INTERVAL '12 days'),
(9, 11, 'Security should always be a priority!', NOW() - INTERVAL '11 days'),
(9, 14, 'The OWASP examples were eye-opening.', NOW() - INTERVAL '10 days'),
(11, 12, 'Database design is an art form.', NOW() - INTERVAL '8 days'),
(11, 15, 'Normalization makes so much sense now.', NOW() - INTERVAL '7 days'),
(12, 13, 'Kubernetes is powerful but has a learning curve.', NOW() - INTERVAL '7 days'),
(12, 16, 'The deployment examples were very helpful.', NOW() - INTERVAL '6 days'),
(14, 15, 'Query optimization is crucial for performance.', NOW() - INTERVAL '5 days'),
(14, 17, 'The execution plan analysis was enlightening.', NOW() - INTERVAL '4 days'),
(15, 16, 'PWAs offer the best of both worlds.', NOW() - INTERVAL '4 days'),
(15, 18, 'Service workers are amazing!', NOW() - INTERVAL '3 days'),
(17, 17, 'AWS is incredibly powerful.', NOW() - INTERVAL '2 days'),
(17, 19, 'The Lambda examples were great.', NOW() - INTERVAL '1 day'),
(18, 18, 'TDD has improved my code quality dramatically.', NOW() - INTERVAL '1 day'),
(18, 20, 'The red-green-refactor cycle is so effective.', NOW() - INTERVAL '12 hours'),
(19, 19, 'Design patterns are essential knowledge.', NOW() - INTERVAL '18 hours'),
(19, 21, 'The Observer pattern example was perfect.', NOW() - INTERVAL '16 hours');

-- ============================================
-- Insert Reviews
-- ============================================
INSERT INTO reviews (post_id, user_id, rating, review_text, created_at) VALUES
-- Reviews for Post 1
(1, 2, 5, 'Excellent tutorial for Java beginners. Clear, concise, and comprehensive.', NOW() - INTERVAL '28 days'),
(1, 3, 4, 'Very good content, but could use more advanced examples.', NOW() - INTERVAL '27 days'),
(1, 5, 5, 'Perfect starting point for learning Java. Highly recommended!', NOW() - INTERVAL '26 days'),
(1, 8, 5, 'Well-written and easy to follow. Great job!', NOW() - INTERVAL '25 days'),

-- Reviews for Post 2
(2, 1, 5, 'Outstanding guide for data science with Python. Very practical.', NOW() - INTERVAL '26 days'),
(2, 4, 5, 'The best Python data science tutorial I have read.', NOW() - INTERVAL '25 days'),
(2, 6, 4, 'Great content, would love more on deep learning.', NOW() - INTERVAL '24 days'),
(2, 9, 5, 'Comprehensive and well-explained. Excellent resource.', NOW() - INTERVAL '23 days'),

-- Reviews for Post 3
(3, 2, 5, 'This article saved me hours of debugging. Amazing insights!', NOW() - INTERVAL '23 days'),
(3, 5, 5, 'Best PostgreSQL indexing guide available. Very detailed.', NOW() - INTERVAL '22 days'),
(3, 7, 4, 'Excellent technical depth. Could use more real-world examples.', NOW() - INTERVAL '21 days'),
(3, 10, 5, 'Performance improvements were immediate. Thank you!', NOW() - INTERVAL '20 days'),

-- Reviews for Post 4
(4, 1, 5, 'Modern JavaScript features explained perfectly.', NOW() - INTERVAL '20 days'),
(4, 3, 5, 'This is now my go-to reference for ES6+.', NOW() - INTERVAL '19 days'),
(4, 6, 4, 'Very good tutorial, well-structured and informative.', NOW() - INTERVAL '18 days'),
(4, 11, 5, 'Comprehensive coverage of modern JavaScript. Excellent!', NOW() - INTERVAL '17 days'),

-- Reviews for Post 5
(5, 2, 5, 'Spring Boot tutorial done right. Very practical.', NOW() - INTERVAL '18 days'),
(5, 4, 4, 'Good content, but could expand on security aspects.', NOW() - INTERVAL '17 days'),
(5, 7, 5, 'Everything I needed to build my first API. Perfect!', NOW() - INTERVAL '16 days'),
(5, 12, 5, 'Clear, detailed, and actionable. Highly recommended.', NOW() - INTERVAL '15 days'),

-- Reviews for Post 10
(10, 3, 5, 'The best React Hooks tutorial available. Period.', NOW() - INTERVAL '8 days'),
(10, 5, 5, 'Custom hooks section was incredibly valuable.', NOW() - INTERVAL '7 days'),
(10, 8, 5, 'Clear examples and excellent explanations throughout.', NOW() - INTERVAL '6 days'),
(10, 13, 5, 'This tutorial elevated my React skills significantly.', NOW() - INTERVAL '5 days'),
(10, 15, 4, 'Very good, but would love more performance tips.', NOW() - INTERVAL '4 days'),

-- Reviews for Post 20
(20, 4, 5, 'GraphQL explained better than any other resource.', NOW() - INTERVAL '10 hours'),
(20, 7, 5, 'Practical examples made learning GraphQL easy.', NOW() - INTERVAL '8 hours'),
(20, 9, 4, 'Great introduction, looking forward to advanced topics.', NOW() - INTERVAL '6 hours'),
(20, 14, 5, 'Comprehensive and well-structured. Excellent work!', NOW() - INTERVAL '4 hours'),

-- Additional reviews
(6, 8, 5, 'Docker best practices that actually work in production.', NOW() - INTERVAL '16 days'),
(6, 11, 5, 'Multi-stage builds changed our deployment strategy.', NOW() - INTERVAL '15 days'),
(7, 9, 5, 'Machine learning made accessible. Great tutorial!', NOW() - INTERVAL '13 days'),
(7, 12, 4, 'Good introduction, but needs more advanced topics.', NOW() - INTERVAL '12 days'),
(8, 10, 4, 'Solid microservices overview with practical patterns.', NOW() - INTERVAL '12 days'),
(8, 13, 5, 'The architectural patterns section was invaluable.', NOW() - INTERVAL '11 days'),
(9, 11, 5, 'Essential security knowledge for every developer.', NOW() - INTERVAL '10 days'),
(9, 14, 5, 'OWASP Top 10 explained clearly with examples.', NOW() - INTERVAL '9 days'),
(11, 12, 5, 'Database normalization finally makes sense!', NOW() - INTERVAL '7 days'),
(11, 15, 4, 'Good explanation, could use more complex examples.', NOW() - INTERVAL '6 days'),
(12, 13, 4, 'Kubernetes basics covered well for beginners.', NOW() - INTERVAL '6 days'),
(12, 16, 5, 'Step-by-step deployment guide was perfect.', NOW() - INTERVAL '5 days'),
(14, 15, 5, 'Query optimization techniques that deliver results.', NOW() - INTERVAL '4 days'),
(14, 17, 5, 'Execution plan analysis was extremely helpful.', NOW() - INTERVAL '3 days'),
(15, 16, 5, 'PWA implementation guide that actually works.', NOW() - INTERVAL '3 days'),
(15, 18, 4, 'Good tutorial, service workers well explained.', NOW() - INTERVAL '2 days'),
(17, 17, 5, 'AWS services explained clearly and practically.', NOW() - INTERVAL '1 day'),
(17, 19, 4, 'Solid introduction to AWS cloud computing.', NOW() - INTERVAL '12 hours'),
(18, 18, 5, 'TDD practices that improve code quality.', NOW() - INTERVAL '18 hours'),
(18, 20, 5, 'Red-green-refactor explained perfectly.', NOW() - INTERVAL '10 hours'),
(19, 19, 5, 'Design patterns reference I keep coming back to.', NOW() - INTERVAL '16 hours'),
(19, 21, 5, 'SOLID principles with excellent examples.', NOW() - INTERVAL '14 hours');

-- ============================================
-- Display Summary Statistics
-- ============================================
SELECT 'Database populated successfully!' AS status;

SELECT 'Summary Statistics:' AS info;
SELECT 
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM posts) AS total_posts,
    (SELECT COUNT(*) FROM comments) AS total_comments,
    (SELECT COUNT(*) FROM tags) AS total_tags,
    (SELECT COUNT(*) FROM post_tags) AS total_post_tag_associations,
    (SELECT COUNT(*) FROM reviews) AS total_reviews;
