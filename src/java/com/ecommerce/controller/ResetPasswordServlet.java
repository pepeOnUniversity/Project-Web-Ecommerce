package com.ecommerce.controller;

import com.ecommerce.dao.PasswordResetDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Reset Password Servlet
 * Xử lý đặt lại mật khẩu với token
 */
@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private PasswordResetDAO passwordResetDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        passwordResetDAO = new PasswordResetDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra nếu đã đăng nhập, redirect về home
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        String token = request.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        // Validate token
        int userId = passwordResetDAO.validateToken(token);
        
        if (userId == -1) {
            request.setAttribute("error", "Token không hợp lệ, đã hết hạn hoặc đã được sử dụng.");
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        // Token hợp lệ, hiển thị form reset password
        request.setAttribute("token", token);
        request.setAttribute("validToken", true);
        request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        String error = null;
        
        // Validation
        if (token == null || token.trim().isEmpty()) {
            error = "Token không hợp lệ.";
        } else if (password == null || password.length() < 6) {
            error = "Mật khẩu phải có ít nhất 6 ký tự.";
        } else if (!password.equals(confirmPassword)) {
            error = "Mật khẩu và xác nhận mật khẩu không khớp.";
        } else {
            // Validate token
            int userId = passwordResetDAO.validateToken(token);
            
            if (userId == -1) {
                error = "Token không hợp lệ, đã hết hạn hoặc đã được sử dụng.";
            } else {
                // Hash password mới
                String passwordHash = PasswordUtil.hashPassword(password);
                
                // Cập nhật password
                if (userDAO.updatePassword(userId, passwordHash)) {
                    // Đánh dấu token đã được sử dụng
                    passwordResetDAO.markTokenAsUsed(token);
                    
                    // Redirect đến trang login với thông báo thành công
                    jakarta.servlet.http.HttpSession session = request.getSession();
                    session.setAttribute("resetPasswordSuccess", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                } else {
                    error = "Có lỗi xảy ra khi cập nhật mật khẩu. Vui lòng thử lại sau.";
                }
            }
        }
        
        // Có lỗi, hiển thị lại form với thông báo lỗi
        request.setAttribute("error", error);
        request.setAttribute("token", token);
        request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
    }
}

