package com.ecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Servlet để kiểm tra xem các JAR files có được load đúng không
 */
@WebServlet(name = "CheckJarsServlet", urlPatterns = {"/check-jars"})
public class CheckJarsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Check JAR Files</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println(".success { background-color: #d4edda; padding: 10px; margin: 10px 0; border-left: 4px solid #28a745; }");
        out.println(".error { background-color: #f8d7da; padding: 10px; margin: 10px 0; border-left: 4px solid #dc3545; }");
        out.println(".info { background-color: #e7f3ff; padding: 10px; margin: 10px 0; border-left: 4px solid #2196F3; }");
        out.println("pre { background-color: #f5f5f5; padding: 10px; overflow-x: auto; }");
        out.println("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #4CAF50; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f2f2; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Kiểm tra JAR Files</h1>");
        
        // Danh sách các JARs cần kiểm tra
        String[] requiredJars = {
            "jakarta.mail-api",
            "jakarta.activation-api",
            "angus-mail",
            "angus-activation"
        };
        
        // Danh sách các classes cần kiểm tra
        String[] requiredClasses = {
            "jakarta.mail.Session",
            "jakarta.mail.Transport",
            "jakarta.mail.internet.MimeMessage",
            "jakarta.activation.DataHandler"
        };
        
        out.println("<h2>1. Kiểm tra Classes có thể load được không</h2>");
        out.println("<table>");
        out.println("<tr><th>Class Name</th><th>Status</th><th>Details</th></tr>");
        
        boolean allClassesLoaded = true;
        for (String className : requiredClasses) {
            try {
                Class<?> clazz = Class.forName(className);
                String location = getClassLocation(clazz);
                out.println("<tr>");
                out.println("<td>" + className + "</td>");
                out.println("<td style='color: green;'>✓ LOADED</td>");
                out.println("<td>" + location + "</td>");
                out.println("</tr>");
            } catch (ClassNotFoundException e) {
                allClassesLoaded = false;
                out.println("<tr>");
                out.println("<td>" + className + "</td>");
                out.println("<td style='color: red;'>✗ NOT FOUND</td>");
                out.println("<td>" + e.getMessage() + "</td>");
                out.println("</tr>");
            }
        }
        out.println("</table>");
        
        if (allClassesLoaded) {
            out.println("<div class='success'>");
            out.println("<h3>✅ Tất cả các classes đều có thể load được!</h3>");
            out.println("</div>");
        } else {
            out.println("<div class='error'>");
            out.println("<h3>❌ Một số classes không thể load được!</h3>");
            out.println("<p>Điều này có nghĩa là các JAR files chưa được load đúng cách.</p>");
            out.println("</div>");
        }
        
        out.println("<h2>2. Kiểm tra JAR files trong WEB-INF/lib (thực tế)</h2>");
        // Kiểm tra thư mục WEB-INF/lib thực tế
        try {
            String webInfLibPath = getServletContext().getRealPath("/WEB-INF/lib");
            java.io.File libDir = new java.io.File(webInfLibPath);
            out.println("<div class='info'>");
            out.println("<p><strong>Thư mục WEB-INF/lib:</strong> " + webInfLibPath + "</p>");
            if (libDir.exists() && libDir.isDirectory()) {
                java.io.File[] jarFiles = libDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
                if (jarFiles != null && jarFiles.length > 0) {
                    out.println("<p><strong>Số lượng JAR files:</strong> " + jarFiles.length + "</p>");
                    out.println("<table>");
                    out.println("<tr><th>JAR File Name</th><th>Size</th><th>Last Modified</th></tr>");
                    for (java.io.File jarFile : jarFiles) {
                        out.println("<tr>");
                        out.println("<td>" + jarFile.getName() + "</td>");
                        out.println("<td>" + (jarFile.length() / 1024) + " KB</td>");
                        out.println("<td>" + new java.util.Date(jarFile.lastModified()) + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                } else {
                    out.println("<p style='color: red;'>Không tìm thấy JAR files nào trong WEB-INF/lib!</p>");
                }
            } else {
                out.println("<p style='color: red;'>Thư mục WEB-INF/lib không tồn tại!</p>");
            }
            out.println("</div>");
        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("<p>Lỗi khi kiểm tra WEB-INF/lib: " + e.getMessage() + "</p>");
            out.println("</div>");
        }
        
        out.println("<h2>2b. Kiểm tra JAR files trong Classpath (runtime)</h2>");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<String> foundJars = new ArrayList<>();
        
        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String path = url.toString();
                if (path.contains(".jar")) {
                    foundJars.add(path);
                }
            }
        } catch (IOException e) {
            out.println("<div class='error'>");
            out.println("<p>Lỗi khi tìm JAR files: " + e.getMessage() + "</p>");
            out.println("</div>");
        }
        
        out.println("<div class='info'>");
        out.println("<h3>JAR files được tìm thấy trong classpath (runtime):</h3>");
        out.println("<pre>");
        if (foundJars.isEmpty()) {
            out.println("Không tìm thấy JAR files nào trong classpath.");
        } else {
            for (String jar : foundJars) {
                out.println(jar);
            }
        }
        out.println("</pre>");
        out.println("</div>");
        
        // Kiểm tra các JARs cụ thể bằng cách tìm classes trong chúng
        out.println("<h2>3. Kiểm tra các JARs cần thiết</h2>");
        out.println("<table>");
        out.println("<tr><th>JAR Name</th><th>Status</th><th>Method</th></tr>");
        
        // Mapping JAR names to test classes (thử nhiều class names)
        java.util.Map<String, String[]> jarToClasses = new java.util.HashMap<>();
        jarToClasses.put("jakarta.mail-api", new String[]{"jakarta.mail.Session", "jakarta.mail.Message"});
        jarToClasses.put("jakarta.activation-api", new String[]{"jakarta.activation.DataHandler", "jakarta.activation.DataSource"});
        jarToClasses.put("angus-mail", new String[]{
            "org.eclipse.angus.mail.util.MimeUtility",
            "org.eclipse.angus.mail.smtp.SMTPTransport",
            "com.sun.mail.util.MimeUtility" // Fallback
        });
        jarToClasses.put("angus-activation", new String[]{
            "org.eclipse.angus.activation.util.MimeType",
            "org.eclipse.angus.activation.DataHandler",
            "com.sun.activation.registries.MimeType" // Fallback
        });
        
        for (String jarName : requiredJars) {
            boolean foundInClasspath = false;
            String classpathLocation = "";
            
            // Method 1: Tìm trong foundJars list
            for (String jar : foundJars) {
                String jarLower = jar.toLowerCase();
                String jarNameLower = jarName.toLowerCase();
                // Kiểm tra nhiều cách: với dấu gạch ngang, không dấu, với version
                if (jarLower.contains(jarNameLower) || 
                    jarLower.contains(jarNameLower.replace("-", "")) ||
                    jarLower.contains(jarNameLower.replace("-", "."))) {
                    foundInClasspath = true;
                    classpathLocation = jar;
                    break;
                }
            }
            
            // Method 2: Kiểm tra bằng cách load class từ JAR đó
            boolean classCanLoad = false;
            String[] testClasses = jarToClasses.get(jarName);
            if (testClasses != null) {
                for (String testClass : testClasses) {
                    try {
                        Class<?> clazz = Class.forName(testClass);
                        classCanLoad = true;
                        if (classpathLocation.isEmpty()) {
                            classpathLocation = getClassLocation(clazz);
                        }
                        break; // Tìm thấy một class là đủ
                    } catch (ClassNotFoundException e) {
                        // Thử class tiếp theo
                        continue;
                    }
                }
            }
            
            out.println("<tr>");
            out.println("<td>" + jarName + "</td>");
            if (classCanLoad) {
                // Nếu class có thể load, JAR đã được load
                out.println("<td style='color: green;'>✓ FOUND (Class can load)</td>");
                out.println("<td>Class loading test</td>");
            } else if (foundInClasspath) {
                out.println("<td style='color: green;'>✓ FOUND IN CLASSPATH</td>");
                out.println("<td>Manifest search</td>");
            } else {
                out.println("<td style='color: orange;'>? NOT FOUND IN CLASSPATH</td>");
                out.println("<td>Both methods failed</td>");
            }
            out.println("</tr>");
            
            // Hiển thị location nếu tìm thấy
            if (!classpathLocation.isEmpty()) {
                out.println("<tr><td colspan='3' style='font-size: 0.9em; color: #666;'>Location: " + classpathLocation + "</td></tr>");
            }
        }
        out.println("</table>");
        
        out.println("<h2>4. Test tạo Session</h2>");
        // Kiểm tra class có tồn tại trước khi sử dụng
        try {
            Class<?> sessionClass = Class.forName("jakarta.mail.Session");
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            
            Object session = sessionClass.getMethod("getInstance", java.util.Properties.class).invoke(null, props);
            out.println("<div class='success'>");
            out.println("<h3>✅ Session được tạo thành công!</h3>");
            out.println("<p>Session class: " + session.getClass().getName() + "</p>");
            try {
                Object provider = sessionClass.getMethod("getProvider", String.class).invoke(session, "transport");
                out.println("<p>Provider: " + provider.getClass().getName() + "</p>");
            } catch (Exception pe) {
                out.println("<p>Provider: Không thể lấy provider</p>");
            }
            out.println("</div>");
        } catch (ClassNotFoundException e) {
            out.println("<div class='error'>");
            out.println("<h3>❌ Class jakarta.mail.Session không tìm thấy!</h3>");
            out.println("<p>Điều này có nghĩa là JAR files chưa được load đúng cách.</p>");
            out.println("<p><strong>Giải pháp:</strong></p>");
            out.println("<ol>");
            out.println("<li>Đảm bảo tất cả JARs có trong <code>web/WEB-INF/lib/</code></li>");
            out.println("<li>Clean & Build project trong NetBeans (Shift+F11)</li>");
            out.println("<li>Restart Tomcat server</li>");
            out.println("<li>Kiểm tra lại trang này</li>");
            out.println("</ol>");
            out.println("</div>");
        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("<h3>❌ Lỗi khi tạo Session!</h3>");
            out.println("<pre>");
            out.println("Exception: " + e.getClass().getName());
            out.println("Message: " + e.getMessage());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            out.println(sw.toString());
            out.println("</pre>");
            out.println("</div>");
        }
        
        out.println("<h2>5. Hướng dẫn</h2>");
        out.println("<div class='info'>");
        out.println("<ul>");
        out.println("<li>Nếu tất cả classes đều LOADED, có nghĩa là JARs đã được load đúng.</li>");
        out.println("<li>Nếu có classes NOT FOUND, cần kiểm tra lại việc thêm JARs vào project.</li>");
        out.println("<li>Đảm bảo các JARs nằm trong <code>web/WEB-INF/lib/</code> và đã Clean & Build project.</li>");
        out.println("<li>Restart Tomcat sau khi thêm JARs.</li>");
        out.println("</ul>");
        out.println("</div>");
        
        out.println("</body>");
        out.println("</html>");
    }
    
    private String getClassLocation(Class<?> clazz) {
        try {
            String className = clazz.getName();
            String resourceName = className.replace('.', '/') + ".class";
            URL resource = clazz.getClassLoader().getResource(resourceName);
            if (resource != null) {
                String path = resource.toString();
                if (path.contains(".jar!")) {
                    int jarIndex = path.indexOf(".jar!");
                    return path.substring(0, jarIndex + 4);
                }
                return path;
            }
            return "Unknown location";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

