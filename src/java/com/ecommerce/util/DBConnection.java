package com.ecommerce.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    
    private static DBConnection instance;
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=EcommerceDB;encrypt=false;trustServerCertificate=true;";
    private static final String DB_USER = "sa"; 
    private static final String DB_PASSWORD = "123456"; 
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    
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






