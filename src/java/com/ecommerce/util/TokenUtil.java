package com.ecommerce.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Token Utility Class
 * Tạo verification token an toàn
 */
public class TokenUtil {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_LENGTH = 32; // 32 bytes = 256 bits
    
    /**
     * Tạo verification token ngẫu nhiên an toàn
     * @return Base64 encoded token
     */
    public static String generateVerificationToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}


