package com.ecommerce.controller;

import com.ecommerce.dao.PendingRegistrationDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.EmailService;
import com.ecommerce.util.PasswordUtil;
import com.ecommerce.util.TokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
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
        
        // Xử lý logout qua GET request
        if ("/logout".equals(path)) {
            handleLogout(request, response);
            return;
        }
        
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
            } else if (!user.isEmailVerified() && !user.isAdmin()) {
                // Email chưa được xác minh, không cho phép đăng nhập
                error = "Vui lòng xác minh email trước khi đăng nhập. Kiểm tra email để lấy link xác minh.";
                // Lưu email vào request để hiển thị trong trang login
                request.setAttribute("email", user.getEmail());
                request.setAttribute("unverifiedEmail", true);
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
            try {
                // Tạo password hash và verification token
                String passwordHash = PasswordUtil.hashPassword(password);
                String verificationToken = TokenUtil.generateVerificationToken();
                
                // Lưu thông tin đăng ký vào pending_registrations (chưa tạo user trong DB)
                PendingRegistrationDAO pendingDAO = new PendingRegistrationDAO();
                
                if (pendingDAO.addPendingRegistration(username, email, passwordHash, fullName, phone, address, verificationToken)) {
                    // Gửi email xác minh
                    String baseUrl = request.getScheme() + "://" + request.getServerName() + 
                                    (request.getServerPort() != 80 && request.getServerPort() != 443 ? 
                                     ":" + request.getServerPort() : "") + 
                                    request.getContextPath();
                    
                    boolean emailSent = false;
                    try {
                        emailSent = EmailService.sendVerificationEmail(
                            email, 
                            username, 
                            verificationToken, 
                            baseUrl
                        );
                    } catch (Exception e) {
                        // Log lỗi nhưng không dừng quá trình đăng ký
                        System.err.println("Lỗi khi gửi email xác minh: " + e.getMessage());
                        e.printStackTrace();
                        emailSent = false;
                    }
                    
                    // Lưu thông báo vào session để hiển thị trên trang verify-email
                    HttpSession session = request.getSession();
                    
                    if (emailSent) {
                        // Email đã được gửi, lưu thông báo vào session và redirect
                        session.setAttribute("verifyMessage", 
                            "Đăng ký thành công! Vui lòng kiểm tra email để xác minh tài khoản.");
                        session.setAttribute("verifyMessageType", "success");
                        session.setAttribute("verifyEmail", email);
                        response.sendRedirect(request.getContextPath() + "/verify-email");
                        return;
                    } else {
                        // Email không gửi được, nhưng vẫn lưu thông tin đăng ký
                        session.setAttribute("verifyMessage", 
                            "Đăng ký thành công! Tuy nhiên, email xác minh chưa được gửi. " +
                            "Vui lòng liên hệ hỗ trợ hoặc thử lại sau.");
                        session.setAttribute("verifyMessageType", "warning");
                        session.setAttribute("verifyEmail", email);
                        response.sendRedirect(request.getContextPath() + "/verify-email");
                        return;
                    }
                } else {
                    error = "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.";
                }
            } catch (Exception e) {
                // Bắt tất cả các exception và log ra
                System.err.println("Lỗi khi đăng ký user: " + e.getMessage());
                e.printStackTrace();
                error = "Có lỗi xảy ra khi đăng ký: " + e.getMessage() + ". Vui lòng thử lại.";
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
            // Xóa tất cả attributes trong session
            session.removeAttribute("user");
            session.removeAttribute("redirectAfterLogin");
            
            // Invalidate session
            session.invalidate();
        }
        
        // Xóa JSESSIONID cookie bằng cách tạo cookie mới với maxAge = 0
        // Xóa với cả path context và root path để đảm bảo
        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.isEmpty()) {
            contextPath = "/";
        }
        
        // Xóa cookie với context path
        Cookie deleteCookie = new Cookie("JSESSIONID", "");
        deleteCookie.setPath(contextPath);
        deleteCookie.setMaxAge(0);
        deleteCookie.setHttpOnly(true);
        response.addCookie(deleteCookie);
        
        // Nếu contextPath khác "/", cũng xóa với root path
        if (!"/".equals(contextPath)) {
            Cookie deleteRootCookie = new Cookie("JSESSIONID", "");
            deleteRootCookie.setPath("/");
            deleteRootCookie.setMaxAge(0);
            deleteRootCookie.setHttpOnly(true);
            response.addCookie(deleteRootCookie);
        }
        
        response.sendRedirect(request.getContextPath() + "/home");
    }
}



