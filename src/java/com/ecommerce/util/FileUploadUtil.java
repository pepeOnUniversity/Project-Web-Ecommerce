package com.ecommerce.util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

/**
 * Utility class để xử lý upload file
 * Tự động tạo tên file, lưu file và trả về đường dẫn
 */
public class FileUploadUtil {
    
    private static final Logger LOGGER = Logger.getLogger(FileUploadUtil.class.getName());
    
    // Thư mục lưu ảnh sản phẩm (relative to webapp root)
    private static final String PRODUCTS_IMAGE_DIR = "images/products";
    
    // Kích thước file tối đa (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // Các định dạng ảnh được phép
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    /**
     * Upload ảnh sản phẩm
     * 
     * @param part Part từ request (file upload)
     * @param productName Tên sản phẩm (để tạo tên file)
     * @param webappPath Đường dẫn đến thư mục webapp (getServletContext().getRealPath("/"))
     * @return Đường dẫn ảnh đã lưu (ví dụ: /images/products/iphone-15-pro-max.jpg) hoặc null nếu lỗi
     */
    public static String uploadProductImage(Part part, String productName, String webappPath) {
        if (part == null || part.getSize() == 0) {
            LOGGER.log(Level.INFO, "No file uploaded");
            return null;
        }
        
        // Validate file size
        if (part.getSize() > MAX_FILE_SIZE) {
            LOGGER.log(Level.WARNING, "File size too large: " + part.getSize());
            return null;
        }
        
        // Validate file extension
        String fileName = getFileName(part);
        if (fileName == null || !isValidImageFile(fileName)) {
            LOGGER.log(Level.WARNING, "Invalid file type: " + fileName);
            return null;
        }
        
        try {
            // Tạo tên file từ tên sản phẩm
            String extension = getFileExtension(fileName);
            String newFileName = ImagePathUtil.generateImageFileName(productName, extension);
            
            // Thêm UUID để tránh trùng tên
            String uniqueFileName = addUniqueSuffix(newFileName);
            
            // Tạo đường dẫn đầy đủ
            String imagePath = "/images/products/" + uniqueFileName;
            String fullPath = webappPath + imagePath;
            
            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(webappPath + "/images/products");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Lưu file
            Path targetPath = Paths.get(fullPath);
            try (InputStream inputStream = part.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            LOGGER.log(Level.INFO, "File uploaded successfully: " + imagePath);
            return imagePath;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error uploading file", e);
            return null;
        }
    }
    
    /**
     * Xóa ảnh cũ khi cập nhật sản phẩm
     * 
     * @param oldImagePath Đường dẫn ảnh cũ
     * @param webappPath Đường dẫn đến thư mục webapp
     */
    public static void deleteOldImage(String oldImagePath, String webappPath) {
        if (oldImagePath == null || oldImagePath.trim().isEmpty()) {
            return;
        }
        
        // Chỉ xóa nếu là ảnh local (không phải URL)
        if (oldImagePath.startsWith("http://") || oldImagePath.startsWith("https://")) {
            return;
        }
        
        try {
            String fullPath = webappPath + oldImagePath;
            File oldFile = new File(fullPath);
            
            if (oldFile.exists() && oldFile.isFile()) {
                oldFile.delete();
                LOGGER.log(Level.INFO, "Old image deleted: " + oldImagePath);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error deleting old image: " + oldImagePath, e);
        }
    }
    
    /**
     * Lấy tên file từ Part
     */
    private static String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf("=") + 2, token.length() - 1);
                // Lấy tên file cuối cùng nếu có đường dẫn
                return fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
        }
        return null;
    }
    
    /**
     * Lấy extension của file
     */
    private static String getFileExtension(String fileName) {
        if (fileName == null) {
            return ".jpg";
        }
        
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0) {
            return fileName.substring(lastDot).toLowerCase();
        }
        return ".jpg";
    }
    
    /**
     * Kiểm tra file có phải là ảnh hợp lệ không
     */
    private static boolean isValidImageFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        
        String extension = getFileExtension(fileName);
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Thêm UUID vào tên file để tránh trùng
     * Ví dụ: iphone-15.jpg -> iphone-15-abc123.jpg
     */
    private static String addUniqueSuffix(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0) {
            String name = fileName.substring(0, lastDot);
            String extension = fileName.substring(lastDot);
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            return name + "-" + uniqueId + extension;
        }
        return fileName + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}


