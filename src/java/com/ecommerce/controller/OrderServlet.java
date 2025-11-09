package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Servlet
 * Xử lý checkout và order history
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/checkout", "/orders", "/order/*"})
public class OrderServlet extends HttpServlet {
    
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    
    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
        orderDAO = new OrderDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = getCurrentUser(session);
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/checkout".equals(path)) {
            showCheckout(request, response, user);
        } else if ("/orders".equals(path)) {
            showOrderHistory(request, response, user);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = getCurrentUser(session);
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/checkout".equals(path)) {
            handlePlaceOrder(request, response, user);
        }
    }
    
    /**
     * Hiển thị trang checkout
     */
    private void showCheckout(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        List<CartItem> cartItems = cartDAO.getCartItemsByUserId(user.getUserId());
        
        if (cartItems.isEmpty()) {
            request.setAttribute("error", "Giỏ hàng của bạn đang trống");
            request.getRequestDispatcher("/views/customer/cart.jsp").forward(request, response);
            return;
        }
        
        // Tính tổng tiền
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("user", user);
        
        request.getRequestDispatcher("/views/customer/checkout.jsp").forward(request, response);
    }
    
    /**
     * Xử lý đặt hàng
     */
    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        String shippingAddress = request.getParameter("shippingAddress");
        String phone = request.getParameter("phone");
        
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập địa chỉ giao hàng");
            showCheckout(request, response, user);
            return;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập số điện thoại");
            showCheckout(request, response, user);
            return;
        }
        
        // Lấy cart items
        List<CartItem> cartItems = cartDAO.getCartItemsByUserId(user.getUserId());
        
        if (cartItems.isEmpty()) {
            request.setAttribute("error", "Giỏ hàng của bạn đang trống");
            showCheckout(request, response, user);
            return;
        }
        
        // Kiểm tra stock quantity trước khi đặt hàng
        // (Stock có thể đã thay đổi từ lúc thêm vào cart)
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct() == null) {
                request.setAttribute("error", "Một số sản phẩm trong giỏ hàng không còn tồn tại");
                showCheckout(request, response, user);
                return;
            }
            
            int requestedQuantity = cartItem.getQuantity();
            int availableStock = cartItem.getProduct().getStockQuantity();
            
            if (availableStock < requestedQuantity) {
                request.setAttribute("error", 
                    String.format("Sản phẩm '%s' chỉ còn %d sản phẩm trong kho (bạn đã chọn %d)", 
                        cartItem.getProduct().getProductName(), availableStock, requestedQuantity));
                showCheckout(request, response, user);
                return;
            }
        }
        
        // Tính tổng tiền
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        
        // Tạo order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getFinalPrice());
            orderItems.add(orderItem);
        }
        
        // Tạo order
        Order order = new Order(user.getUserId(), totalAmount, shippingAddress, phone);
        int orderId = orderDAO.createOrder(order, orderItems);
        
        if (orderId > 0) {
            // Xóa giỏ hàng
            cartDAO.clearCart(user.getUserId());
            
            // Redirect đến trang order history
            response.sendRedirect(request.getContextPath() + "/orders?orderId=" + orderId);
        } else {
            request.setAttribute("error", "Có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.");
            showCheckout(request, response, user);
        }
    }
    
    /**
     * Hiển thị lịch sử đơn hàng
     */
    private void showOrderHistory(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        List<Order> orders = orderDAO.getOrdersByUserId(user.getUserId());
        
        // Nếu có orderId trong query param, highlight order đó
        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam != null) {
            try {
                int orderId = Integer.parseInt(orderIdParam);
                request.setAttribute("highlightOrderId", orderId);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/views/customer/order-history.jsp").forward(request, response);
    }
    
    /**
     * Lấy user hiện tại từ session
     */
    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("user");
    }
}



