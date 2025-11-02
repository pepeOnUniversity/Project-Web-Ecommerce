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
 * Authentication Servlet
 * Xử lý đăng nhập, đăng ký, đăng xuất
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        // Kiểm tra nếu đã đăng nhập, redirect về home
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        if ("/login".equals(path)) {
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
        } else if ("/register".equals(path)) {
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/login".equals(path)) {
            handleLogin(request, response);
        } else if ("/register".equals(path)) {
            handleRegister(request, response);
        } else if ("/logout".equals(path)) {
            handleLogout(request, response);
        }
    }
    
    /**
     * Xử lý đăng nhập
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        String error = null;
        
        if (username == null || username.trim().isEmpty()) {
            error = "Vui lòng nhập username";
        } else if (password == null || password.isEmpty()) {
            error = "Vui lòng nhập password";
        } else {
            User user = userDAO.getUserByUsername(username);
            
            if (user == null) {
                error = "Username hoặc password không đúng";
            } else if (!user.isActive()) {
                error = "Tài khoản của bạn đã bị khóa";
            } else if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                error = "Username hoặc password không đúng";
            } else {
                // Đăng nhập thành công
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // Remember me - set session timeout dài hơn
                if ("on".equals(rememberMe)) {
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
                }
                
                // Redirect đến URL được lưu hoặc về home
                String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    session.removeAttribute("redirectAfterLogin");
                    response.sendRedirect(request.getContextPath() + redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/home");
                }
                return;
            }
        }
        
        request.setAttribute("error", error);
        request.setAttribute("username", username);
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }
    
    /**
     * Xử lý đăng ký
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        String error = null;
        
        // Validation
        if (username == null || username.trim().isEmpty()) {
            error = "Vui lòng nhập username";
        } else if (userDAO.isUsernameExists(username)) {
            error = "Username đã tồn tại";
        } else if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            error = "Vui lòng nhập email hợp lệ";
        } else if (userDAO.isEmailExists(email)) {
            error = "Email đã tồn tại";
        } else if (password == null || password.length() < 6) {
            error = "Password phải có ít nhất 6 ký tự";
        } else if (!password.equals(confirmPassword)) {
            error = "Password và Confirm Password không khớp";
        } else if (fullName == null || fullName.trim().isEmpty()) {
            error = "Vui lòng nhập họ tên";
        } else if (phone == null || phone.trim().isEmpty()) {
            error = "Vui lòng nhập số điện thoại";
        } else if (address == null || address.trim().isEmpty()) {
            error = "Vui lòng nhập địa chỉ";
        }
        
        if (error == null) {
            // Tạo user mới
            String passwordHash = PasswordUtil.hashPassword(password);
            User newUser = new User(username, email, passwordHash, fullName, phone, address);
            
            if (userDAO.addUser(newUser)) {
                // Đăng ký thành công, tự động đăng nhập
                User savedUser = userDAO.getUserByUsername(username);
                HttpSession session = request.getSession();
                session.setAttribute("user", savedUser);
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            } else {
                error = "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.";
            }
        }
        
        request.setAttribute("error", error);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("fullName", fullName);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);
        request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
    }
    
    /**
     * Xử lý đăng xuất
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/home");
    }
}


