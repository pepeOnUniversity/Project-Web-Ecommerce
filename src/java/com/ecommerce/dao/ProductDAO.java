package com.ecommerce.dao;

import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;
import com.ecommerce.util.SlugUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Product DAO Class
 * Xử lý các thao tác database liên quan đến Product
 */
public class ProductDAO {
    
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());
    private final DBConnection dbConnection;
    
    public ProductDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    /**
     * Lấy tất cả products đang active
     * @return 
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = 1 ORDER BY product_id DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all products", e);
        }
        
        return products;
    }
    
    /**
     * Lấy product theo ID
     */
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ? AND is_active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting product by ID: " + productId, e);
        }
        
        return null;
    }
    
    /**
     * Lấy product theo slug
     */
    public Product getProductBySlug(String slug) {
        String sql = "SELECT * FROM products WHERE slug = ? AND is_active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, slug);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting product by slug: " + slug, e);
        }
        
        return null;
    }
    
    /**
     * Kiểm tra slug đã tồn tại chưa (cho product khác)
     */
    public boolean isSlugExists(String slug, Integer excludeProductId) {
        String sql = "SELECT COUNT(*) FROM products WHERE slug = ?";
        if (excludeProductId != null) {
            sql += " AND product_id != ?";
        }
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, slug);
            if (excludeProductId != null) {
                ps.setInt(2, excludeProductId);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking slug existence: " + slug, e);
        }
        
        return false;
    }
    
    /**
     * Lấy featured products
     */
    public List<Product> getFeaturedProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM products WHERE is_featured = 1 AND is_active = 1 ORDER BY product_id DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting featured products", e);
        }
        
        return products;
    }
    
    /**
     * Lấy products theo category
     */
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ? AND is_active = 1 ORDER BY product_id DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting products by category: " + categoryId, e);
        }
        
        return products;
    }
    
    /**
     * Tìm kiếm products theo keyword
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = 1 AND " +
                     "(product_name LIKE ? OR description LIKE ?) " +
                     "ORDER BY product_id DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching products: " + keyword, e);
        }
        
        return products;
    }
    
    /**
     * Thêm product mới
     */
    public boolean addProduct(Product product) {
        // Auto-generate slug nếu chưa có
        String slug = product.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            String baseSlug = SlugUtil.generateSlug(product.getProductName());
            slug = SlugUtil.generateUniqueSlug(baseSlug, s -> isSlugExists(s, null));
            product.setSlug(slug);
        }
        
        String sql = "INSERT INTO products (product_name, slug, description, price, discount_price, " +
                     "stock_quantity, category_id, image_url, is_featured, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getProductName());
            ps.setString(2, slug);
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setBigDecimal(5, product.getDiscountPrice());
            ps.setInt(6, product.getStockQuantity());
            ps.setInt(7, product.getCategoryId());
            ps.setString(8, product.getImageUrl());
            ps.setBoolean(9, product.isFeatured());
            ps.setBoolean(10, product.isActive());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding product", e);
            return false;
        }
    }
    
    /**
     * Cập nhật product
     */
    public boolean updateProduct(Product product) {
        // Auto-generate slug nếu chưa có hoặc tên sản phẩm đã thay đổi
        String slug = product.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            String baseSlug = SlugUtil.generateSlug(product.getProductName());
            slug = SlugUtil.generateUniqueSlug(baseSlug, s -> isSlugExists(s, product.getProductId()));
            product.setSlug(slug);
        }
        
        String sql = "UPDATE products SET product_name = ?, slug = ?, description = ?, price = ?, " +
                     "discount_price = ?, stock_quantity = ?, category_id = ?, image_url = ?, " +
                     "is_featured = ?, is_active = ? WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getProductName());
            ps.setString(2, slug);
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setBigDecimal(5, product.getDiscountPrice());
            ps.setInt(6, product.getStockQuantity());
            ps.setInt(7, product.getCategoryId());
            ps.setString(8, product.getImageUrl());
            ps.setBoolean(9, product.isFeatured());
            ps.setBoolean(10, product.isActive());
            ps.setInt(11, product.getProductId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product", e);
            return false;
        }
    }
    
    /**
     * Xóa product (soft delete)
     */
    public boolean deleteProduct(int productId) {
        String sql = "UPDATE products SET is_active = 0 WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product", e);
            return false;
        }
    }
    
    /**
     * Cập nhật stock quantity
     */
    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock for product: " + productId, e);
            return false;
        }
    }
    
    /**
     * Map ResultSet thành Product object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        
        // Slug có thể null nếu chưa migrate dữ liệu cũ
        try {
            product.setSlug(rs.getString("slug"));
        } catch (SQLException e) {
            // Nếu column slug chưa tồn tại, set null
            product.setSlug(null);
        }
        
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setDiscountPrice(rs.getBigDecimal("discount_price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setImageUrl(rs.getString("image_url"));
        product.setFeatured(rs.getBoolean("is_featured"));
        product.setActive(rs.getBoolean("is_active"));
        return product;
    }
}


