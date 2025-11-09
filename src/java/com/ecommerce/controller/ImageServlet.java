package com.ecommerce.controller;

import com.ecommerce.util.ImagePathUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet để serve ảnh từ thư mục bên ngoài webapp
 * Map URL pattern: /images/*
 * 
 * Cấu hình trong web.xml:
 * <init-param>
 *     <param-name>imageBasePath</param-name>
 *     <param-value>/var/www/ecommerce/images</param-value>
 * </init-param>
 */
@WebServlet(name = "ImageServlet", urlPatterns = {"/images/*"})
public class ImageServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ImageServlet.class.getName());
    
    private String imageBasePath;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Lấy base path từ init parameter hoặc environment variable
        imageBasePath = getInitParameter("imageBasePath");
        
        if (imageBasePath == null || imageBasePath.trim().isEmpty()) {
            // Thử lấy từ environment variable
            imageBasePath = System.getProperty("ecommerce.images.path");
            if (imageBasePath == null) {
                imageBasePath = System.getenv("ECOMMERCE_IMAGES_PATH");
            }
        }
        
        // Nếu vẫn không có, dùng thư mục mặc định trong webapp
        if (imageBasePath == null || imageBasePath.trim().isEmpty()) {
            String webappPath = getServletContext().getRealPath("/");
            imageBasePath = webappPath + "images";
            LOGGER.log(Level.WARNING, "IMAGE_BASE_PATH chưa được cấu hình, dùng thư mục mặc định: " + imageBasePath);
        }
        
        LOGGER.log(Level.INFO, "ImageServlet initialized with base path: " + imageBasePath);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy đường dẫn ảnh từ URL
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.trim().isEmpty() || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image path not specified");
            return;
        }
        
        // Loại bỏ leading slash
        String imagePath = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        
        // Tạo đường dẫn file đầy đủ
        File imageFile = new File(imageBasePath, imagePath);
        
        // Kiểm tra file có tồn tại không
        if (!imageFile.exists() || !imageFile.isFile()) {
            LOGGER.log(Level.WARNING, "Image not found: " + imageFile.getAbsolutePath());
            // Nếu không tìm thấy, thử dùng placeholder
            File placeholderFile = new File(imageBasePath, "placeholder.jpg");
            if (placeholderFile.exists() && placeholderFile.isFile()) {
                imageFile = placeholderFile;
                LOGGER.log(Level.INFO, "Using placeholder image: " + placeholderFile.getAbsolutePath());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
                return;
            }
        }
        
        // Kiểm tra security: đảm bảo file nằm trong base path
        try {
            String canonicalPath = imageFile.getCanonicalPath();
            String canonicalBasePath = new File(imageBasePath).getCanonicalPath();
            
            if (!canonicalPath.startsWith(canonicalBasePath)) {
                LOGGER.log(Level.SEVERE, "Security violation: Attempted to access file outside base path");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error checking file path", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        
        // Xác định content type
        String contentType = getContentType(imagePath);
        response.setContentType(contentType);
        
        // Set cache headers (cache 1 ngày)
        response.setHeader("Cache-Control", "public, max-age=86400");
        response.setHeader("Expires", String.valueOf(System.currentTimeMillis() + 86400000));
        
        // Đọc và gửi file
        try (FileInputStream fis = new FileInputStream(imageFile);
             OutputStream out = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading image file: " + imageFile.getAbsolutePath(), e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading image");
            }
        }
    }
    
    /**
     * Xác định content type dựa trên extension
     */
    private String getContentType(String filename) {
        String lower = filename.toLowerCase();
        
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        
        return "application/octet-stream";
    }
}


