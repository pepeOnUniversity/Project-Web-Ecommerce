package com.ecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * View Log Servlet
 * Hiển thị log của project WebEcommerce trực tiếp trên web
 */
@WebServlet(name = "ViewLogServlet", urlPatterns = {"/view-log"})
public class ViewLogServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ViewLogServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>View Log - WebEcommerce</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta http-equiv='refresh' content='5'>"); // Auto refresh mỗi 5 giây
        out.println("<style>");
        out.println("body { font-family: 'Courier New', monospace; margin: 20px; background-color: #1e1e1e; color: #d4d4d4; }");
        out.println("h1 { color: #4ec9b0; }");
        out.println("h2 { color: #569cd6; }");
        out.println(".info { background-color: #252526; padding: 15px; margin: 10px 0; border-left: 4px solid #007acc; }");
        out.println(".log-container { background-color: #1e1e1e; padding: 15px; margin: 10px 0; border: 1px solid #3e3e42; }");
        out.println(".log-line { margin: 2px 0; padding: 2px 5px; }");
        out.println(".log-severe { color: #f48771; }");
        out.println(".log-warning { color: #dcdcaa; }");
        out.println(".log-info { color: #4ec9b0; }");
        out.println(".log-fine { color: #9cdcfe; }");
        out.println(".filter-box { background-color: #252526; padding: 15px; margin: 10px 0; border: 1px solid #3e3e42; }");
        out.println("input[type='text'] { background-color: #3e3e42; color: #d4d4d4; border: 1px solid #007acc; padding: 5px; width: 300px; }");
        out.println("button { background-color: #007acc; color: white; border: none; padding: 5px 15px; cursor: pointer; }");
        out.println("button:hover { background-color: #005a9e; }");
        out.println("pre { margin: 0; white-space: pre-wrap; word-wrap: break-word; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>📋 View Log - WebEcommerce</h1>");
        
        // Filter parameter
        String filter = request.getParameter("filter");
        if (filter == null) filter = "";
        
        out.println("<div class='filter-box'>");
        out.println("<form method='get'>");
        out.println("<label>Filter log (tìm kiếm): </label>");
        out.println("<input type='text' name='filter' value='" + filter + "' placeholder='Nhập từ khóa (ví dụ: WebEcommerce, com.ecommerce, EmailService)'>");
        out.println("<button type='submit'>Tìm kiếm</button>");
        out.println("<a href='?filter=' style='color: #4ec9b0; margin-left: 10px;'>Xóa filter</a>");
        out.println("</form>");
        out.println("</div>");
        
        // Hiển thị thông tin về log
        out.println("<div class='info'>");
        out.println("<h2>ℹ️ Hướng dẫn xem log</h2>");
        out.println("<ul>");
        out.println("<li><strong>Filter:</strong> Nhập từ khóa để tìm log (ví dụ: 'WebEcommerce', 'com.ecommerce', 'EmailService')</li>");
        out.println("<li><strong>Auto refresh:</strong> Trang sẽ tự động refresh mỗi 5 giây</li>");
        out.println("<li><strong>Log colors:</strong>");
        out.println("<ul>");
        out.println("<li><span class='log-severe'>SEVERE</span> - Lỗi nghiêm trọng</li>");
        out.println("<li><span class='log-warning'>WARNING</span> - Cảnh báo</li>");
        out.println("<li><span class='log-info'>INFO</span> - Thông tin</li>");
        out.println("<li><span class='log-fine'>FINE/FINER/FINEST</span> - Debug</li>");
        out.println("</ul>");
        out.println("</li>");
        out.println("</ul>");
        out.println("</div>");
        
        // Tìm và hiển thị log files
        out.println("<div class='log-container'>");
        out.println("<h2>📄 Log Files</h2>");
        
        List<String> logFiles = findLogFiles();
        
        if (logFiles.isEmpty()) {
            out.println("<p style='color: #f48771;'>⚠️ Không tìm thấy log files. Log có thể đang được ghi vào console hoặc log file khác.</p>");
            out.println("<p>Hãy kiểm tra:</p>");
            out.println("<ul>");
            out.println("<li>Console/Output trong NetBeans</li>");
            out.println("<li>Tomcat log files trong thư mục <code>logs/</code> của Tomcat</li>");
            out.println("<li>File <code>catalina.out</code> hoặc <code>localhost.log</code></li>");
            out.println("</ul>");
        } else {
            out.println("<p>Tìm thấy " + logFiles.size() + " log file(s):</p>");
            out.println("<ul>");
            for (String logFile : logFiles) {
                out.println("<li><code>" + logFile + "</code></li>");
            }
            out.println("</ul>");
            
            // Đọc và hiển thị log từ file đầu tiên
            if (!logFiles.isEmpty()) {
                String firstLogFile = logFiles.get(0);
                out.println("<h3>📝 Nội dung log từ: <code>" + firstLogFile + "</code></h3>");
                out.println("<p style='color: #9cdcfe;'>Hiển thị 100 dòng cuối cùng (filter: '" + (filter.isEmpty() ? "không có" : filter) + "')</p>");
                
                try {
                    List<String> logLines = readLogFile(firstLogFile, filter, 100);
                    if (logLines.isEmpty()) {
                        out.println("<p style='color: #f48771;'>Không tìm thấy log nào phù hợp với filter.</p>");
                    } else {
                        out.println("<pre>");
                        for (String line : logLines) {
                            String cssClass = getLogLevelClass(line);
                            out.println("<span class='" + cssClass + "'>" + escapeHtml(line) + "</span>");
                        }
                        out.println("</pre>");
                    }
                } catch (Exception e) {
                    out.println("<p style='color: #f48771;'>Lỗi khi đọc log file: " + e.getMessage() + "</p>");
                    LOGGER.log(Level.SEVERE, "Lỗi khi đọc log file", e);
                }
            }
        }
        
        out.println("</div>");
        
        // Hiển thị thông tin về cách tìm log trong NetBeans
        out.println("<div class='info'>");
        out.println("<h2>🔍 Cách tìm log trong NetBeans</h2>");
        out.println("<ol>");
        out.println("<li>Mở tab <strong>Output</strong> ở dưới cùng của NetBeans</li>");
        out.println("<li>Nếu không thấy tab Output, vào <strong>Window → Output</strong> hoặc nhấn <strong>Ctrl+4</strong></li>");
        out.println("<li>Trong tab Output, chọn <strong>Apache Tomcat</strong> hoặc <strong>WebEcommerce</strong> từ dropdown</li>");
        out.println("<li>Sử dụng <strong>Ctrl+F</strong> để tìm kiếm trong log</li>");
        out.println("<li>Tìm các từ khóa: <code>WebEcommerce</code>, <code>com.ecommerce</code>, <code>EmailService</code></li>");
        out.println("</ol>");
        out.println("</div>");
        
        // Hiển thị thông tin về cách tìm log file trong hệ thống
        out.println("<div class='info'>");
        out.println("<h2>📁 Cách tìm log file trong hệ thống</h2>");
        out.println("<p>Log files thường nằm ở:</p>");
        out.println("<ul>");
        out.println("<li><strong>NetBeans:</strong> <code>C:\\Users\\[YourUsername]\\AppData\\Local\\NetBeans\\[Version]\\tomcat\\[Port]\\logs\\</code></li>");
        out.println("<li><strong>Tomcat standalone:</strong> <code>[TomcatInstallDir]\\logs\\</code></li>");
        out.println("<li><strong>File log thường:</strong> <code>catalina.out</code>, <code>localhost.log</code>, <code>localhost.[date].log</code></li>");
        out.println("</ul>");
        out.println("<p><strong>Lưu ý:</strong> Tìm các dòng có chứa <code>[/WebEcommerce]</code> hoặc <code>com.ecommerce</code> để xác định log của project này.</p>");
        out.println("</div>");
        
        out.println("</body>");
        out.println("</html>");
    }
    
    /**
     * Tìm các log files có thể có
     */
    private List<String> findLogFiles() {
        List<String> logFiles = new ArrayList<>();
        
        // Thử tìm log files ở các vị trí thường gặp
        String[] possiblePaths = {
            "logs/webecommerce-0.log",
            "logs/webecommerce-1.log",
            "../logs/catalina.out",
            "../logs/localhost.log",
            System.getProperty("catalina.base") + "/logs/catalina.out",
            System.getProperty("catalina.base") + "/logs/localhost.log",
            System.getProperty("catalina.home") + "/logs/catalina.out",
            System.getProperty("catalina.home") + "/logs/localhost.log"
        };
        
        for (String pathStr : possiblePaths) {
            if (pathStr == null || pathStr.contains("null")) continue;
            
            try {
                Path path = Paths.get(pathStr);
                if (Files.exists(path) && Files.isRegularFile(path)) {
                    logFiles.add(path.toAbsolutePath().toString());
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        
        return logFiles;
    }
    
    /**
     * Đọc log file và filter
     */
    private List<String> readLogFile(String filePath, String filter, int maxLines) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        
        List<String> allLines = Files.readAllLines(path);
        List<String> filteredLines = allLines;
        
        // Filter nếu có
        if (filter != null && !filter.trim().isEmpty()) {
            String lowerFilter = filter.toLowerCase();
            filteredLines = allLines.stream()
                .filter(line -> line.toLowerCase().contains(lowerFilter))
                .collect(Collectors.toList());
        }
        
        // Lấy N dòng cuối cùng
        int startIndex = Math.max(0, filteredLines.size() - maxLines);
        return filteredLines.subList(startIndex, filteredLines.size());
    }
    
    /**
     * Xác định CSS class dựa trên log level
     */
    private String getLogLevelClass(String line) {
        String upperLine = line.toUpperCase();
        if (upperLine.contains("SEVERE")) {
            return "log-severe";
        } else if (upperLine.contains("WARNING")) {
            return "log-warning";
        } else if (upperLine.contains("INFO")) {
            return "log-info";
        } else if (upperLine.contains("FINE") || upperLine.contains("FINER") || upperLine.contains("FINEST")) {
            return "log-fine";
        }
        return "log-line";
    }
    
    /**
     * Escape HTML để hiển thị an toàn
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}


