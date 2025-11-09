package com.ecommerce.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    
    private static DBConnection instance;
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    
    // Database configuration - đọc từ environment variables hoặc system properties
    private static String getConfig(String name, String defaultValue) {
        // Thử lấy từ System Properties trước
        String value = System.getProperty(name);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // Fallback: lấy từ Environment Variables (thử cả dấu chấm và dấu gạch dưới)
        value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            // Thử với tên biến không có dấu chấm (Windows không hỗ trợ dấu chấm trong env vars)
            String envName = name.toUpperCase().replace(".", "_");
            value = System.getenv(envName);
        }
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // Default values cho development (CHỈ DÙNG KHI CHẠY LOCAL)
        // TODO: Xóa hoặc comment phần này khi deploy production
        // Trong production, PHẢI set environment variables hoặc system properties
        if (defaultValue != null) {
            LOGGER.log(Level.WARNING, "Using default value for " + name + ". " +
                      "Please set environment variable or system property for production!");
            return defaultValue;
        }
        
        return null;
    }
    
    private static final String DB_URL = getConfig("db.url", 
        "jdbc:sqlserver://localhost:1433;databaseName=EcommerceDB;encrypt=false;trustServerCertificate=true;");
    private static final String DB_USER = getConfig("db.user", "sa"); 
    private static final String DB_PASSWORD = getConfig("db.password", "123456");
    
    // init JDBC Driver
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "SQL Server JDBC Driver not found!", e);
            throw new RuntimeException("SQL Server JDBC Driver not found!", e);
        }
    }
    
    // Private constructor để đảm bảo singleton
    private DBConnection() {
        // Log cấu hình database (ẩn password)
        LOGGER.log(Level.INFO, "Database Configuration - URL: " + DB_URL + ", User: " + DB_USER);
    }
    
    /**
     * @return 
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    /**
     * @return Connection object
     * @throws SQLException nếu kết nối thất bại
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            LOGGER.log(Level.INFO, "Database connection established successfully");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error connecting to database: " + e.getMessage(), e);
            throw e;
        }
    }
    
    // close connection
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                LOGGER.log(Level.INFO, "Database connection closed");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Test kết nối database
     * @return true nếu kết nối thành công
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Test connection failed: " + e.getMessage(), e);
            return false;
        }
    }
}






