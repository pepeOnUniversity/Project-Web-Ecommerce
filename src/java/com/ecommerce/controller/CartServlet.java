package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cart Servlet
 * Xử lý giỏ hàng với AJAX support
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/*"})
@MultipartConfig
public class CartServlet extends HttpServlet {
    
    private CartDAO cartDAO;
    private ProductDAO productDAO;
    
    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
        productDAO = new ProductDAO();
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
        
        List<CartItem> cartItems = cartDAO.getCartItemsByUserId(user.getUserId());
        request.setAttribute("cartItems", cartItems);
        
        request.getRequestDispatcher("/views/customer/cart.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo luôn trả về JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            HttpSession session = request.getSession(false);
            User user = getCurrentUser(session);
            
            if (user == null) {
                sendJsonResponse(response, false, "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng", null);
                return;
            }
            
            String action = request.getParameter("action");
            
            // Xử lý trường hợp action null hoặc empty
            if (action == null || action.trim().isEmpty()) {
                sendJsonResponse(response, false, "Thiếu tham số action", null);
                return;
            }
            
            if ("add".equals(action)) {
                handleAddToCart(request, response, user);
            } else {
                sendJsonResponse(response, false, "Invalid action: " + action, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Đảm bảo response chưa bị commit
            if (!response.isCommitted()) {
                sendJsonResponse(response, false, "Có lỗi xảy ra: " + e.getMessage(), null);
            }
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo luôn trả về JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            HttpSession session = request.getSession(false);
            User user = getCurrentUser(session);
            
            if (user == null) {
                sendJsonResponse(response, false, "Bạn cần đăng nhập", null);
                return;
            }
            
            handleUpdateCart(request, response, user);
        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                sendJsonResponse(response, false, "Có lỗi xảy ra: " + e.getMessage(), null);
            }
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo luôn trả về JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            HttpSession session = request.getSession(false);
            User user = getCurrentUser(session);
            
            if (user == null) {
                sendJsonResponse(response, false, "Bạn cần đăng nhập", null);
                return;
            }
            
            handleRemoveFromCart(request, response, user);
        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                sendJsonResponse(response, false, "Có lỗi xảy ra: " + e.getMessage(), null);
            }
        }
    }
    
    /**
     * Xử lý thêm vào giỏ hàng
     */
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        
        try {
            String productIdStr = request.getParameter("productId");
            String quantityStr = request.getParameter("quantity");
            
            if (productIdStr == null || quantityStr == null) {
                sendJsonResponse(response, false, "Thiếu thông tin cần thiết", null);
                return;
            }
            
            int productId = Integer.parseInt(productIdStr);
            int quantity = Integer.parseInt(quantityStr);
            
            if (quantity <= 0) {
                sendJsonResponse(response, false, "Số lượng phải lớn hơn 0", null);
                return;
            }
            
            // Kiểm tra product tồn tại và còn hàng
            var product = productDAO.getProductById(productId);
            if (product == null) {
                sendJsonResponse(response, false, "Sản phẩm không tồn tại", null);
                return;
            }
            
            if (product.getStockQuantity() < quantity) {
                sendJsonResponse(response, false, "Số lượng trong kho không đủ", null);
                return;
            }
            
            CartItem cartItem = new CartItem(user.getUserId(), productId, quantity);
            boolean success = cartDAO.addCartItem(cartItem);
            
            if (success) {
                int cartCount = cartDAO.getCartItemsByUserId(user.getUserId()).size();
                sendJsonResponse(response, true, "Đã thêm vào giỏ hàng", Map.of("cartCount", cartCount));
            } else {
                sendJsonResponse(response, false, "Có lỗi xảy ra khi thêm vào giỏ hàng", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Dữ liệu không hợp lệ", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Có lỗi xảy ra", null);
        }
    }
    
    /**
     * Xử lý cập nhật giỏ hàng
     */
    private void handleUpdateCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        
        try {
            // Đọc parameters từ request body cho PUT request
            Map<String, String> params = parseRequestBody(request);
            String cartItemIdStr = params.get("cartItemId");
            String quantityStr = params.get("quantity");
            
            if (cartItemIdStr == null || quantityStr == null) {
                sendJsonResponse(response, false, "Thiếu thông tin cần thiết", null);
                return;
            }
            
            int cartItemId = Integer.parseInt(cartItemIdStr);
            int quantity = Integer.parseInt(quantityStr);
            
            if (quantity <= 0) {
                sendJsonResponse(response, false, "Số lượng phải lớn hơn 0", null);
                return;
            }
            
            CartItem cartItem = cartDAO.getCartItemById(cartItemId);
            if (cartItem == null || cartItem.getUserId() != user.getUserId()) {
                sendJsonResponse(response, false, "Item không tồn tại", null);
                return;
            }
            
            // Kiểm tra stock
            if (cartItem.getProduct() == null || cartItem.getProduct().getStockQuantity() < quantity) {
                sendJsonResponse(response, false, "Số lượng trong kho không đủ", null);
                return;
            }
            
            cartItem.setQuantity(quantity);
            boolean success = cartDAO.updateCartItem(cartItem);
            
            if (success) {
                sendJsonResponse(response, true, "Đã cập nhật giỏ hàng", null);
            } else {
                sendJsonResponse(response, false, "Có lỗi xảy ra", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Dữ liệu không hợp lệ", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Có lỗi xảy ra", null);
        }
    }
    
    /**
     * Xử lý xóa khỏi giỏ hàng
     */
    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        
        try {
            // Đọc parameters từ request body cho DELETE request
            Map<String, String> params = parseRequestBody(request);
            String cartItemIdStr = params.get("cartItemId");
            
            if (cartItemIdStr == null) {
                sendJsonResponse(response, false, "Thiếu thông tin cần thiết", null);
                return;
            }
            
            int cartItemId = Integer.parseInt(cartItemIdStr);
            
            CartItem cartItem = cartDAO.getCartItemById(cartItemId);
            if (cartItem == null || cartItem.getUserId() != user.getUserId()) {
                sendJsonResponse(response, false, "Item không tồn tại", null);
                return;
            }
            
            boolean success = cartDAO.deleteCartItem(cartItemId);
            
            if (success) {
                int cartCount = cartDAO.getCartItemsByUserId(user.getUserId()).size();
                sendJsonResponse(response, true, "Đã xóa khỏi giỏ hàng", Map.of("cartCount", cartCount));
            } else {
                sendJsonResponse(response, false, "Có lỗi xảy ra", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Dữ liệu không hợp lệ", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Có lỗi xảy ra", null);
        }
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
    
    /**
     * Gửi JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, Map<String, Object> data)
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":").append(success).append(",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        
        if (data != null && !data.isEmpty()) {
            json.append(",");
            boolean first = true;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!first) json.append(",");
                json.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof Number) {
                    json.append(entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
                    json.append(entry.getValue());
                } else {
                    json.append("\"").append(escapeJson(entry.getValue().toString())).append("\"");
                }
                first = false;
            }
        }
        
        json.append("}");
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
    
    /**
     * Parse request body để lấy parameters (cho PUT và DELETE requests)
     */
    private Map<String, String> parseRequestBody(HttpServletRequest request) throws IOException {
        Map<String, String> params = new HashMap<>();
        
        // Đọc request body
        StringBuilder body = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        
        // Parse URL-encoded parameters
        String bodyStr = body.toString();
        if (bodyStr != null && !bodyStr.isEmpty()) {
            String[] pairs = bodyStr.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                        params.put(key, value);
                    } catch (Exception e) {
                        // Ignore invalid pairs
                    }
                }
            }
        }
        
        return params;
    }
    
    /**
     * Escape JSON string
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

