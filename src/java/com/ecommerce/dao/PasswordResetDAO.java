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
 * Password Reset DAO Class
 * Xử lý các thao tác database liên quan đến password reset tokens
 */
public class PasswordResetDAO {
    
    private static final Logger LOGGER = Logger.getLogger(PasswordResetDAO.class.getName());
    private final DBConnection dbConnection;
    
    // Token hết hạn sau 1 giờ (3600 giây)
    private static final long TOKEN_EXPIRY_SECONDS = 3600;
    
    public PasswordResetDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    /**
     * Tạo password reset token cho user
     * @param userId ID của user
     * @param token Token string
     * @return true nếu tạo thành công
     */
    public boolean createPasswordResetToken(int userId, String token) {
        // Xóa các token cũ của user này trước (để tránh nhiều token cùng lúc)
        invalidateUserTokens(userId);
        
        // Tính thời gian hết hạn (1 giờ từ bây giờ)
        long expiryTime = System.currentTimeMillis() + (TOKEN_EXPIRY_SECONDS * 1000);
        Timestamp expiresAt = new Timestamp(expiryTime);
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        
        String sql = "INSERT INTO password_reset_tokens (user_id, token, expires_at, created_at, used) " +
                     "VALUES (?, ?, ?, ?, 0)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, expiresAt);
            ps.setTimestamp(4, createdAt);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating password reset token for user: " + userId, e);
            return false;
        }
    }
    
    /**
     * Kiểm tra token có hợp lệ không (tồn tại, chưa hết hạn, chưa được sử dụng)
     * @param token Token string
     * @return user_id nếu token hợp lệ, -1 nếu không hợp lệ
     */
    public int validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return -1;
        }
        
        String sql = "SELECT user_id, expires_at, used FROM password_reset_tokens " +
                     "WHERE token = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Kiểm tra token đã được sử dụng chưa
                    boolean used = rs.getBoolean("used");
                    if (used) {
                        LOGGER.log(Level.INFO, "Token đã được sử dụng: " + token);
                        return -1;
                    }
                    
                    // Kiểm tra token đã hết hạn chưa
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    if (expiresAt.before(now)) {
                        LOGGER.log(Level.INFO, "Token đã hết hạn: " + token);
                        return -1;
                    }
                    
                    // Token hợp lệ
                    int userId = rs.getInt("user_id");
                    LOGGER.log(Level.INFO, "Token hợp lệ cho user: " + userId);
                    return userId;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating password reset token", e);
        }
        
        return -1;
    }
    
    /**
     * Đánh dấu token đã được sử dụng
     * @param token Token string
     * @return true nếu cập nhật thành công
     */
    public boolean markTokenAsUsed(String token) {
        String sql = "UPDATE password_reset_tokens SET used = 1 WHERE token = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking token as used: " + token, e);
            return false;
        }
    }
    
    /**
     * Vô hiệu hóa tất cả token của user (xóa hoặc đánh dấu đã dùng)
     * @param userId ID của user
     */
    private void invalidateUserTokens(int userId) {
        String sql = "UPDATE password_reset_tokens SET used = 1 WHERE user_id = ? AND used = 0";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error invalidating user tokens: " + userId, e);
        }
    }
    
    /**
     * Xóa các token đã hết hạn (cleanup job - có thể gọi định kỳ)
     */
    public void cleanupExpiredTokens() {
        String sql = "DELETE FROM password_reset_tokens WHERE expires_at < ? OR used = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(1, now);
            
            int rowsDeleted = ps.executeUpdate();
            LOGGER.log(Level.INFO, "Đã xóa " + rowsDeleted + " token đã hết hạn hoặc đã sử dụng");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error cleaning up expired tokens", e);
        }
    }
}

