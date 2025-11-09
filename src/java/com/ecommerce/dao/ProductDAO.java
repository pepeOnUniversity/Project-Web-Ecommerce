package com.ecommerce.dao;

import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;
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
        
        LOGGER.info("Getting all active products");
        
        try (Connection conn = dbConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Database connection is NULL!");
                return products;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    try {
                        products.add(mapResultSetToProduct(rs));
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error mapping product from ResultSet", e);
                    }
                }
                LOGGER.info("Total active products found: " + products.size());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all products", e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error getting all products", e);
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * Lấy tất cả products (kể cả inactive) - dùng cho admin
     * @return 
     */
    public List<Product> getAllProductsForAdmin() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY product_id DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all products for admin", e);
        }
        
        return products;
    }
    
    /**
     * Lấy product theo ID (chỉ lấy active)
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
     * Lấy product theo ID (bất kể trạng thái) - dùng cho admin
     */
    public Product getProductByIdForAdmin(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting product by ID for admin: " + productId, e);
        }
        
        return null;
    }
    
    /**
     * Lấy featured products
     */
    public List<Product> getFeaturedProducts(int limit) {
        List<Product> products = new ArrayList<>();
        // SQL Server không hỗ trợ TOP với parameter, phải dùng string concatenation hoặc cách khác
        // Sử dụng cách an toàn hơn: lấy tất cả rồi limit trong code, hoặc dùng OFFSET/FETCH
        String sql = "SELECT * FROM products WHERE is_featured = 1 AND is_active = 1 ORDER BY product_id DESC";
        
        LOGGER.info("Getting featured products with limit: " + limit);
        LOGGER.info("SQL: " + sql);
        
        try (Connection conn = dbConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Database connection is NULL!");
                return products;
            }
            LOGGER.info("Database connection successful");
            
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                LOGGER.info("Executing query...");
                int count = 0;
                while (rs.next() && count < limit) {
                    try {
                        Product product = mapResultSetToProduct(rs);
                        products.add(product);
                        count++;
                        LOGGER.info("Added product: " + product.getProductId() + " - " + product.getProductName());
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error mapping product from ResultSet", e);
                    }
                }
                LOGGER.info("Total featured products found: " + products.size());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting featured products", e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error getting featured products", e);
            e.printStackTrace();
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
        String sql = "INSERT INTO products (product_name, description, price, discount_price, " +
                     "stock_quantity, category_id, image_url, is_featured, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setBigDecimal(4, product.getDiscountPrice());
            ps.setInt(5, product.getStockQuantity());
            ps.setInt(6, product.getCategoryId());
            ps.setString(7, product.getImageUrl());
            ps.setBoolean(8, product.isFeatured());
            ps.setBoolean(9, product.isActive());
            
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
        String sql = "UPDATE products SET product_name = ?, description = ?, price = ?, " +
                     "discount_price = ?, stock_quantity = ?, category_id = ?, image_url = ?, " +
                     "is_featured = ?, is_active = ? WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setBigDecimal(4, product.getDiscountPrice());
            ps.setInt(5, product.getStockQuantity());
            ps.setInt(6, product.getCategoryId());
            ps.setString(7, product.getImageUrl());
            ps.setBoolean(8, product.isFeatured());
            ps.setBoolean(9, product.isActive());
            ps.setInt(10, product.getProductId());
            
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
        
        LOGGER.log(Level.INFO, "Deleting product (soft delete) with ID: " + productId);
        
        try (Connection conn = dbConnection.getConnection()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Database connection is NULL when trying to delete product: " + productId);
                return false;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                
                int rowsAffected = ps.executeUpdate();
                LOGGER.log(Level.INFO, "Delete product query executed. Rows affected: " + rowsAffected + " for product ID: " + productId);
                
                if (rowsAffected > 0) {
                    LOGGER.log(Level.INFO, "Successfully soft deleted product: " + productId);
                    return true;
                } else {
                    LOGGER.log(Level.WARNING, "No rows affected when deleting product: " + productId + ". Product may not exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error deleting product: " + productId, e);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error deleting product: " + productId, e);
            e.printStackTrace();
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
     * Chỉ cập nhật image_url của sản phẩm
     * Dùng khi admin chỉ muốn thay đổi ảnh mà không cần sửa thông tin khác
     * 
     * @param productId ID sản phẩm
     * @param imageUrl Đường dẫn ảnh mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateImageUrl(int productId, String imageUrl) {
        String sql = "UPDATE products SET image_url = ? WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, imageUrl);
            ps.setInt(2, productId);
            
            int rowsAffected = ps.executeUpdate();
            LOGGER.log(Level.INFO, "Updated image_url for product " + productId + ": " + imageUrl);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating image_url for product: " + productId, e);
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


