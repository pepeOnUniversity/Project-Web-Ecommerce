package com.ecommerce.dao;

import com.ecommerce.model.CartItem;
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
 * Cart DAO Class
 * Xử lý các thao tác database liên quan đến Cart
 */
public class CartDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CartDAO.class.getName());
    private final DBConnection dbConnection;
    
    public CartDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    /**
     * Lấy tất cả cart items của user
     * @param userId
     * @return 
     */
    public List<CartItem> getCartItemsByUserId(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT c.*, p.* FROM cart_items c " +
                     "INNER JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.user_id = ? AND p.is_active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = mapResultSetToCartItem(rs);
                    item.setProduct(mapResultSetToProduct(rs));
                    cartItems.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cart items for user: " + userId, e);
        }
        
        return cartItems;
    }
    
    /**
     * Lấy cart item theo ID
     */
    public CartItem getCartItemById(int cartItemId) {
        String sql = "SELECT c.*, p.* FROM cart_items c " +
                     "INNER JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.cart_item_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartItemId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CartItem item = mapResultSetToCartItem(rs);
                    item.setProduct(mapResultSetToProduct(rs));
                    return item;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cart item by ID: " + cartItemId, e);
        }
        
        return null;
    }
    
    /**
     * Thêm item vào cart
     */
    public boolean addCartItem(CartItem cartItem) {
        // Kiểm tra xem item đã tồn tại chưa
        CartItem existing = getCartItemByUserAndProduct(cartItem.getUserId(), cartItem.getProductId());
        
        if (existing != null) {
            // Nếu đã tồn tại, cập nhật quantity
            existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
            return updateCartItem(existing);
        } else {
            // Nếu chưa tồn tại, thêm mới
            String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
            
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, cartItem.getUserId());
                ps.setInt(2, cartItem.getProductId());
                ps.setInt(3, cartItem.getQuantity());
                
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error adding cart item", e);
                return false;
            }
        }
    }
    
    /**
     * Cập nhật quantity của cart item
     */
    public boolean updateCartItem(CartItem cartItem) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartItem.getQuantity());
            ps.setInt(2, cartItem.getCartId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating cart item", e);
            return false;
        }
    }
    
    /**
     * Xóa cart item
     */
    public boolean deleteCartItem(int cartItemId) {
        String sql = "DELETE FROM cart_items WHERE cart_item_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartItemId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting cart item", e);
            return false;
        }
    }
    
    /**
     * Xóa tất cả cart items của user
     */
    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected >= 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing cart for user: " + userId, e);
            return false;
        }
    }
    
    /**
     * Lấy cart item theo user và product
     */
    private CartItem getCartItemByUserAndProduct(int userId, int productId) {
        String sql = "SELECT * FROM cart_items WHERE user_id = ? AND product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCartItem(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cart item by user and product", e);
        }
        
        return null;
    }
    
    /**
     * Map ResultSet thành CartItem object
     */
    private CartItem mapResultSetToCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setCartId(rs.getInt("cart_item_id"));
        item.setUserId(rs.getInt("user_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
    
    /**
     * Map ResultSet thành Product object (từ joined query)
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


