package com.ecommerce.controller;

import com.ecommerce.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Test Password Hash Servlet
 * Servlet này để test và generate password hash
 * URL: /test-password
 */
@WebServlet(name = "TestPasswordServlet", urlPatterns = {"/test-password"})
public class TestPasswordServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Test Password Hash</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Password Hash Generator & Tester</h1>");
        
        String password = request.getParameter("password");
        String hashToVerify = request.getParameter("hash");
        
        if (password != null && !password.isEmpty()) {
            // Generate hash
            String hash = PasswordUtil.hashPassword(password);
            
            out.println("<h2>Generated Hash for: " + password + "</h2>");
            out.println("<p><strong>Hash:</strong><br>");
            out.println("<code style='word-break: break-all;'>" + hash + "</code></p>");
            
            out.println("<h3>SQL Statement:</h3>");
            out.println("<pre>UPDATE users SET password_hash = '" + hash + "' WHERE username = 'admin';</pre>");
            
            // Test verify
            boolean verified = PasswordUtil.verifyPassword(password, hash);
            out.println("<p><strong>Verify result:</strong> " + (verified ? "✓ MATCH" : "✗ NO MATCH") + "</p>");
        }
        
        if (hashToVerify != null && password != null && !password.isEmpty()) {
            // Verify existing hash
            boolean verified = PasswordUtil.verifyPassword(password, hashToVerify);
            out.println("<h2>Verification Result</h2>");
            out.println("<p>Password: <strong>" + password + "</strong></p>");
            out.println("<p>Hash: <code style='word-break: break-all;'>" + hashToVerify + "</code></p>");
            out.println("<p>Result: <strong>" + (verified ? "✓ MATCH - Password is correct!" : "✗ NO MATCH - Password is incorrect!") + "</strong></p>");
        }
        
        // Form để test
        out.println("<hr>");
        out.println("<h2>Test Form</h2>");
        out.println("<form method='get'>");
        out.println("<p><label>Password to hash: <input type='text' name='password' value='admin123' required></label></p>");
        out.println("<p><button type='submit'>Generate Hash</button></p>");
        out.println("</form>");
        
        out.println("<hr>");
        out.println("<h2>Verify Existing Hash</h2>");
        out.println("<form method='get'>");
        out.println("<p><label>Password: <input type='text' name='password' value='admin123' required></label></p>");
        out.println("<p><label>Hash to verify: <input type='text' name='hash' value='$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyN8e1uvfZCC' style='width: 500px;' required></label></p>");
        out.println("<p><button type='submit'>Verify Hash</button></p>");
        out.println("</form>");
        
        out.println("</body>");
        out.println("</html>");
    }
}

