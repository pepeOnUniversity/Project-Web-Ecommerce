package com.ecommerce.filter;

import com.ecommerce.model.User;
import java.io.IOException;
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
            // Lưu URL để redirect sau khi login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", path);
            
            // Redirect đến trang login
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        // Nếu đã đăng nhập, cho phép tiếp tục
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Không cần cleanup gì
    }
}



