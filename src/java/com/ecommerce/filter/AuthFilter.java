package com.ecommerce.filter;

import com.ecommerce.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Authentication Filter
 * Bảo vệ các trang yêu cầu đăng nhập
 */
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần khởi tạo gì
    }
    
    /**
     * Kiểm tra xem request có phải là AJAX request không
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        String method = request.getMethod();
        
        // Kiểm tra X-Requested-With header (thường có giá trị "XMLHttpRequest")
        if ("XMLHttpRequest".equals(requestedWith)) {
            return true;
        }
        
        // Kiểm tra Accept header có chứa application/json không
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        
        // Đối với POST/PUT/DELETE requests, nếu có Content-Type là form-urlencoded
        // và không phải là GET request, có thể là AJAX
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
                // Kiểm tra thêm bằng cách xem có header Origin (thường có trong AJAX)
                String origin = request.getHeader("Origin");
                if (origin != null) {
                    return true;
                }
                // Hoặc kiểm tra Referer header
                String referer = request.getHeader("Referer");
                if (referer != null && referer.contains(request.getContextPath())) {
                    // Nếu referer cùng domain, có thể là AJAX từ cùng trang
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Gửi JSON response cho AJAX requests
     */
    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        PrintWriter out = response.getWriter();
        out.print("{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}");
        out.flush();
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
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        User user = null;
        
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        
        // Lấy URL được yêu cầu để redirect sau khi login
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Kiểm tra nếu user chưa đăng nhập
        if (user == null) {
            // Kiểm tra nếu là AJAX request
            if (isAjaxRequest(httpRequest)) {
                sendJsonError(httpResponse, "Bạn cần đăng nhập để thực hiện thao tác này");
                return;
            }
            
            // Lưu URL để redirect sau khi login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", path);
            
            // Redirect đến trang login
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        // Kiểm tra email đã được xác minh chưa (trừ admin)
        if (!user.isAdmin() && !user.isEmailVerified()) {
            // Kiểm tra nếu là AJAX request
            if (isAjaxRequest(httpRequest)) {
                sendJsonError(httpResponse, "Vui lòng xác minh email trước khi sử dụng dịch vụ");
                return;
            }
            
            // Email chưa được xác minh, redirect đến trang verify-email
            request.setAttribute("message", "Vui lòng xác minh email trước khi sử dụng dịch vụ.");
            request.setAttribute("messageType", "warning");
            request.setAttribute("email", user.getEmail());
            
            // Lưu URL để redirect sau khi verify
            session.setAttribute("redirectAfterVerify", path);
            
            try {
                httpRequest.getRequestDispatcher("/views/auth/verify-email.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
                httpResponse.sendRedirect(contextPath + "/verify-email");
            }
            return;
        }
        
        // Nếu đã đăng nhập và email đã được xác minh, cho phép tiếp tục
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Không cần cleanup gì
    }
}



