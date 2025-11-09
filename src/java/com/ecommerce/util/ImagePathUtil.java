package com.ecommerce.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class để quản lý đường dẫn ảnh
 * Hỗ trợ cả local storage và cloud storage
 */
public class ImagePathUtil {
    
    private static final Logger LOGGER = Logger.getLogger(ImagePathUtil.class.getName());
    
    // Base path cho ảnh (có thể cấu hình qua environment variable hoặc properties file)
    private static String IMAGE_BASE_PATH = System.getProperty("ecommerce.images.path", 
            System.getenv("ECOMMERCE_IMAGES_PATH"));
    
    // Base URL cho ảnh (dùng cho cloud storage)
    private static String IMAGE_BASE_URL = System.getProperty("ecommerce.images.url",
            System.getenv("ECOMMERCE_IMAGES_URL"));
    
    /**
     * Lấy URL ảnh đầy đủ
     * Nếu có IMAGE_BASE_URL (cloud storage), trả về full URL
     * Nếu không, trả về relative URL (dùng cho local storage)
     * 
     * @param imagePath Đường dẫn ảnh trong DB (ví dụ: /images/products/iphone15.jpg)
     * @return URL ảnh đầy đủ hoặc relative URL
     */
    public static String getImageUrl(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return getDefaultImageUrl();
        }
        
        // Nếu đã là full URL (http/https), trả về luôn
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        
        // Nếu có base URL (cloud storage), trả về full URL
        if (IMAGE_BASE_URL != null && !IMAGE_BASE_URL.trim().isEmpty()) {
            // Đảm bảo base URL không có trailing slash
            String baseUrl = IMAGE_BASE_URL.endsWith("/") 
                    ? IMAGE_BASE_URL.substring(0, IMAGE_BASE_URL.length() - 1)
                    : IMAGE_BASE_URL;
            
            // Đảm bảo imagePath bắt đầu bằng /
            String path = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
            
            return baseUrl + path;
        }
        
        // Trả về relative URL (dùng cho local storage)
        return imagePath.startsWith("/") ? imagePath : "/" + imagePath;
    }
    
    /**
     * Lấy đường dẫn file system thực tế của ảnh
     * Chỉ dùng cho local storage
     * 
     * @param imagePath Đường dẫn ảnh trong DB (ví dụ: /images/products/iphone15.jpg)
     * @return Đường dẫn file system đầy đủ
     */
    public static String getImageFilePath(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }
        
        if (IMAGE_BASE_PATH == null || IMAGE_BASE_PATH.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "IMAGE_BASE_PATH chưa được cấu hình");
            return null;
        }
        
        // Loại bỏ leading slash nếu có
        String relativePath = imagePath.startsWith("/") 
                ? imagePath.substring(1) 
                : imagePath;
        
        // Kết hợp base path với relative path
        String basePath = IMAGE_BASE_PATH.endsWith("/") 
                ? IMAGE_BASE_PATH.substring(0, IMAGE_BASE_PATH.length() - 1)
                : IMAGE_BASE_PATH;
        
        return basePath + "/" + relativePath;
    }
    
    /**
     * Kiểm tra xem có đang dùng cloud storage không
     * 
     * @return true nếu có cấu hình IMAGE_BASE_URL
     */
    public static boolean isUsingCloudStorage() {
        return IMAGE_BASE_URL != null && !IMAGE_BASE_URL.trim().isEmpty();
    }
    
    /**
     * Lấy URL ảnh mặc định khi không có ảnh
     * 
     * @return URL ảnh placeholder
     */
    public static String getDefaultImageUrl() {
        return "/images/placeholder.jpg";
    }
    
    /**
     * Validate đường dẫn ảnh có hợp lệ không
     * 
     * @param imagePath Đường dẫn ảnh
     * @return true nếu hợp lệ
     */
    public static boolean isValidImagePath(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra extension
        String lowerPath = imagePath.toLowerCase();
        return lowerPath.endsWith(".jpg") || 
               lowerPath.endsWith(".jpeg") || 
               lowerPath.endsWith(".png") || 
               lowerPath.endsWith(".gif") || 
               lowerPath.endsWith(".webp");
    }
    
    /**
     * Tạo tên file ảnh từ tên sản phẩm
     * Ví dụ: "iPhone 15 Pro Max" -> "iphone-15-pro-max.jpg"
     * 
     * @param productName Tên sản phẩm
     * @param extension Extension file (mặc định: jpg)
     * @return Tên file đã được format
     */
    public static String generateImageFileName(String productName, String extension) {
        if (productName == null || productName.trim().isEmpty()) {
            return "product-" + System.currentTimeMillis() + "." + (extension != null ? extension : "jpg");
        }
        
        // Chuyển thành lowercase, thay space bằng dash, loại bỏ ký tự đặc biệt
        String fileName = productName.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "") // Loại bỏ ký tự đặc biệt
                .replaceAll("\\s+", "-") // Thay space bằng dash
                .replaceAll("-+", "-"); // Loại bỏ dash trùng lặp
        
        String ext = (extension != null && !extension.isEmpty()) ? extension : "jpg";
        if (!ext.startsWith(".")) {
            ext = "." + ext;
        }
        
        return fileName + ext;
    }
    
    /**
     * Tạo đường dẫn ảnh đầy đủ từ tên sản phẩm
     * 
     * @param productName Tên sản phẩm
     * @param category Loại ảnh (products, categories, etc.)
     * @param extension Extension file
     * @return Đường dẫn ảnh (ví dụ: /images/products/iphone-15-pro-max.jpg)
     */
    public static String generateImagePath(String productName, String category, String extension) {
        String fileName = generateImageFileName(productName, extension);
        String cat = (category != null && !category.isEmpty()) ? category : "products";
        
        return "/images/" + cat + "/" + fileName;
    }
}


