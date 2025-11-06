package com.ecommerce.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class để generate URL-friendly slugs từ text
 * Chuyển đổi tiếng Việt và các ký tự đặc biệt thành slug dạng lowercase, dấu gạch ngang
 */
public class SlugUtil {
    
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");
    
    /**
     * Generate slug từ text
     * VD: "Laptop Gaming ASUS ROG" -> "laptop-gaming-asus-rog"
     * 
     * @param text Text cần chuyển đổi
     * @return Slug đã được chuẩn hóa
     */
    public static String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Chuyển về dạng NFD (Normalized Form Decomposed) để tách dấu
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        
        // Loại bỏ dấu tiếng Việt và các ký tự đặc biệt
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        
        // Chuyển về lowercase
        normalized = normalized.toLowerCase();
        
        // Thay thế whitespace và ký tự đặc biệt bằng dấu gạch ngang
        normalized = WHITESPACE.matcher(normalized).replaceAll("-");
        normalized = NON_LATIN.matcher(normalized).replaceAll("-");
        
        // Loại bỏ nhiều dấu gạch ngang liên tiếp
        normalized = normalized.replaceAll("-+", "-");
        
        // Loại bỏ dấu gạch ngang ở đầu và cuối
        normalized = EDGES_DASHES.matcher(normalized).replaceAll("");
        normalized = normalized.trim(); // Thêm trim để đảm bảo không còn khoảng trắng
        
        return normalized;
    }
    
    /**
     * Tạo slug unique bằng cách thêm số đếm nếu cần
     * 
     * @param baseSlug Slug gốc
     * @param isSlugExists Function để check slug đã tồn tại chưa
     * @return Slug unique
     */
    public static String generateUniqueSlug(String baseSlug, java.util.function.Function<String, Boolean> isSlugExists) {
        if (baseSlug == null || baseSlug.isEmpty()) {
            return "";
        }
        
        String slug = baseSlug;
        int counter = 1;
        
        // Nếu slug đã tồn tại, thêm số đếm vào cuối
        while (isSlugExists.apply(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
    
    /**
     * Extract product ID từ URL có format: "123-slug-name" hoặc chỉ "123"
     * 
     * @param pathSegment Phần path từ URL (sau /product/)
     * @return Product ID hoặc null nếu không parse được
     */
    public static Integer extractProductId(String pathSegment) {
        if (pathSegment == null || pathSegment.isEmpty()) {
            return null;
        }
        
        // Nếu có dấu gạch ngang, lấy phần đầu (ID)
        int dashIndex = pathSegment.indexOf('-');
        String idStr = dashIndex > 0 ? pathSegment.substring(0, dashIndex) : pathSegment;
        
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}



