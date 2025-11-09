package com.ecommerce.controller.admin;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import com.ecommerce.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Admin Product Servlet
 * Xử lý CRUD sản phẩm với upload ảnh
 */
@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,  // 1MB
    maxFileSize = 10 * 1024 * 1024,   // 10MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class AdminProductServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(AdminProductServlet.class.getName());
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    
    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("edit".equals(action)) {
            // Hiển thị form edit
            showEditForm(request, response);
        } else if ("delete".equals(action)) {
            // Xóa sản phẩm
            deleteProduct(request, response);
        } else {
            // Mặc định: hiển thị trang quản lý sản phẩm
            showManageProducts(request, response);
        }
    }
    
    /**
     * Hiển thị trang quản lý sản phẩm
     */
    private void showManageProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Admin cần thấy tất cả products (kể cả inactive)
            request.setAttribute("products", productDAO.getAllProductsForAdmin());
            request.setAttribute("categories", categoryDAO.getAllCategories());
            
            request.getRequestDispatcher("/views/admin/manage-products.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing manage products", e);
            request.setAttribute("error", "Có lỗi xảy ra");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addProduct(request, response);
        } else if ("update".equals(action)) {
            updateProduct(request, response);
        } else if ("updateImage".equals(action)) {
            updateProductImage(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_action");
        }
    }
    
    /**
     * Hiển thị form edit sản phẩm
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            // Admin cần có thể edit cả products inactive
            Product product = productDAO.getProductByIdForAdmin(productId);
            
            if (product == null) {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=product_not_found");
                return;
            }
            
            request.setAttribute("product", product);
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.setAttribute("isEdit", true);
            
            // Load products và categories để hiển thị
            request.setAttribute("products", productDAO.getAllProductsForAdmin());
            
            // Forward về trang quản lý với modal edit mở
            request.getRequestDispatcher("/views/admin/manage-products.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_id");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing edit form", e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=server_error");
        }
    }
    
    /**
     * Thêm sản phẩm mới
     */
    private void addProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Lấy thông tin từ form
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            String discountPriceStr = request.getParameter("discountPrice");
            BigDecimal discountPrice = (discountPriceStr != null && !discountPriceStr.trim().isEmpty())
                    ? new BigDecimal(discountPriceStr) : null;
            int stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            boolean isFeatured = "on".equals(request.getParameter("isFeatured"));
            boolean isActive = "on".equals(request.getParameter("isActive"));
            
            // Tạo product object
            Product product = new Product();
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setDiscountPrice(discountPrice);
            product.setStockQuantity(stockQuantity);
            product.setCategoryId(categoryId);
            product.setFeatured(isFeatured);
            product.setActive(isActive);
            
            // Upload ảnh nếu có
            Part imagePart = request.getPart("productImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                String webappPath = getServletContext().getRealPath("/");
                String imagePath = FileUploadUtil.uploadProductImage(imagePart, productName, webappPath);
                
                if (imagePath != null) {
                    product.setImageUrl(imagePath);
                } else {
                    // Nếu upload thất bại, vẫn cho phép tạo sản phẩm nhưng không có ảnh
                    LOGGER.log(Level.WARNING, "Failed to upload image for new product: " + productName);
                }
            }
            
            // Thêm vào database
            if (productDAO.addProduct(product)) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=add_failed");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding product", e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=server_error");
        }
    }
    
    /**
     * Cập nhật sản phẩm
     */
    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            // Admin cần có thể update cả products inactive
            Product existingProduct = productDAO.getProductByIdForAdmin(productId);
            
            if (existingProduct == null) {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=product_not_found");
                return;
            }
            
            // Lấy thông tin từ form
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            String discountPriceStr = request.getParameter("discountPrice");
            BigDecimal discountPrice = (discountPriceStr != null && !discountPriceStr.trim().isEmpty())
                    ? new BigDecimal(discountPriceStr) : null;
            int stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            boolean isFeatured = "on".equals(request.getParameter("isFeatured"));
            boolean isActive = "on".equals(request.getParameter("isActive"));
            
            // Cập nhật thông tin
            existingProduct.setProductName(productName);
            existingProduct.setDescription(description);
            existingProduct.setPrice(price);
            existingProduct.setDiscountPrice(discountPrice);
            existingProduct.setStockQuantity(stockQuantity);
            existingProduct.setCategoryId(categoryId);
            existingProduct.setFeatured(isFeatured);
            existingProduct.setActive(isActive);
            
            // Upload ảnh mới nếu có
            Part imagePart = request.getPart("productImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                String webappPath = getServletContext().getRealPath("/");
                
                // Xóa ảnh cũ
                FileUploadUtil.deleteOldImage(existingProduct.getImageUrl(), webappPath);
                
                // Upload ảnh mới
                String imagePath = FileUploadUtil.uploadProductImage(imagePart, productName, webappPath);
                
                if (imagePath != null) {
                    existingProduct.setImageUrl(imagePath);
                }
            } else {
                // Nếu không upload ảnh mới, giữ nguyên ảnh cũ
                // Không cần làm gì, existingProduct đã có imageUrl từ database
            }
            
            // Cập nhật database
            if (productDAO.updateProduct(existingProduct)) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=update_failed");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product", e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=server_error");
        }
    }
    
    /**
     * Chỉ cập nhật ảnh sản phẩm (không cần sửa thông tin khác)
     */
    private void updateProductImage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            
            // Lấy sản phẩm hiện tại
            Product existingProduct = productDAO.getProductByIdForAdmin(productId);
            if (existingProduct == null) {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=product_not_found");
                return;
            }
            
            // Kiểm tra có file upload không
            Part imagePart = request.getPart("productImage");
            if (imagePart == null || imagePart.getSize() == 0) {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=no_image");
                return;
            }
            
            String webappPath = getServletContext().getRealPath("/");
            
            // Xóa ảnh cũ
            FileUploadUtil.deleteOldImage(existingProduct.getImageUrl(), webappPath);
            
            // Upload ảnh mới
            String imagePath = FileUploadUtil.uploadProductImage(imagePart, existingProduct.getProductName(), webappPath);
            
            if (imagePath == null) {
                // Kiểm tra lỗi cụ thể
                if (imagePart.getSize() > 10 * 1024 * 1024) {
                    response.sendRedirect(request.getContextPath() + "/admin/products?error=file_too_large");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/products?error=upload_failed");
                }
                return;
            }
            
            LOGGER.log(Level.INFO, "Uploaded image path: " + imagePath + " for product ID: " + productId);
            
            // Cập nhật chỉ image_url trong database
            if (productDAO.updateImageUrl(productId, imagePath)) {
                LOGGER.log(Level.INFO, "Successfully updated image_url in database for product ID: " + productId);
                // Redirect với timestamp để force reload
                response.sendRedirect(request.getContextPath() + "/admin/products?success=image_updated&t=" + System.currentTimeMillis());
            } else {
                LOGGER.log(Level.SEVERE, "Failed to update image_url in database for product ID: " + productId);
                response.sendRedirect(request.getContextPath() + "/admin/products?error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_id");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product image", e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=server_error");
        }
    }
    
    /**
     * Xóa sản phẩm
     */
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            
            // Lấy thông tin sản phẩm để xóa ảnh (kể cả inactive)
            Product product = productDAO.getProductByIdForAdmin(productId);
            if (product != null) {
                String webappPath = getServletContext().getRealPath("/");
                FileUploadUtil.deleteOldImage(product.getImageUrl(), webappPath);
            }
            
            // Xóa trong database (soft delete)
            if (productDAO.deleteProduct(productId)) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_id");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting product", e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=server_error");
        }
    }
}

