package com.ecommerce.controller;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Change Password Servlet
 * Xử lý đổi mật khẩu cho user đã đăng nhập
 */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/change-password"})
public class ChangePasswordServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Hiển thị form đổi mật khẩu
        request.getRequestDispatcher("/views/customer/change-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        String error = null;
        String success = null;
        
        // Validation
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            error = "Vui lòng nhập mật khẩu hiện tại.";
        } else if (newPassword == null || newPassword.length() < 6) {
            error = "Mật khẩu mới phải có ít nhất 6 ký tự.";
        } else if (!newPassword.equals(confirmPassword)) {
            error = "Mật khẩu mới và xác nhận mật khẩu không khớp.";
        } else {
            // Lấy user mới nhất từ database để đảm bảo có password hash mới nhất
            User user = userDAO.getUserById(currentUser.getUserId());
            if (user == null) {
                error = "Không tìm thấy thông tin người dùng.";
            } else {
                // Xác thực mật khẩu hiện tại
                if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
                    error = "Mật khẩu hiện tại không đúng.";
                } else {
                    // Kiểm tra mật khẩu mới không trùng với mật khẩu cũ
                    if (PasswordUtil.verifyPassword(newPassword, user.getPasswordHash())) {
                        error = "Mật khẩu mới phải khác với mật khẩu hiện tại.";
                    } else {
                        // Hash password mới
                        String passwordHash = PasswordUtil.hashPassword(newPassword);
                        
                        // Cập nhật password
                        if (userDAO.updatePassword(user.getUserId(), passwordHash)) {
                            success = "Đổi mật khẩu thành công!";
                            // Cập nhật user trong session với password hash mới
                            user.setPasswordHash(passwordHash);
                            session.setAttribute("user", user);
                        } else {
                            error = "Có lỗi xảy ra khi cập nhật mật khẩu. Vui lòng thử lại sau.";
                        }
                    }
                }
            }
        }
        
        // Set attributes và forward về trang change password
        if (error != null) {
            request.setAttribute("error", error);
        }
        if (success != null) {
            request.setAttribute("success", success);
        }
        
        request.getRequestDispatcher("/views/customer/change-password.jsp").forward(request, response);
    }
}

