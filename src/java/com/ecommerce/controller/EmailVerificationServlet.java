package com.ecommerce.controller;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Email Verification Servlet
 * Xử lý xác minh email cho người dùng
 */
@WebServlet(name = "EmailVerificationServlet", urlPatterns = {"/verify-email"})
public class EmailVerificationServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String token = request.getParameter("token");
            
            // Log token nhận được để debug
            System.out.println("=== EMAIL VERIFICATION DEBUG ===");
            System.out.println("Token received (raw from request): " + token);
            
            // Xử lý token: decode nếu cần (browser có thể đã decode một phần)
            if (token != null && !token.trim().isEmpty()) {
                try {
                    // Token có thể đã được URL encode trong email (nếu có lỗi từ code cũ)
                    // Hoặc browser đã tự động decode
                    // Thử decode nhiều lần nếu cần (để xử lý cả trường hợp double-encoded)
                    String originalToken = token;
                    String decodedToken = token;
                    
                    // Decode tối đa 3 lần để xử lý các trường hợp encode nhiều lần
                    for (int i = 0; i < 3; i++) {
                        if (decodedToken.contains("%")) {
                            String temp = java.net.URLDecoder.decode(decodedToken, "UTF-8");
                            if (temp.equals(decodedToken)) {
                                // Không thay đổi sau khi decode, dừng lại
                                break;
                            }
                            decodedToken = temp;
                            System.out.println("Token decoded (iteration " + (i + 1) + "): " + decodedToken);
                        } else {
                            // Không có dấu %, không cần decode nữa
                            break;
                        }
                    }
                    
                    token = decodedToken;
                    if (!originalToken.equals(token)) {
                        System.out.println("Token đã được decode từ: " + originalToken + " -> " + token);
                    } else {
                        System.out.println("Token không cần decode: " + token);
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi khi decode token: " + e.getMessage());
                    e.printStackTrace();
                    // Nếu decode lỗi, giữ nguyên token gốc
                }
            }
            
            if (token == null || token.trim().isEmpty()) {
                // Không có token, kiểm tra session để lấy thông báo từ đăng ký
                HttpSession session = request.getSession(false);
                
                // Luôn khởi tạo các attributes với giá trị mặc định
                String message = "Vui lòng kiểm tra email để lấy link xác minh.";
                String messageType = "info";
                String email = null;
                
                if (session != null) {
                    String verifyMessage = (String) session.getAttribute("verifyMessage");
                    String verifyMessageType = (String) session.getAttribute("verifyMessageType");
                    String verifyEmail = (String) session.getAttribute("verifyEmail");
                    
                    if (verifyMessage != null && !verifyMessage.trim().isEmpty()) {
                        // Có thông báo từ session, sử dụng nó
                        message = verifyMessage;
                        messageType = (verifyMessageType != null && !verifyMessageType.trim().isEmpty()) 
                                    ? verifyMessageType : "info";
                        // Xóa thông báo khỏi session sau khi đã lấy
                        session.removeAttribute("verifyMessage");
                        session.removeAttribute("verifyMessageType");
                    }
                    
                    // Lấy email từ session nếu có
                    if (verifyEmail != null && !verifyEmail.trim().isEmpty()) {
                        email = verifyEmail;
                    }
                }
                
                // Luôn set attributes vào request
                request.setAttribute("message", message);
                request.setAttribute("messageType", messageType);
                if (email != null) {
                    request.setAttribute("email", email);
                }
                
                // Forward đến verify-email.jsp
                System.out.println("=== FORWARDING TO VERIFY-EMAIL.JSP (NO TOKEN) ===");
                System.out.println("Message: " + message);
                System.out.println("MessageType: " + messageType);
                System.out.println("Email: " + email);
                
                request.getRequestDispatcher("/views/auth/verify-email.jsp").forward(request, response);
                return;
            }
            
            // Có token, xử lý xác minh email
            System.out.println("Đang tìm user với token: " + token);
            User user = userDAO.getUserByVerificationToken(token);
            
            if (user == null) {
                // Token không hợp lệ hoặc đã hết hạn
                System.out.println("KHÔNG TÌM THẤY user với token này!");
                System.out.println("Token length: " + (token != null ? token.length() : 0));
                
                request.setAttribute("message", "Link xác minh không hợp lệ hoặc đã hết hạn. Vui lòng đăng ký lại hoặc liên hệ hỗ trợ.");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/views/auth/verify-email.jsp").forward(request, response);
                return;
            }
            
            System.out.println("Tìm thấy user: " + user.getUsername() + " (ID: " + user.getUserId() + ")");
            System.out.println("User email verified: " + user.isEmailVerified());
            
            if (user.isEmailVerified()) {
                // Email đã được xác minh rồi
                request.setAttribute("message", "Email của bạn đã được xác minh rồi. Bạn có thể đăng nhập ngay.");
                request.setAttribute("messageType", "success");
                request.getRequestDispatcher("/views/auth/verify-email.jsp").forward(request, response);
                return;
            }
            
            // Xác minh email
            boolean verified = userDAO.verifyEmail(token);
            
            if (verified) {
                // Xác minh thành công
                // Cập nhật user trong session nếu đang đăng nhập
                HttpSession session = request.getSession(false);
                if (session != null) {
                    User sessionUser = (User) session.getAttribute("user");
                    if (sessionUser != null && sessionUser.getUserId() == user.getUserId()) {
                        user.setEmailVerified(true);
                        session.setAttribute("user", user);
                        
                        // Redirect đến URL được lưu hoặc về home
                        String redirectUrl = (String) session.getAttribute("redirectAfterVerify");
                        if (redirectUrl != null && !redirectUrl.isEmpty()) {
                            session.removeAttribute("redirectAfterVerify");
                            response.sendRedirect(request.getContextPath() + redirectUrl);
                            return;
                        }
                    }
                }
                
                request.setAttribute("message", "Email đã được xác minh thành công! Bạn có thể đăng nhập ngay.");
                request.setAttribute("messageType", "success");
            } else {
                // Xác minh thất bại
                request.setAttribute("message", "Có lỗi xảy ra khi xác minh email. Vui lòng thử lại sau.");
                request.setAttribute("messageType", "error");
            }
            
            // Đảm bảo luôn có message và messageType
            if (request.getAttribute("message") == null) {
                request.setAttribute("message", "Vui lòng kiểm tra email để lấy link xác minh.");
            }
            if (request.getAttribute("messageType") == null) {
                request.setAttribute("messageType", "info");
            }
            
            // Forward đến verify-email.jsp
            System.out.println("=== FORWARDING TO VERIFY-EMAIL.JSP (WITH TOKEN) ===");
            System.out.println("Message: " + request.getAttribute("message"));
            System.out.println("MessageType: " + request.getAttribute("messageType"));
            
            request.getRequestDispatcher("/views/auth/verify-email.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Bắt tất cả các exception và log ra
            System.err.println("=== LỖI TRONG EMAIL VERIFICATION SERVLET ===");
            System.err.println("Exception: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            // Kiểm tra xem response đã được commit chưa
            if (!response.isCommitted()) {
                // Thay vì forward đến error.jsp, hiển thị thông báo lỗi trong chính trang verify-email
                // Điều này tránh vấn đề khi include header/navbar gây exception
                request.setAttribute("message", "Có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại sau hoặc liên hệ hỗ trợ.");
                request.setAttribute("messageType", "error");
                
                // Đảm bảo email được set nếu có trong session
                try {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        String verifyEmail = (String) session.getAttribute("verifyEmail");
                        if (verifyEmail != null && !verifyEmail.trim().isEmpty()) {
                            request.setAttribute("email", verifyEmail);
                        }
                    }
                } catch (Exception sessionEx) {
                    System.err.println("Lỗi khi lấy email từ session: " + sessionEx.getMessage());
                }
                
                try {
                    // Forward đến verify-email.jsp với thông báo lỗi
                    request.getRequestDispatcher("/views/auth/verify-email.jsp").forward(request, response);
                } catch (Exception forwardEx) {
                    System.err.println("Lỗi khi forward đến verify-email.jsp: " + forwardEx.getMessage());
                    forwardEx.printStackTrace();
                    
                    // Nếu không thể forward đến verify-email.jsp, thử forward đến error.jsp
                    try {
                        request.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại sau.");
                        request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                    } catch (Exception errorPageEx) {
                        System.err.println("Lỗi khi forward đến error page: " + errorPageEx.getMessage());
                        errorPageEx.printStackTrace();
                        // Cuối cùng, gửi response lỗi trực tiếp
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                         "Có lỗi xảy ra khi xử lý yêu cầu.");
                    }
                }
            } else {
                // Response đã được commit, không thể forward, chỉ log lỗi
                System.err.println("Response đã được commit, không thể forward đến error page");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Xử lý resend verification email
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Resend email sẽ được xử lý trong AuthServlet
        response.sendRedirect(request.getContextPath() + "/register?resend=true");
    }
}

