package com.ecommerce.dao;

import com.ecommerce.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pending Registration DAO Class
 * Xử lý các thao tác database liên quan đến đăng ký chưa xác minh
 */
public class PendingRegistrationDAO {
    
    private static final Logger LOGGER = Logger.getLogger(PendingRegistrationDAO.class.getName());
    private final DBConnection dbConnection;
    
    public PendingRegistrationDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    /**
     * Lưu thông tin đăng ký chưa xác minh
     * @param username
     * @param email
     * @param passwordHash
     * @param fullName
     * @param phone
     * @param address
     * @param verificationToken
     * @return true nếu thành công
     */
    public boolean addPendingRegistration(String username, String email, String passwordHash, 
                                         String fullName, String phone, String address, 
                                         String verificationToken) {
        String sql = "INSERT INTO pending_registrations " +
                     "(username, email, password_hash, full_name, phone, address, verification_token, expires_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, fullName);
            ps.setString(5, phone);
            ps.setString(6, address);
            ps.setString(7, verificationToken);
            
            // Token hết hạn sau 24 giờ
            long expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
            ps.setTimestamp(8, new Timestamp(expiresAt));
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding pending registration", e);
            return false;
        }
    }
    
    /**
     * Lấy thông tin đăng ký chưa xác minh theo token
     * @param token
     * @return PendingRegistration object hoặc null
     */
    public PendingRegistration getByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT * FROM pending_registrations WHERE verification_token = ? AND expires_at > GETDATE()";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPendingRegistration(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting pending registration by token", e);
        }
        
        return null;
    }
    
    /**
     * Xóa đăng ký chưa xác minh sau khi đã xác minh thành công
     * @param token
     * @return true nếu thành công
     */
    public boolean deleteByToken(String token) {
        String sql = "DELETE FROM pending_registrations WHERE verification_token = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting pending registration", e);
            return false;
        }
    }
    
    /**
     * Kiểm tra username đã tồn tại trong pending registrations
     * @param username
     * @return true nếu đã tồn tại
     */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM pending_registrations WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking username in pending registrations", e);
        }
        
        return false;
    }
    
    /**
     * Kiểm tra email đã tồn tại trong pending registrations
     * @param email
     * @return true nếu đã tồn tại
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM pending_registrations WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email in pending registrations", e);
        }
        
        return false;
    }
    
    /**
     * Xóa các đăng ký đã hết hạn
     * @return số lượng bản ghi đã xóa
     */
    public int deleteExpiredRegistrations() {
        String sql = "DELETE FROM pending_registrations WHERE expires_at < GETDATE()";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting expired registrations", e);
            return 0;
        }
    }
    
    /**
     * Map ResultSet thành PendingRegistration object
     */
    private PendingRegistration mapResultSetToPendingRegistration(ResultSet rs) throws SQLException {
        PendingRegistration pr = new PendingRegistration();
        pr.setRegistrationId(rs.getInt("registration_id"));
        pr.setUsername(rs.getString("username"));
        pr.setEmail(rs.getString("email"));
        pr.setPasswordHash(rs.getString("password_hash"));
        pr.setFullName(rs.getString("full_name"));
        pr.setPhone(rs.getString("phone"));
        pr.setAddress(rs.getString("address"));
        pr.setVerificationToken(rs.getString("verification_token"));
        pr.setCreatedAt(rs.getTimestamp("created_at"));
        pr.setExpiresAt(rs.getTimestamp("expires_at"));
        return pr;
    }
    
    /**
     * Inner class để lưu thông tin pending registration
     */
    public static class PendingRegistration {
        private int registrationId;
        private String username;
        private String email;
        private String passwordHash;
        private String fullName;
        private String phone;
        private String address;
        private String verificationToken;
        private Timestamp createdAt;
        private Timestamp expiresAt;
        
        // Getters and Setters
        public int getRegistrationId() {
            return registrationId;
        }
        
        public void setRegistrationId(int registrationId) {
            this.registrationId = registrationId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPasswordHash() {
            return passwordHash;
        }
        
        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public String getVerificationToken() {
            return verificationToken;
        }
        
        public void setVerificationToken(String verificationToken) {
            this.verificationToken = verificationToken;
        }
        
        public Timestamp getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
        }
        
        public Timestamp getExpiresAt() {
            return expiresAt;
        }
        
        public void setExpiresAt(Timestamp expiresAt) {
            this.expiresAt = expiresAt;
        }
    }
}

