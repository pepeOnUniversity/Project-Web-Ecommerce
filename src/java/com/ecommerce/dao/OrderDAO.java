package com.ecommerce.dao;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.User;
import com.ecommerce.util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Order DAO Class
 * Xử lý các thao tác database liên quan đến Order
 */
public class OrderDAO {
    
    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());
    private final DBConnection dbConnection;
    private final ProductDAO productDAO;
    
    public OrderDAO() {
        this.dbConnection = DBConnection.getInstance();
        this.productDAO = new ProductDAO();
    }
    
    /**
     * Lấy order theo ID
     */
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, u.* FROM orders o " +
                     "INNER JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.order_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setUser(mapResultSetToUser(rs));
                    order.setOrderItems(getOrderItemsByOrderId(orderId));
                    return order;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting order by ID: " + orderId, e);
        }
        
        return null;
    }
    
    /**
     * Lấy tất cả orders của user
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting orders by user ID: " + userId, e);
        }
        
        return orders;
    }
    
    /**
     * Lấy tất cả orders (cho admin)
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name, u.email FROM orders o " +
                     "INNER JOIN users u ON o.user_id = u.user_id " +
                     "ORDER BY order_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all orders", e);
        }
        
        return orders;
    }
    
    /**
     * Tạo order mới
     */
    public int createOrder(Order order, List<OrderItem> orderItems) {
        String sql = "INSERT INTO orders (user_id, total_amount, shipping_address, phone, status, payment_method, payment_status, order_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getUserId());
                ps.setBigDecimal(2, order.getTotalAmount());
                ps.setString(3, order.getShippingAddress());
                ps.setString(4, order.getPhone());
                ps.setString(5, order.getStatus() != null ? order.getStatus() : "PENDING");
                ps.setString(6, order.getPaymentMethod() != null ? order.getPaymentMethod() : "COD");
                ps.setString(7, "PENDING"); // payment_status mặc định là PENDING
                ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int orderId = generatedKeys.getInt(1);
                            order.setOrderId(orderId);
                            
                            // Thêm order items
                            if (createOrderItems(orderId, orderItems, conn)) {
                                conn.commit();
                                return orderId;
                            } else {
                                conn.rollback();
                                return -1;
                            }
                        }
                    }
                }
                conn.rollback();
                return -1;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating order", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
                }
            }
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    dbConnection.closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection", e);
                }
            }
        }
    }
    
    /**
     * Tạo order items
     */
    private boolean createOrderItems(int orderId, List<OrderItem> orderItems, Connection conn) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderItem item : orderItems) {
                ps.setInt(1, orderId);
                ps.setInt(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                // Schema chỉ có price (giá tại thời điểm đặt hàng), không có subtotal
                ps.setBigDecimal(4, item.getUnitPrice());
                ps.addBatch();
                
                // Cập nhật stock
                productDAO.updateStock(item.getProductId(), item.getQuantity());
            }
            
            int[] results = ps.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        }
    }
    
    /**
     * Cập nhật order status
     */
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ?, updated_at = GETDATE() WHERE order_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, orderId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating order status", e);
            return false;
        }
    }
    
    /**
     * Cập nhật payment status và transaction ID
     */
    public boolean updateOrderPaymentStatus(int orderId, String paymentStatus, String transactionId) {
        String sql = "UPDATE orders SET payment_status = ?, vnp_transaction_id = ?, updated_at = GETDATE() WHERE order_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, paymentStatus);
            ps.setString(2, transactionId);
            ps.setInt(3, orderId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating order payment status", e);
            return false;
        }
    }
    
    /**
     * Lấy order items theo order ID
     */
    private List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.* FROM order_items oi " +
                     "INNER JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE oi.order_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = mapResultSetToOrderItem(rs);
                    item.setProduct(productDAO.getProductById(rs.getInt("product_id")));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting order items for order: " + orderId, e);
        }
        
        return items;
    }
    
    /**
     * Tính tổng revenue (cho admin dashboard)
     */
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) as total FROM orders WHERE status != 'CANCELLED'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total revenue", e);
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Đếm tổng số orders
     */
    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) as total FROM orders";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total orders", e);
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet thành Order object
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setPhone(rs.getString("phone"));
        order.setStatus(rs.getString("status"));
        
        // Lấy payment_method và payment_status nếu có
        try {
            order.setPaymentMethod(rs.getString("payment_method"));
        } catch (SQLException e) {
            // Column không tồn tại, set null
            order.setPaymentMethod(null);
        }
        
        try {
            order.setPaymentStatus(rs.getString("payment_status"));
        } catch (SQLException e) {
            // Column không tồn tại, set null
            order.setPaymentStatus(null);
        }
        
        try {
            order.setVnpTransactionId(rs.getString("vnp_transaction_id"));
        } catch (SQLException e) {
            // Column không tồn tại, set null
            order.setVnpTransactionId(null);
        }
        
        order.setNotes(null);
        order.setOrderDate(rs.getTimestamp("order_date"));
        return order;
    }
    
    /**
     * Map ResultSet thành User object (từ joined query)
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
    
    /**
     * Map ResultSet thành OrderItem object
     */
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setOrderDetailId(rs.getInt("order_item_id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        // Schema có cột price (giá tại thời điểm đặt hàng), không có subtotal
        BigDecimal price = rs.getBigDecimal("price");
        item.setUnitPrice(price);
        // Tính subtotal = price * quantity
        if (price != null && item.getQuantity() > 0) {
            item.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return item;
    }
}

