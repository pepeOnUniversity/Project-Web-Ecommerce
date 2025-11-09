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
            
            // Đảm bảo webappPath không null
            if (webappPath == null || webappPath.trim().isEmpty()) {
                LOGGER.log(Level.SEVERE, "webappPath is null or empty");
                return null;
            }
            
            // Loại bỏ trailing slash nếu có
            String cleanWebappPath = webappPath.endsWith("/") || webappPath.endsWith("\\") 
                    ? webappPath.substring(0, webappPath.length() - 1) 
                    : webappPath;
            
            // Tạo đường dẫn file đầy đủ (thay thế / bằng \ trên Windows nếu cần)
            String fullPath = cleanWebappPath + imagePath.replace("/", File.separator);
            
            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(cleanWebappPath + File.separator + "images" + File.separator + "products");
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    LOGGER.log(Level.WARNING, "Failed to create upload directory: " + uploadDir.getAbsolutePath());
                } else {
                    LOGGER.log(Level.INFO, "Created upload directory: " + uploadDir.getAbsolutePath());
                }
            }
            
            // Lưu file vào thư mục deploy (nơi server serve files)
            Path targetPath = Paths.get(fullPath);
            byte[] fileContent;
            try (InputStream inputStream = part.getInputStream()) {
                // Đọc toàn bộ file vào memory để có thể lưu vào nhiều nơi
                fileContent = inputStream.readAllBytes();
                Files.write(targetPath, fileContent);
            }
            
            // Verify file was saved
            File savedFile = new File(fullPath);
            if (!savedFile.exists() || !savedFile.isFile()) {
                LOGGER.log(Level.SEVERE, "File was not saved correctly: " + fullPath);
                return null;
            }
            
            LOGGER.log(Level.INFO, "File uploaded successfully: " + imagePath + " (saved to: " + fullPath + ", size: " + savedFile.length() + " bytes)");
            
            // Trong development, cũng lưu file vào thư mục source nếu có thể
            // Điều này giúp file không bị mất khi restart server
            try {
                File sourceFile = saveToSourceDirectoryIfPossible(cleanWebappPath, uniqueFileName, fileContent);
                if (sourceFile != null) {
                    LOGGER.log(Level.INFO, "Also saved to source directory: " + sourceFile.getAbsolutePath());
                }
            } catch (Exception e) {
                // Không critical, chỉ log warning
                LOGGER.log(Level.WARNING, "Could not save file to source directory (this is OK in production)", e);
            }
            
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
    
    /**
     * Lưu file vào thư mục source trong development (nếu có thể)
     * Điều này giúp file không bị mất khi restart server
     * 
     * @param deployedWebappPath Đường dẫn thư mục deploy (getRealPath("/"))
     * @param fileName Tên file cần lưu
     * @param fileContent Nội dung file (bytes)
     * @return File đã lưu hoặc null nếu không thể lưu
     */
    private static File saveToSourceDirectoryIfPossible(String deployedWebappPath, String fileName, byte[] fileContent) throws IOException {
        if (fileContent == null || fileContent.length == 0) {
            return null;
        }
        
        // Thử tìm thư mục source bằng cách đi ngược từ thư mục deploy
        File deployedDir = new File(deployedWebappPath);
        
        if (!deployedDir.exists()) {
            return null;
        }
        
        // Tìm thư mục source bằng cách tìm thư mục có chứa "web" và "WEB-INF"
        // Thường thì thư mục deploy sẽ là: .../build/web hoặc .../target/web hoặc .../work/.../web
        // Thư mục source sẽ là: .../web
        File currentDir = deployedDir;
        int maxDepth = 15; // Giới hạn độ sâu tìm kiếm
        int depth = 0;
        
        while (currentDir != null && depth < maxDepth) {
            // Kiểm tra xem có phải là thư mục webapp source không
            // (có WEB-INF và không nằm trong build/target/work)
            File webInfDir = new File(currentDir, "WEB-INF");
            String dirName = currentDir.getName().toLowerCase();
            
            if (webInfDir.exists() && webInfDir.isDirectory()) {
                // Kiểm tra xem có phải là thư mục source (không phải build/target/work)
                if (!dirName.equals("build") && !dirName.equals("target") && 
                    !dirName.equals("work") && !currentDir.getPath().contains("work")) {
                    // Tìm thấy thư mục webapp source
                    File sourceImagesDir = new File(currentDir, "images" + File.separator + "products");
                    if (sourceImagesDir.exists() || sourceImagesDir.mkdirs()) {
                        // Lưu file vào thư mục source
                        File sourceFile = new File(sourceImagesDir, fileName);
                        Files.write(sourceFile.toPath(), fileContent);
                        return sourceFile;
                    }
                }
            }
            
            // Đi lên thư mục cha
            currentDir = currentDir.getParentFile();
            depth++;
        }
        
        // Nếu không tìm thấy bằng cách trên, thử tìm từ workspace root
        // (thường là thư mục có chứa file build.xml hoặc pom.xml)
        String userDir = System.getProperty("user.dir");
        if (userDir != null) {
            File workspaceRoot = new File(userDir);
            File webDir = new File(workspaceRoot, "web");
            File webInfDir = new File(webDir, "WEB-INF");
            
            if (webInfDir.exists() && webInfDir.isDirectory()) {
                File sourceImagesDir = new File(webDir, "images" + File.separator + "products");
                if (sourceImagesDir.exists() || sourceImagesDir.mkdirs()) {
                    File sourceFile = new File(sourceImagesDir, fileName);
                    Files.write(sourceFile.toPath(), fileContent);
                    return sourceFile;
                }
            }
        }
        
        return null;
    }
}


