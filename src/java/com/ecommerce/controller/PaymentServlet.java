package com.ecommerce.controller;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.util.VNPayUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Payment Servlet
 * Xử lý tạo payment URL và redirect đến VNPay
 */
@WebServlet("/payment/vnpay")
public class PaymentServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentServlet.class.getName());
    private final OrderDAO orderDAO;
    
    public PaymentServlet() {
        this.orderDAO = new OrderDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy orderId từ parameter
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Mã đơn hàng không hợp lệ");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
            return;
        }
        
        try {
            int orderId = Integer.parseInt(orderIdStr);
            
            // Lấy order từ database
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                request.setAttribute("error", "Không tìm thấy đơn hàng");
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra order thuộc về user hiện tại
            if (order.getUserId() != user.getUserId()) {
                request.setAttribute("error", "Bạn không có quyền truy cập đơn hàng này");
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra payment method
            if (!"VNPAY".equals(order.getPaymentMethod())) {
                request.setAttribute("error", "Đơn hàng này không sử dụng phương thức thanh toán VNPay");
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra payment status
            if (!"PENDING".equals(order.getPaymentStatus())) {
                request.setAttribute("error", "Đơn hàng này đã được xử lý");
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                return;
            }
            
            // Tạo payment URL
            long amount = order.getTotalAmount().longValue();
            String orderInfo = "Thanh toan don hang #" + orderId;
            String paymentUrl = VNPayUtil.createPaymentUrl(orderId, amount, orderInfo, request);
            
            if (paymentUrl == null || paymentUrl.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Cannot create payment URL for order: " + orderId);
                request.setAttribute("error", "Không thể tạo URL thanh toán. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                return;
            }
            
            // Redirect đến VNPay
            LOGGER.log(Level.INFO, "Redirecting to VNPay for order: " + orderId);
            response.sendRedirect(paymentUrl);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid orderId: " + orderIdStr, e);
            request.setAttribute("error", "Mã đơn hàng không hợp lệ");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment", e);
            request.setAttribute("error", "Có lỗi xảy ra khi xử lý thanh toán. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
}
