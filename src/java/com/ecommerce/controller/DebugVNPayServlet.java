package com.ecommerce.controller;

import com.ecommerce.config.VNPayConfig;
import com.ecommerce.util.VNPayUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Debug VNPay Servlet
 * Hiển thị thông tin cấu hình VNPay để debug
 * Chỉ dùng trong development
 */
@WebServlet("/debug/vnpay")
public class DebugVNPayServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>VNPay Debug Info</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("h1 { color: #333; }");
        out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
        out.println("th { background-color: #4CAF50; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f2f2; }");
        out.println(".success { color: green; font-weight: bold; }");
        out.println(".error { color: red; font-weight: bold; }");
        out.println(".warning { color: orange; font-weight: bold; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>🔍 VNPay Configuration Debug</h1>");
        
        // TMN Code
        String tmnCode = VNPayConfig.getTmnCode();
        out.println("<h2>1. TMN Code (Terminal ID)</h2>");
        out.println("<table>");
        out.println("<tr><th>Property</th><th>Value</th><th>Status</th></tr>");
        out.println("<tr>");
        out.println("<td>vnp_TmnCode</td>");
        out.println("<td><strong>" + tmnCode + "</strong></td>");
        if ("OXPI7X5A".equals(tmnCode)) {
            out.println("<td class='success'>✅ Đúng</td>");
        } else if ("YOUR_TMN_CODE_HERE".equals(tmnCode)) {
            out.println("<td class='error'>❌ Chưa cấu hình</td>");
        } else {
            out.println("<td class='warning'>⚠️ Khác với giá trị mong đợi</td>");
        }
        out.println("</tr>");
        out.println("</table>");
        
        // Hash Secret
        String hashSecret = VNPayConfig.getHashSecret();
        out.println("<h2>2. Hash Secret</h2>");
        out.println("<table>");
        out.println("<tr><th>Property</th><th>Value</th><th>Status</th></tr>");
        out.println("<tr>");
        out.println("<td>vnp_HashSecret</td>");
        out.println("<td><strong>" + (hashSecret != null && hashSecret.length() > 10 ? 
                hashSecret.substring(0, 10) + "..." : hashSecret) + "</strong> (Độ dài: " + 
                (hashSecret != null ? hashSecret.length() : 0) + " ký tự)</td>");
        if (hashSecret != null && hashSecret.length() == 32) {
            out.println("<td class='success'>✅ Đúng (32 ký tự)</td>");
        } else if ("YOUR_HASH_SECRET_HERE".equals(hashSecret)) {
            out.println("<td class='error'>❌ Chưa cấu hình</td>");
        } else {
            out.println("<td class='warning'>⚠️ Độ dài không đúng (mong đợi: 32 ký tự)</td>");
        }
        out.println("</tr>");
        out.println("</table>");
        
        // Payment URL
        out.println("<h2>3. Payment URL</h2>");
        out.println("<table>");
        out.println("<tr><th>Property</th><th>Value</th></tr>");
        out.println("<tr><td>VNPAY_PAYMENT_URL</td><td>" + VNPayConfig.VNPAY_PAYMENT_URL + "</td></tr>");
        out.println("</table>");
        
        // Return URL và IPN URL
        String returnUrl = VNPayConfig.getReturnUrl(request);
        String ipnUrl = VNPayConfig.getIpnUrl(request);
        out.println("<h2>4. Callback URLs</h2>");
        out.println("<table>");
        out.println("<tr><th>Property</th><th>Value</th><th>Status</th></tr>");
        out.println("<tr>");
        out.println("<td>Return URL</td>");
        out.println("<td><strong>" + returnUrl + "</strong></td>");
        if (returnUrl.contains("ngrok") || returnUrl.contains("localhost")) {
            out.println("<td class='warning'>⚠️ Localhost/Ngrok - Cần cấu hình trong VNPay Dashboard</td>");
        } else {
            out.println("<td class='success'>✅ Public URL</td>");
        }
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>IPN URL</td>");
        out.println("<td><strong>" + ipnUrl + "</strong></td>");
        if (ipnUrl.contains("ngrok") || ipnUrl.contains("localhost")) {
            out.println("<td class='warning'>⚠️ Localhost/Ngrok - Cần cấu hình trong VNPay Dashboard</td>");
        } else {
            out.println("<td class='success'>✅ Public URL</td>");
        }
        out.println("</tr>");
        out.println("</table>");
        
        // Test Payment URL
        out.println("<h2>5. Test Payment URL</h2>");
        out.println("<p>Tạo payment URL test với orderId = 1, amount = 100000 VND:</p>");
        try {
            String testPaymentUrl = VNPayUtil.createPaymentUrl(1, 100000L, "Test payment", request);
            if (testPaymentUrl != null && !testPaymentUrl.isEmpty()) {
                out.println("<p><strong>Payment URL:</strong></p>");
                out.println("<p style='word-break: break-all; background: #f5f5f5; padding: 10px; border-radius: 5px;'>");
                out.println("<a href='" + testPaymentUrl + "' target='_blank'>" + testPaymentUrl + "</a>");
                out.println("</p>");
                out.println("<p><a href='" + testPaymentUrl + "' target='_blank' style='background: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Mở URL trong tab mới</a></p>");
            } else {
                out.println("<p class='error'>❌ Không thể tạo payment URL. Kiểm tra lại cấu hình.</p>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>❌ Lỗi khi tạo payment URL: " + e.getMessage() + "</p>");
        }
        
        // Lưu ý
        out.println("<h2>6. Lưu Ý</h2>");
        out.println("<ul>");
        out.println("<li>✅ Đảm bảo TMN Code và Hash Secret đã được cấu hình đúng</li>");
        out.println("<li>✅ Nếu dùng localhost/ngrok, cần cấu hình Return URL trong VNPay Dashboard</li>");
        out.println("<li>✅ Test với thẻ test: NCB - 9704198526191432198 - NGUYEN VAN A - 07/15 - OTP: 123456</li>");
        out.println("</ul>");
        
        out.println("</body>");
        out.println("</html>");
    }
}
