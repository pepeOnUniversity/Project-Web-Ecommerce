package com.ecommerce.util;

/**
 * Utility class để generate password hash
 * Chạy main method để tạo hash mới cho password
 */
public class GeneratePasswordHash {
    
    public static void main(String[] args) {
        String password = "admin123";
        String hash = PasswordUtil.hashPassword(password);
        
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("\nVerify test:");
        System.out.println("Password matches hash: " + PasswordUtil.verifyPassword(password, hash));
        
        // In ra SQL statement để update
        System.out.println("\nSQL to update admin password:");
        System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE username = 'admin';");
    }
}


