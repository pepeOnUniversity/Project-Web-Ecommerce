package com.ecommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * VNPay Configuration Class
 * Lưu trữ các thông tin cấu hình VNPay
 * 
 * Để sử dụng:
 * 1. Đăng ký tài khoản sandbox tại: https://sandbox.vnpayment.vn/devreg/
 * 2. Nhận vnp_TmnCode và vnp_HashSecret từ email
 * 3. Cập nhật các giá trị bên dưới hoặc tạo file vnpay.properties
 */
public class VNPayConfig {
    
    private static final Logger LOGGER = Logger.getLogger(VNPayConfig.class.getName());
    
    // Cache cho config properties từ file
    private static Properties configProperties = null;
    private static boolean configLoaded = false;
    
    /**
     * Load cấu hình từ file config.properties
     */
    private static synchronized void loadConfigProperties() {
        if (configLoaded) {
            return; // Đã load rồi, không load lại
        }
        
        configProperties = new Properties();
        try {
            // Thử load từ classpath (src/java/resources/config.properties hoặc WEB-INF/classes/config.properties)
            InputStream inputStream = VNPayConfig.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream != null) {
                configProperties.load(inputStream);
                inputStream.close();
                LOGGER.log(Level.FINE, "Đã load cấu hình từ file config.properties");
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Không thể load file config.properties: " + e.getMessage());
        }
        
        configLoaded = true;
    }
    
    /**
     * Lấy ngrok URL từ System Property, Environment Variable, hoặc config.properties
     * Thứ tự ưu tiên:
     * 1. System Property: vnpay.ngrok.url
     * 2. Environment Variable: VNPAY_NGROK_URL hoặc vnpay.ngrok.url
     * 3. config.properties: ngrok.url
     */
    private static String getNgrokUrl() {
        // 1. Thử lấy từ System Properties trước (ưu tiên cao nhất)
        String ngrokUrl = System.getProperty("vnpay.ngrok.url");
        if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
            return ngrokUrl;
        }
        
        // 2. Fallback: lấy từ Environment Variables
        ngrokUrl = System.getenv("VNPAY_NGROK_URL");
        if (ngrokUrl == null || ngrokUrl.isEmpty()) {
            ngrokUrl = System.getenv("vnpay.ngrok.url");
        }
        if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
            return ngrokUrl;
        }
        
        // 3. Fallback: lấy từ file config.properties
        loadConfigProperties();
        if (configProperties != null) {
            ngrokUrl = configProperties.getProperty("ngrok.url");
            if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
                LOGGER.log(Level.FINE, "Đã lấy ngrok URL từ file config.properties");
                return ngrokUrl;
            }
        }
        
        return null;
    }
    
    // VNPay Sandbox URLs
    public static final String VNPAY_PAYMENT_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNPAY_QUERY_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    public static final String VNPAY_REFUND_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    
    // Thông tin merchant (đọc từ config.properties hoặc system properties)
    // Đăng ký tại: https://sandbox.vnpayment.vn/devreg/
    // Thứ tự ưu tiên: System Property > Environment Variable > config.properties > Default
    public static String getTmnCode() {
        // 1. System Property
        String tmnCode = System.getProperty("vnpay.tmn.code");
        if (tmnCode != null && !tmnCode.isEmpty()) {
            return tmnCode;
        }
        
        // 2. Environment Variable
        tmnCode = System.getenv("VNPAY_TMN_CODE");
        if (tmnCode == null || tmnCode.isEmpty()) {
            tmnCode = System.getenv("vnpay.tmn.code");
        }
        if (tmnCode != null && !tmnCode.isEmpty()) {
            return tmnCode;
        }
        
        // 3. config.properties
        loadConfigProperties();
        if (configProperties != null) {
            tmnCode = configProperties.getProperty("vnpay.tmn.code");
            if (tmnCode != null && !tmnCode.isEmpty()) {
                return tmnCode;
            }
        }
        
        // 4. Default (fallback)
        return "YOUR_TMN_CODE_HERE";
    }
    
    public static String getHashSecret() {
        // 1. System Property
        String hashSecret = System.getProperty("vnpay.hash.secret");
        if (hashSecret != null && !hashSecret.isEmpty()) {
            return hashSecret;
        }
        
        // 2. Environment Variable
        hashSecret = System.getenv("VNPAY_HASH_SECRET");
        if (hashSecret == null || hashSecret.isEmpty()) {
            hashSecret = System.getenv("vnpay.hash.secret");
        }
        if (hashSecret != null && !hashSecret.isEmpty()) {
            return hashSecret;
        }
        
        // 3. config.properties
        loadConfigProperties();
        if (configProperties != null) {
            hashSecret = configProperties.getProperty("vnpay.hash.secret");
            if (hashSecret != null && !hashSecret.isEmpty()) {
                return hashSecret;
            }
        }
        
        // 4. Default (fallback)
        return "YOUR_HASH_SECRET_HERE";
    }
    
    // Deprecated: Sử dụng getTmnCode() và getHashSecret() thay vì
    // Lưu ý: Không thể dùng final static field vì cần đọc từ config động
    // Sử dụng getTmnCode() và getHashSecret() methods thay vì
    
    // Return URL và IPN URL (callback URLs)
    // Lưu ý: VNPay cần callback về một URL công khai
    // Để test localhost, có thể dùng ngrok: https://ngrok.com/
    // Ví dụ: ngrok http 8080 -> lấy URL và cập nhật vào VNPay dashboard
    public static final String VNPAY_RETURN_URL = "http://localhost:8080/WebEcommerce/vnpay-return";
    public static final String VNPAY_IPN_URL = "http://localhost:8080/WebEcommerce/vnpay-ipn";
    
    // Version và command
    public static final String VNPAY_VERSION = "2.1.0";
    public static final String VNPAY_COMMAND = "pay";
    public static final String VNPAY_CURRENCY_CODE = "VND";
    public static final String VNPAY_LOCALE = "vn";
    
    // Order type
    public static final String VNPAY_ORDER_TYPE = "other";
    
    /**
     * Lấy return URL động dựa trên request
     */
    public static String getReturnUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        
        // Thử lấy ngrok URL (từ system property, env var, hoặc config file)
        String ngrokUrl = getNgrokUrl();
        if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
            // Loại bỏ trailing slash nếu có
            if (ngrokUrl.endsWith("/")) {
                ngrokUrl = ngrokUrl.substring(0, ngrokUrl.length() - 1);
            }
            return ngrokUrl + contextPath + "/vnpay-return";
        }
        
        // Fallback: dùng URL từ request
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        
        return scheme + "://" + serverName + 
               (serverPort != 80 && serverPort != 443 ? ":" + serverPort : "") + 
               contextPath + "/vnpay-return";
    }
    
    /**
     * Lấy IPN URL động
     */
    public static String getIpnUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        
        // Thử lấy ngrok URL (từ system property, env var, hoặc config file)
        String ngrokUrl = getNgrokUrl();
        if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
            // Loại bỏ trailing slash nếu có
            if (ngrokUrl.endsWith("/")) {
                ngrokUrl = ngrokUrl.substring(0, ngrokUrl.length() - 1);
            }
            return ngrokUrl + contextPath + "/vnpay-ipn";
        }
        
        // Fallback: dùng URL từ request
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        
        return scheme + "://" + serverName + 
               (serverPort != 80 && serverPort != 443 ? ":" + serverPort : "") + 
               contextPath + "/vnpay-ipn";
    }
}

