package com.ecommerce.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.mail.*;
import jakarta.mail.internet.*;

/**
 * Email Service
 * Gửi email xác minh cho người dùng
 * Sử dụng Gmail SMTP (miễn phí và ổn định)
 */
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    
    // Cache cho config properties từ file
    private static Properties configProperties = null;
    private static boolean configLoaded = false;
    
    /**
     * Load cấu hình từ file config.properties
     * File này nên được đặt trong thư mục src/java/resources hoặc WEB-INF/classes
     */
    private static synchronized void loadConfigProperties() {
        if (configLoaded) {
            return; // Đã load rồi, không load lại
        }
        
        configProperties = new Properties();
        try {
            // Thử load từ classpath (src/java/resources/config.properties hoặc WEB-INF/classes/config.properties)
            InputStream inputStream = EmailService.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream != null) {
                configProperties.load(inputStream);
                inputStream.close();
                LOGGER.log(Level.INFO, "Đã load cấu hình từ file config.properties");
            } else {
                LOGGER.log(Level.FINE, "Không tìm thấy file config.properties trong classpath");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể load file config.properties: " + e.getMessage());
        }
        
        configLoaded = true;
    }
    
    // Lấy cấu hình SMTP từ System Properties, Environment Variables, hoặc file config.properties
    private static String getConfig(String name, String defaultValue) {
        // 1. Thử lấy từ System Properties trước (ưu tiên cao nhất)
        String value = System.getProperty(name);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // 2. Fallback: lấy từ Environment Variables (thử cả dấu chấm và dấu gạch dưới)
        value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            // Thử với tên biến không có dấu chấm (Windows không hỗ trợ dấu chấm trong env vars)
            String envName = name.toUpperCase().replace(".", "_");
            value = System.getenv(envName);
        }
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // 3. Fallback: lấy từ file config.properties
        loadConfigProperties();
        if (configProperties != null) {
            value = configProperties.getProperty(name);
            if (value != null && !value.isEmpty()) {
                LOGGER.log(Level.FINE, "Đã lấy cấu hình '" + name + "' từ file config.properties");
                return value;
            }
        }
        
        // Default values cho development/testing
        // LƯU Ý: Trong production, PHẢI set environment variables hoặc system properties
        // KHÔNG nên hardcode credentials trong code
        // TODO: Xóa hoặc comment phần này khi deploy production
        switch (name) {
            case "smtp.host":
                return defaultValue != null ? defaultValue : "smtp.gmail.com";
            case "smtp.port":
                return defaultValue != null ? defaultValue : "587";
            case "smtp.user":
                // KHÔNG có default value - PHẢI cấu hình trong production
                LOGGER.log(Level.WARNING, "smtp.user chưa được cấu hình. Email service sẽ không hoạt động!");
                return defaultValue;
            case "smtp.password":
                // KHÔNG có default value - PHẢI cấu hình trong production
                LOGGER.log(Level.WARNING, "smtp.password chưa được cấu hình. Email service sẽ không hoạt động!");
                return defaultValue;
            case "email.from":
                return defaultValue;
            case "email.from.name":
                return defaultValue != null ? defaultValue : "Ecommerce App";
            default:
                return defaultValue;
        }
    }
    
    /**
     * Gửi email xác minh
     * @param toEmail Email người nhận
     * @param username Tên người dùng
     * @param verificationToken Token xác minh
     * @param baseUrl URL gốc của website (để tạo link xác minh)
     * @return true nếu gửi thành công, false nếu có lỗi
     */
    public static boolean sendVerificationEmail(String toEmail, String username, String verificationToken, String baseUrl) {
        LOGGER.log(Level.INFO, "Bắt đầu gửi email xác minh đến: " + toEmail);
        
        // Lấy cấu hình động mỗi lần gửi email
        String smtpHost = getConfig("smtp.host", "smtp.gmail.com");
        String smtpPort = getConfig("smtp.port", "587");
        String smtpUser = getConfig("smtp.user", "");
        String smtpPassword = getConfig("smtp.password", "");
        String fromEmail = getConfig("email.from", smtpUser);
        String fromName = getConfig("email.from.name", "Ecommerce App");
        
        // Log cấu hình (ẩn password)
        LOGGER.log(Level.INFO, "SMTP Config - Host: " + smtpHost + ", Port: " + smtpPort + ", User: " + smtpUser);
        LOGGER.log(Level.INFO, "SMTP Config - From: " + fromEmail + ", FromName: " + fromName);
        
        if (smtpUser == null || smtpUser.isEmpty() || smtpPassword == null || smtpPassword.isEmpty()) {
            LOGGER.log(Level.SEVERE, "SMTP credentials chưa được cấu hình. smtpUser: '" + smtpUser + "', smtpPassword: '" + 
                      (smtpPassword != null && !smtpPassword.isEmpty() ? "***" : "null/empty") + "'");
            return false;
        }
        
        try {
            // Tạo link xác minh
            // Token đã là URL-safe Base64 (từ Base64.getUrlEncoder()), không cần encode thêm
            // Nếu encode thêm sẽ làm token không khớp với database
            String verificationLink = baseUrl + "/verify-email?token=" + verificationToken;
            LOGGER.log(Level.INFO, "Verification token: " + verificationToken);
            LOGGER.log(Level.INFO, "Verification link: " + verificationLink);
            
            // Nội dung email
            String subject = "Xác minh email đăng ký tài khoản";
            String htmlContent = buildVerificationEmailHtml(username, verificationLink);
            
            // Gửi email
            boolean result = sendEmail(toEmail, subject, htmlContent, smtpHost, smtpPort, smtpUser, smtpPassword, fromEmail, fromName);
            if (result) {
                LOGGER.log(Level.INFO, "Email xác minh đã được gửi thành công đến: " + toEmail);
            } else {
                LOGGER.log(Level.SEVERE, "Email xác minh KHÔNG được gửi đến: " + toEmail);
            }
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi email xác minh đến " + toEmail, e);
            e.printStackTrace(); // In stack trace để debug
            return false;
        }
    }
    
    /**
     * Gửi email thông thường
     */
    private static boolean sendEmail(String toEmail, String subject, String htmlContent,
                                    String smtpHost, String smtpPort, String smtpUser, 
                                    String smtpPassword, String fromEmail, String fromName) {
        try {
            LOGGER.log(Level.INFO, "Đang cấu hình SMTP properties...");
            
            // Cấu hình properties
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", smtpHost);
            // Tăng timeout để tránh timeout quá nhanh (30 giây)
            props.put("mail.smtp.connectiontimeout", "30000");
            props.put("mail.smtp.timeout", "30000");
            props.put("mail.smtp.writetimeout", "30000");
            // Bật debug mode để xem chi tiết (có thể tắt trong production)
            props.put("mail.debug", "false"); // Set true để debug
            
            LOGGER.log(Level.INFO, "Đang tạo SMTP session...");
            
            // Tạo session với authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    LOGGER.log(Level.FINE, "Đang xác thực với SMTP server...");
                    return new PasswordAuthentication(smtpUser, smtpPassword);
                }
            });
            
            LOGGER.log(Level.INFO, "Đang tạo email message...");
            
            // Tạo message
            Message message = new MimeMessage(session);
            // Encode fromName với UTF-8
            message.setFrom(new InternetAddress(fromEmail, fromName, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            // Encode subject với UTF-8 để hỗ trợ tiếng Việt
            try {
                message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
            } catch (java.io.UnsupportedEncodingException e) {
                // Fallback: set subject trực tiếp nếu encode thất bại
                LOGGER.log(Level.WARNING, "Không thể encode subject với UTF-8, sử dụng subject gốc: " + e.getMessage());
                message.setSubject(subject);
            }
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            
            LOGGER.log(Level.INFO, "Đang gửi email qua Transport...");
            
            // Gửi email
            Transport.send(message);
            
            LOGGER.log(Level.INFO, "Đã gửi email xác minh thành công đến " + toEmail);
            return true;
            
        } catch (AddressException e) {
            LOGGER.log(Level.SEVERE, "Lỗi địa chỉ email không hợp lệ: " + toEmail, e);
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Lỗi MessagingException khi gửi email đến " + toEmail, e);
            LOGGER.log(Level.SEVERE, "Exception message: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Exception class: " + e.getClass().getName());
            if (e.getCause() != null) {
                LOGGER.log(Level.SEVERE, "Cause: " + e.getCause().getClass().getName() + " - " + e.getCause().getMessage());
                if (e.getCause().getCause() != null) {
                    LOGGER.log(Level.SEVERE, "Root cause: " + e.getCause().getCause().getClass().getName() + " - " + e.getCause().getCause().getMessage());
                }
            }
            // Log thêm thông tin về SMTP config để debug
            LOGGER.log(Level.SEVERE, "SMTP Config khi lỗi - Host: " + smtpHost + ", Port: " + smtpPort + ", User: " + smtpUser);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi không mong đợi khi gửi email đến " + toEmail, e);
            LOGGER.log(Level.SEVERE, "Exception type: " + e.getClass().getName());
            LOGGER.log(Level.SEVERE, "Exception message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gửi email reset password
     * @param toEmail Email người nhận
     * @param username Tên người dùng
     * @param resetToken Token reset password
     * @param baseUrl URL gốc của website (để tạo link reset)
     * @return true nếu gửi thành công, false nếu có lỗi
     */
    public static boolean sendPasswordResetEmail(String toEmail, String username, String resetToken, String baseUrl) {
        LOGGER.log(Level.INFO, "Bắt đầu gửi email reset password đến: " + toEmail);
        
        // Lấy cấu hình động mỗi lần gửi email
        String smtpHost = getConfig("smtp.host", "smtp.gmail.com");
        String smtpPort = getConfig("smtp.port", "587");
        String smtpUser = getConfig("smtp.user", "");
        String smtpPassword = getConfig("smtp.password", "");
        String fromEmail = getConfig("email.from", smtpUser);
        String fromName = getConfig("email.from.name", "Ecommerce App");
        
        // Log cấu hình (ẩn password)
        LOGGER.log(Level.INFO, "SMTP Config - Host: " + smtpHost + ", Port: " + smtpPort + ", User: " + smtpUser);
        LOGGER.log(Level.INFO, "SMTP Config - From: " + fromEmail + ", FromName: " + fromName);
        
        if (smtpUser == null || smtpUser.isEmpty() || smtpPassword == null || smtpPassword.isEmpty()) {
            LOGGER.log(Level.SEVERE, "SMTP credentials chưa được cấu hình. smtpUser: '" + smtpUser + "', smtpPassword: '" + 
                      (smtpPassword != null && !smtpPassword.isEmpty() ? "***" : "null/empty") + "'");
            return false;
        }
        
        try {
            // Tạo link reset password
            String resetLink = baseUrl + "/reset-password?token=" + resetToken;
            LOGGER.log(Level.INFO, "Reset token: " + resetToken);
            LOGGER.log(Level.INFO, "Reset link: " + resetLink);
            
            // Nội dung email
            String subject = "Đặt lại mật khẩu";
            String htmlContent = buildPasswordResetEmailHtml(username, resetLink);
            
            // Gửi email
            boolean result = sendEmail(toEmail, subject, htmlContent, smtpHost, smtpPort, smtpUser, smtpPassword, fromEmail, fromName);
            if (result) {
                LOGGER.log(Level.INFO, "Email reset password đã được gửi thành công đến: " + toEmail);
            } else {
                LOGGER.log(Level.SEVERE, "Email reset password KHÔNG được gửi đến: " + toEmail);
            }
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi email reset password đến " + toEmail, e);
            e.printStackTrace(); // In stack trace để debug
            return false;
        }
    }
    
    /**
     * Tạo HTML content cho email xác minh
     */
    private static String buildVerificationEmailHtml(String username, String verificationLink) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<style>" +
               "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
               ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
               ".header { background-color: #007bff; color: white; padding: 20px; text-align: center; }" +
               ".content { background-color: #f8f9fa; padding: 30px; }" +
               ".button { display: inline-block; padding: 12px 30px; background-color: #007bff; " +
               "color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
               ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>Xác minh Email</h1>" +
               "</div>" +
               "<div class='content'>" +
               "<p>Xin chào <strong>" + escapeHtml(username) + "</strong>,</p>" +
               "<p>Cảm ơn bạn đã đăng ký tài khoản tại Ecommerce Store!</p>" +
               "<p>Vui lòng click vào nút bên dưới để xác minh email của bạn:</p>" +
               "<div style='text-align: center;'>" +
               "<a href='" + verificationLink + "' class='button'>Xác minh Email</a>" +
               "</div>" +
               "<p>Hoặc copy và paste link sau vào trình duyệt:</p>" +
               "<p style='word-break: break-all; color: #007bff;'>" + verificationLink + "</p>" +
               "<p><strong>Lưu ý:</strong> Link này sẽ hết hạn sau 24 giờ.</p>" +
               "<p>Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.</p>" +
               "</div>" +
               "<div class='footer'>" +
               "<p>© 2024 Ecommerce Store. All rights reserved.</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Tạo HTML content cho email reset password
     */
    private static String buildPasswordResetEmailHtml(String username, String resetLink) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<style>" +
               "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
               ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
               ".header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }" +
               ".content { background-color: #f8f9fa; padding: 30px; }" +
               ".button { display: inline-block; padding: 12px 30px; background-color: #dc3545; " +
               "color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
               ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
               ".warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>Đặt lại Mật khẩu</h1>" +
               "</div>" +
               "<div class='content'>" +
               "<p>Xin chào <strong>" + escapeHtml(username) + "</strong>,</p>" +
               "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>" +
               "<p>Vui lòng click vào nút bên dưới để đặt lại mật khẩu:</p>" +
               "<div style='text-align: center;'>" +
               "<a href='" + resetLink + "' class='button'>Đặt lại Mật khẩu</a>" +
               "</div>" +
               "<p>Hoặc copy và paste link sau vào trình duyệt:</p>" +
               "<p style='word-break: break-all; color: #dc3545;'>" + resetLink + "</p>" +
               "<div class='warning'>" +
               "<p><strong>Lưu ý quan trọng:</strong></p>" +
               "<ul>" +
               "<li>Link này sẽ hết hạn sau 1 giờ.</li>" +
               "<li>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</li>" +
               "<li>Để bảo mật tài khoản, không chia sẻ link này với bất kỳ ai.</li>" +
               "</ul>" +
               "</div>" +
               "</div>" +
               "<div class='footer'>" +
               "<p>© 2024 Ecommerce Store. All rights reserved.</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Escape HTML để tránh XSS
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

