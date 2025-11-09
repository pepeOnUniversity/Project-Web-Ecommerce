package com.ecommerce.dao;

import com.ecommerce.model.User;
import com.ecommerce.util.DBConnection;
import com.ecommerce.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User DAO Class
 * Xử lý các thao tác database liên quan đến User
 */
public class UserDAO {
    
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final DBConnection dbConnection;
    
    public UserDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    /**
     * Lấy user theo username
     * @param username
     * @return 
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by username: " + username, e);
        }
        
        return null;
    }
    
    /**
     * Lấy user theo email
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by email: " + email, e);
        }
        
        return null;
    }
    
    /**
     * Lấy user theo ID
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by ID: " + userId, e);
        }
        
        return null;
    }
    
    /**
     * Xác thực user (đăng nhập)
     * @param username Username của user
     * @param password Plain text password (không phải hash)
     * @return User object nếu đăng nhập thành công, null nếu thất bại
     */
    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.isActive()) {
            // Sử dụng BCrypt để verify password
            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Thêm user mới (đăng ký)
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, phone, address, role, is_active, email_verified, verification_token, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Debug: Log token trước khi lưu
        String tokenToSave = user.getVerificationToken();
        System.out.println("UserDAO.addUser() - Đang lưu user: " + user.getUsername());
        System.out.println("UserDAO.addUser() - Verification token: " + tokenToSave);
        System.out.println("UserDAO.addUser() - Token length: " + (tokenToSave != null ? tokenToSave.length() : 0));
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getRole());
            ps.setBoolean(8, user.isActive());
            ps.setBoolean(9, user.isEmailVerified());
            ps.setString(10, tokenToSave);
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("UserDAO.addUser() - User đã được lưu thành công");
                // Verify token đã được lưu đúng
                User savedUser = getUserByUsername(user.getUsername());
                if (savedUser != null) {
                    String savedToken = savedUser.getVerificationToken();
                    System.out.println("UserDAO.addUser() - Token sau khi lưu (verify): " + savedToken);
                    System.out.println("UserDAO.addUser() - Token match? " + (tokenToSave != null && tokenToSave.equals(savedToken)));
                }
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user", e);
            System.out.println("UserDAO.addUser() - SQL Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật user
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, full_name = ?, phone = ?, address = ? WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setInt(5, user.getUserId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            return false;
        }
    }
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean isUsernameExists(String username) {
        return getUserByUsername(username) != null;
    }
    
    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean isEmailExists(String email) {
        return getUserByEmail(email) != null;
    }
    
    /**
     * Đếm tổng số customers (users có role = 'CUSTOMER')
     * @return Số lượng customers
     */
    public int countTotalCustomers() {
        String sql = "SELECT COUNT(*) as total FROM users WHERE role = 'CUSTOMER' AND is_active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total customers", e);
        }
        
        return 0;
    }
    
    /**
     * Lấy user theo verification token
     * Thử nhiều cách để tìm token (xử lý cả trường hợp token đã encode/decoded)
     */
    public User getUserByVerificationToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        
        // Debug logging
        System.out.println("UserDAO.getUserByVerificationToken() - Token nhận được: " + token);
        System.out.println("UserDAO.getUserByVerificationToken() - Token length: " + token.length());
        
        String sql = "SELECT * FROM users WHERE verification_token = ?";
        
        try (Connection conn = dbConnection.getConnection()) {
            
            // Thử 1: Tìm với token gốc (thường là cách này sẽ work)
            User user = findUserByToken(conn, sql, token);
            if (user != null) {
                System.out.println("UserDAO.getUserByVerificationToken() - Tìm thấy user với token gốc: " + user.getUsername());
                return user;
            }
            
            // Thử 2: Nếu token có dấu %, có thể đã được encode, thử decode và tìm lại
            if (token.contains("%")) {
                try {
                    String decodedToken = java.net.URLDecoder.decode(token, "UTF-8");
                    System.out.println("UserDAO.getUserByVerificationToken() - Thử tìm với decoded token: " + decodedToken);
                    user = findUserByToken(conn, sql, decodedToken);
                    if (user != null) {
                        System.out.println("UserDAO.getUserByVerificationToken() - Tìm thấy user với decoded token: " + user.getUsername());
                        return user;
                    }
                } catch (Exception e) {
                    System.out.println("UserDAO.getUserByVerificationToken() - Lỗi khi decode token: " + e.getMessage());
                }
            }
            
            // Thử 3: Encode token và tìm (nếu token trong DB đã được encode)
            try {
                String encodedToken = java.net.URLEncoder.encode(token, "UTF-8");
                System.out.println("UserDAO.getUserByVerificationToken() - Thử tìm với encoded token: " + encodedToken);
                user = findUserByToken(conn, sql, encodedToken);
                if (user != null) {
                    System.out.println("UserDAO.getUserByVerificationToken() - Tìm thấy user với encoded token: " + user.getUsername());
                    return user;
                }
            } catch (Exception e) {
                System.out.println("UserDAO.getUserByVerificationToken() - Lỗi khi encode token: " + e.getMessage());
            }
            
            // Không tìm thấy với bất kỳ cách nào
            System.out.println("UserDAO.getUserByVerificationToken() - KHÔNG tìm thấy user với token này sau khi thử tất cả cách");
            
            // Debug: Liệt kê tất cả tokens trong database (chỉ trong development)
            debugListAllTokens(conn, token);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by verification token", e);
            System.out.println("UserDAO.getUserByVerificationToken() - SQL Exception: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Helper method: Tìm user với token cụ thể
     */
    private User findUserByToken(Connection conn, String sql, String token) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Debug method: Liệt kê tất cả verification tokens trong database
     * CHỈ DÙNG TRONG DEVELOPMENT!
     */
    private void debugListAllTokens(Connection conn, String searchToken) {
        try {
            String sql = "SELECT user_id, username, email, verification_token, email_verified FROM users WHERE verification_token IS NOT NULL";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                System.out.println("=== DEBUG: Tất cả verification tokens trong database ===");
                int count = 0;
                while (rs.next()) {
                    count++;
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String dbToken = rs.getString("verification_token");
                    boolean verified = rs.getBoolean("email_verified");
                    
                    System.out.println("User #" + count + ":");
                    System.out.println("  - ID: " + userId);
                    System.out.println("  - Username: " + username);
                    System.out.println("  - Email: " + email);
                    System.out.println("  - Token: " + dbToken);
                    System.out.println("  - Token length: " + (dbToken != null ? dbToken.length() : 0));
                    System.out.println("  - Email verified: " + verified);
                    System.out.println("  - Token matches? " + (dbToken != null && dbToken.equals(searchToken)));
                    System.out.println("---");
                }
                System.out.println("Tổng số users có verification_token: " + count);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi debug list tokens: " + e.getMessage());
        }
    }
    
    /**
     * Xác minh email của user
     */
    public boolean verifyEmail(String token) {
        String sql = "UPDATE users SET email_verified = 1, verification_token = NULL WHERE verification_token = ? AND email_verified = 0";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verifying email", e);
            return false;
        }
    }
    
    /**
     * Cập nhật verification token cho user
     */
    public boolean updateVerificationToken(int userId, String token) {
        String sql = "UPDATE users SET verification_token = ? WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating verification token", e);
            return false;
        }
    }
    
    /**
     * Map ResultSet thành User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        
        // Xử lý email_verified (có thể null cho user cũ)
        try {
            user.setEmailVerified(rs.getBoolean("email_verified"));
        } catch (SQLException e) {
            // Nếu column chưa tồn tại, set mặc định là true (cho user cũ)
            user.setEmailVerified(true);
        }
        
        // Xử lý verification_token (có thể null)
        try {
            user.setVerificationToken(rs.getString("verification_token"));
        } catch (SQLException e) {
            // Nếu column chưa tồn tại, set null
            user.setVerificationToken(null);
        }
        
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}


