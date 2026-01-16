package com.blogging.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 // Hibernate utility class for managing SessionFactory.
 // Automatically creates database tables based on JPA entity annotations.

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory;

    static {
        try {
            logger.info("Initializing Hibernate SessionFactory...");
            
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            
            sessionFactory = configuration.buildSessionFactory();
            
            logger.info("Hibernate SessionFactory initialized successfully");
            logger.info("Database tables will be created/updated automatically");
            
        } catch (Exception e) {
            logger.error("Failed to initialize Hibernate SessionFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void getSessionFactory() {
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }
}
