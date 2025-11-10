package com.ecommerce.controller;

import com.ecommerce.dao.PasswordResetDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.EmailService;
import com.ecommerce.util.TokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Forgot Password Servlet
 * Xử lý yêu cầu quên mật khẩu - gửi email reset password
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {
    
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
        
        // Hiển thị trang forgot password
        request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String error = null;
        String success = null;
        
        // Validation
        if (email == null || email.trim().isEmpty()) {
            error = "Vui lòng nhập email";
        } else if (!email.contains("@")) {
            error = "Email không hợp lệ";
        } else {
            // Tìm user theo email
            User user = userDAO.getUserByEmail(email.trim());
            
            if (user == null) {
                // Không tìm thấy user, nhưng vẫn hiển thị thông báo thành công để bảo mật
                // (không tiết lộ email có tồn tại hay không)
                success = "Nếu email này đã được đăng ký, chúng tôi đã gửi link đặt lại mật khẩu đến email của bạn. " +
                         "Vui lòng kiểm tra hộp thư (cả thư mục spam).";
            } else if (!user.isActive()) {
                error = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ hỗ trợ.";
            } else {
                // Tạo token reset password
                String resetToken = TokenUtil.generateVerificationToken();
                
                // Lưu token vào database
                if (passwordResetDAO.createPasswordResetToken(user.getUserId(), resetToken)) {
                    // Gửi email reset password
                    String baseUrl = request.getScheme() + "://" + request.getServerName() + 
                                    (request.getServerPort() != 80 && request.getServerPort() != 443 ? 
                                     ":" + request.getServerPort() : "") + 
                                    request.getContextPath();
                    
                    boolean emailSent = false;
                    try {
                        emailSent = EmailService.sendPasswordResetEmail(
                            user.getEmail(), 
                            user.getUsername(), 
                            resetToken, 
                            baseUrl
                        );
                    } catch (Exception e) {
                        System.err.println("Lỗi khi gửi email reset password: " + e.getMessage());
                        e.printStackTrace();
                        emailSent = false;
                    }
                    
                    if (emailSent) {
                        success = "Chúng tôi đã gửi link đặt lại mật khẩu đến email của bạn. " +
                                 "Vui lòng kiểm tra hộp thư (cả thư mục spam). " +
                                 "Link sẽ hết hạn sau 1 giờ.";
                    } else {
                        error = "Có lỗi xảy ra khi gửi email. Vui lòng thử lại sau hoặc liên hệ hỗ trợ.";
                    }
                } else {
                    error = "Có lỗi xảy ra khi tạo token reset password. Vui lòng thử lại sau.";
                }
            }
        }
        
        // Set attributes và forward
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("email", email);
        } else {
            request.setAttribute("success", success);
        }
        
        request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
    }
}

