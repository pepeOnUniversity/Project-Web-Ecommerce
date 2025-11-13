package com.ecommerce.controller.admin;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Admin Servlet
 * Xử lý admin dashboard và quản lý
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin", "/admin/dashboard", "/admin/orders", "/admin/orders/detail"})
public class AdminServlet extends HttpServlet {
    
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private CategoryDAO categoryDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        categoryDAO = new CategoryDAO();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/admin".equals(path) || "/admin/dashboard".equals(path)) {
            showDashboard(request, response);
        } else if ("/admin/orders".equals(path)) {
            showManageOrders(request, response);
        } else if ("/admin/orders/detail".equals(path)) {
            showOrderDetail(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("updateOrderStatus".equals(action)) {
            handleUpdateOrderStatus(request, response);
        }
    }
    
    /**
     * Hiển thị dashboard
     */
    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Thống kê
            BigDecimal totalRevenue = orderDAO.getTotalRevenue();
            int totalOrders = orderDAO.getTotalOrders();
            int totalProducts = productDAO.getAllProducts().size();
            int totalCustomers = userDAO.countTotalCustomers();
            
            // Lấy recent orders
            List<Order> recentOrders = orderDAO.getAllOrders();
            if (recentOrders.size() > 10) {
                recentOrders = recentOrders.subList(0, 10);
            }
            
            // Lấy low stock products (stock < 10)
            List<Product> allProducts = productDAO.getAllProducts();
            List<Product> lowStockProducts = allProducts.stream()
                    .filter(p -> p.getStockQuantity() < 10)
                    .limit(5)
                    .toList();
            
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("recentOrders", recentOrders);
            request.setAttribute("lowStockProducts", lowStockProducts);
            
            request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải dashboard");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Hiển thị quản lý đơn hàng
     */
    private void showManageOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Order> orders = orderDAO.getAllOrders();
            request.setAttribute("orders", orders);
            
            request.getRequestDispatcher("/views/admin/manage-orders.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Hiển thị chi tiết đơn hàng
     */
    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String orderIdStr = request.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/orders?error=invalid_order");
                return;
            }
            
            int orderId = Integer.parseInt(orderIdStr);
            Order order = orderDAO.getOrderById(orderId);
            
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/admin/orders?error=order_not_found");
                return;
            }
            
            request.setAttribute("order", order);
            request.getRequestDispatcher("/views/admin/order-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=invalid_order");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải chi tiết đơn hàng");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    private void handleUpdateOrderStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String status = request.getParameter("status");
            String returnToDetail = request.getParameter("returnToDetail"); // Check if coming from detail page
            
            if (orderDAO.updateOrderStatus(orderId, status)) {
                if ("true".equals(returnToDetail)) {
                    response.sendRedirect(request.getContextPath() + "/admin/orders/detail?orderId=" + orderId + "&success=true");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/orders?success=true");
                }
            } else {
                if ("true".equals(returnToDetail)) {
                    response.sendRedirect(request.getContextPath() + "/admin/orders/detail?orderId=" + orderId + "&error=true");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/orders?error=true");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            String orderIdStr = request.getParameter("orderId");
            String returnToDetail = request.getParameter("returnToDetail");
            if ("true".equals(returnToDetail) && orderIdStr != null) {
                response.sendRedirect(request.getContextPath() + "/admin/orders/detail?orderId=" + orderIdStr + "&error=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/orders?error=true");
            }
        }
    }
}



