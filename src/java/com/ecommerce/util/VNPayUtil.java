package com.ecommerce.util;

import com.ecommerce.config.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * VNPay Utility Class
 * Cung cấp các method để tạo payment URL và verify callback từ VNPay
 */
public class VNPayUtil {
    
    private static final Logger LOGGER = Logger.getLogger(VNPayUtil.class.getName());
    
    /**
     * Tạo payment URL từ VNPay
     * 
     * @param orderId Mã đơn hàng
     * @param amount Số tiền (VND)
     * @param orderInfo Thông tin đơn hàng
     * @param request HttpServletRequest để lấy return URL và IPN URL
     * @return Payment URL từ VNPay hoặc null nếu có lỗi
     */
    public static String createPaymentUrl(int orderId, long amount, String orderInfo, HttpServletRequest request) {
        try {
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.VNPAY_VERSION);
            vnp_Params.put("vnp_Command", VNPayConfig.VNPAY_COMMAND);
            vnp_Params.put("vnp_TmnCode", VNPayConfig.getTmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu số tiền nhân 100
            vnp_Params.put("vnp_CurrCode", VNPayConfig.VNPAY_CURRENCY_CODE);
            vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", VNPayConfig.VNPAY_ORDER_TYPE);
            vnp_Params.put("vnp_Locale", VNPayConfig.VNPAY_LOCALE);
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.getReturnUrl(request));
            vnp_Params.put("vnp_IpAddr", getIpAddress(request));
            
            // Thêm thời gian tạo và hết hạn
            vnp_Params.put("vnp_CreateDate", getCurrentDateTime());
            vnp_Params.put("vnp_ExpireDate", getExpireDateTime());
            
            // Sắp xếp params theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            
            // Tạo hash data (QUAN TRỌNG: dùng giá trị RAW, không encode)
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(fieldValue); // Dùng giá trị RAW, không encode
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
            
            // Tính hash
            String vnp_SecureHash = hmacSHA512(VNPayConfig.getHashSecret(), hashData.toString());
            vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
            
            // Tạo query string (cần encode cho URL)
            StringBuilder queryUrl = new StringBuilder();
            itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    queryUrl.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    queryUrl.append('=');
                    queryUrl.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    if (itr.hasNext()) {
                        queryUrl.append('&');
                    }
                }
            }
            
            // Thêm vnp_SecureHash vào query string
            queryUrl.append('&');
            queryUrl.append(URLEncoder.encode("vnp_SecureHash", StandardCharsets.UTF_8.toString()));
            queryUrl.append('=');
            queryUrl.append(URLEncoder.encode(vnp_SecureHash, StandardCharsets.UTF_8.toString()));
            
            // Tạo payment URL
            String paymentUrl = VNPayConfig.VNPAY_PAYMENT_URL + "?" + queryUrl.toString();
            
            LOGGER.log(Level.INFO, "Created VNPay payment URL for order: " + orderId);
            return paymentUrl;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating VNPay payment URL", e);
            return null;
        }
    }
    
    /**
     * Verify payment callback từ VNPay
     * 
     * @param request HttpServletRequest chứa callback parameters
     * @return true nếu signature hợp lệ, false nếu không
     */
    public static boolean verifyPayment(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            
            // Lấy tất cả parameters từ request
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue()[0];
                params.put(paramName, paramValue);
            }
            
            // Lấy vnp_SecureHash từ params
            String vnp_SecureHash = params.get("vnp_SecureHash");
            if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
                LOGGER.log(Level.WARNING, "vnp_SecureHash is missing");
                return false;
            }
            
            // Xóa vnp_SecureHash khỏi params để tính hash
            params.remove("vnp_SecureHash");
            
            // Sắp xếp params
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            
            // Tạo hash data
            // Lưu ý: Khi VNPay gửi callback, các giá trị đã được URL encode
            // Nhưng khi tạo hash data để verify, cần dùng giá trị RAW (không encode)
            // Vì vậy cần decode trước khi tạo hash data
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Decode giá trị từ URL encoded về RAW
                    try {
                        fieldValue = java.net.URLDecoder.decode(fieldValue, StandardCharsets.UTF_8.toString());
                    } catch (Exception e) {
                        // Nếu decode lỗi, dùng giá trị gốc
                        LOGGER.log(Level.WARNING, "Cannot decode field value: " + fieldName, e);
                    }
                    
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(fieldValue); // Dùng giá trị RAW, không encode
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
            
            // Tính hash và so sánh
            String calculatedHash = hmacSHA512(VNPayConfig.getHashSecret(), hashData.toString());
            return calculatedHash.equals(vnp_SecureHash);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying VNPay payment", e);
            return false;
        }
    }
    
    /**
     * Tính HMAC SHA512
     */
    private static String hmacSHA512(String key, String data) {
        try {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSHA512.init(secretKey);
            byte[] hashBytes = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Convert bytes to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating HMAC SHA512", e);
            return null;
        }
    }
    
    /**
     * Lấy IP address từ request
     */
    private static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
    
    /**
     * Lấy thời gian hiện tại theo format yyyyMMddHHmmss
     */
    private static String getCurrentDateTime() {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date date = new java.util.Date();
        return formatter.format(date);
    }
    
    /**
     * Lấy thời gian hết hạn (15 phút sau) theo format yyyyMMddHHmmss
     */
    private static String getExpireDateTime() {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MINUTE, 15); // Hết hạn sau 15 phút
        return formatter.format(cal.getTime());
    }
}

