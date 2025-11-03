package com.ecommerce.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password Utility Class
 * Sử dụng BCrypt để hash và verify passwords
 */
public class PasswordUtil {
    
    /**
     * Hash password sử dụng BCrypt
     * @param password Plain text password
     * @return Hashed password
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    /**
     * Verify password với hash
     * @param password Plain text password
     * @param hash Hashed password từ database
     * @return true nếu password đúng
     */
    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            return false;
        }
    }
}



