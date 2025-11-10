package com.ecommerce.controller;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Profile Servlet
 * Xử lý xem và cập nhật thông tin profile của user đã đăng nhập
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Lấy thông tin user mới nhất từ database
        User user = userDAO.getUserById(currentUser.getUserId());
        if (user == null) {
            request.setAttribute("error", "Không tìm thấy thông tin người dùng.");
        } else {
            // Cập nhật user trong session với thông tin mới nhất
            session.setAttribute("user", user);
            request.setAttribute("user", user);
        }
        
        // Hiển thị trang profile
        request.getRequestDispatcher("/views/customer/profile.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Kiểm tra xem có phải request đổi mật khẩu không
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        String error = null;
        String success = null;
        
        // Nếu có thông tin đổi mật khẩu, xử lý đổi mật khẩu
        if (currentPassword != null || newPassword != null || confirmPassword != null) {
            // Xử lý đổi mật khẩu
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
        } else {
            // Xử lý cập nhật thông tin profile
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            
            // Validation
            if (fullName == null || fullName.trim().isEmpty()) {
                error = "Vui lòng nhập họ và tên.";
            } else if (email == null || email.trim().isEmpty()) {
                error = "Vui lòng nhập email.";
            } else if (!isValidEmail(email)) {
                error = "Email không hợp lệ.";
            } else {
                // Kiểm tra email đã được sử dụng bởi user khác chưa
                User existingUser = userDAO.getUserByEmail(email);
                if (existingUser != null && existingUser.getUserId() != currentUser.getUserId()) {
                    error = "Email này đã được sử dụng bởi tài khoản khác.";
                } else {
                    // Lấy user mới nhất từ database
                    User user = userDAO.getUserById(currentUser.getUserId());
                    if (user == null) {
                        error = "Không tìm thấy thông tin người dùng.";
                    } else {
                        // Cập nhật thông tin
                        user.setFullName(fullName.trim());
                        user.setEmail(email.trim());
                        user.setPhone(phone != null ? phone.trim() : "");
                        user.setAddress(address != null ? address.trim() : "");
                        
                        // Lưu vào database
                        if (userDAO.updateUser(user)) {
                            success = "Cập nhật thông tin thành công!";
                            // Cập nhật user trong session
                            session.setAttribute("user", user);
                        } else {
                            error = "Có lỗi xảy ra khi cập nhật thông tin. Vui lòng thử lại sau.";
                        }
                    }
                }
            }
        }
        
        // Set attributes
        if (error != null) {
            request.setAttribute("error", error);
        }
        if (success != null) {
            request.setAttribute("success", success);
        }
        
        // Lấy lại thông tin user mới nhất để hiển thị
        User user = userDAO.getUserById(currentUser.getUserId());
        if (user != null) {
            request.setAttribute("user", user);
            session.setAttribute("user", user);
        }
        
        // Forward về trang profile
        request.getRequestDispatcher("/views/customer/profile.jsp").forward(request, response);
    }
    
    /**
     * Kiểm tra email có hợp lệ không
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Regex pattern đơn giản để kiểm tra email
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailPattern);
    }
}

