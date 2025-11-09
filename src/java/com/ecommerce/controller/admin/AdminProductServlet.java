package com.ecommerce.controller.admin;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Admin Product Servlet
 * Xử lý CRUD sản phẩm với link ảnh
 */
@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products"})
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
            
            // Lấy link ảnh nếu có
            String imageUrl = request.getParameter("imageUrl");
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // Validate URL format
                if (isValidImageUrl(imageUrl)) {
                    product.setImageUrl(imageUrl.trim());
                } else {
                    LOGGER.log(Level.WARNING, "Invalid image URL format for new product: " + productName);
                    response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_image_url");
                    return;
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
            
            // Cập nhật link ảnh mới nếu có
            String imageUrl = request.getParameter("imageUrl");
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // Validate URL format
                if (isValidImageUrl(imageUrl)) {
                    existingProduct.setImageUrl(imageUrl.trim());
                } else {
                    LOGGER.log(Level.WARNING, "Invalid image URL format for product update: " + productId);
                    response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_image_url");
                    return;
                }
            }
            // Nếu không nhập link ảnh mới, giữ nguyên ảnh cũ (existingProduct đã có imageUrl từ database)
            
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
     * Validate image URL format
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            java.net.URL urlObj = new java.net.URL(url.trim());
            String protocol = urlObj.getProtocol();
            return "http".equals(protocol) || "https".equals(protocol);
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }
    
    /**
     * Xóa sản phẩm
     */
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Product ID parameter is missing");
                response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_id");
                return;
            }
            
            int productId = Integer.parseInt(idParam);
            LOGGER.log(Level.INFO, "Attempting to delete product with ID: " + productId);
            
            // Lấy thông tin sản phẩm để xóa ảnh (kể cả inactive)
            Product product = productDAO.getProductByIdForAdmin(productId);
            if (product == null) {
                LOGGER.log(Level.WARNING, "Product not found with ID: " + productId);
                response.sendRedirect(request.getContextPath() + "/admin/products?error=product_not_found");
                return;
            }
            
            LOGGER.log(Level.INFO, "Found product: " + product.getProductName() + " (ID: " + productId + ")");
            
            // Kiểm tra xem product có đang được sử dụng trong cart_items hoặc order_items không
            boolean isInUse = productDAO.isProductInUse(productId);
            if (isInUse) {
                LOGGER.log(Level.WARNING, "Cannot delete product " + productId + " because it is in use (cart_items or order_items)");
                response.sendRedirect(request.getContextPath() + "/admin/products?error=product_in_use");
                return;
            }
            
            // Không cần xóa ảnh vì chỉ lưu link URL, không lưu file local
            
            // Xóa trong database (soft delete)
            LOGGER.log(Level.INFO, "Attempting to soft delete product in database: " + productId);
            boolean deleted = productDAO.deleteProduct(productId);
            
            if (deleted) {
                LOGGER.log(Level.INFO, "Successfully deleted product: " + productId);
                response.sendRedirect(request.getContextPath() + "/admin/products?success=deleted");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to delete product in database: " + productId);
                response.sendRedirect(request.getContextPath() + "/admin/products?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid product ID format: " + request.getParameter("id"), e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=invalid_id");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting product", e);
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/products?error=server_error");
        }
    }
}

