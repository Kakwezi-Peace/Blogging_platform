package com.blogging.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


 //Database configuration management class.
 //Loads database connection properties from configuration file.

public class DatabaseConfig {
    private static final String CONFIG_FILE = "database.properties";
    private static final Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("Unable to find " + CONFIG_FILE + ", using default values");
                setDefaultProperties();
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/blogging_platform");
        properties.setProperty("db.username", "postgres");
        properties.setProperty("db.password", "postgres");
        properties.setProperty("db.driver", "org.postgresql.Driver");
        properties.setProperty("db.pool.size", "10");
        properties.setProperty("db.pool.timeout", "30000");
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    public static String getDriver() {
        return properties.getProperty("db.driver");
    }

    public static int getPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.size", "10"));
    }

    public static long getPoolTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.timeout", "30000"));
    }
}
