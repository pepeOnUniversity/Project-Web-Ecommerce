package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;
import com.ecommerce.util.VNPayUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * VNPay Callback Servlet
 * Xử lý callback từ VNPay sau khi thanh toán
 * 
 * URL patterns:
 * - /vnpay-return: Return URL (user được redirect về sau khi thanh toán)
 * - /vnpay-ipn: IPN URL (VNPay gửi callback để xác nhận thanh toán)
 */
@WebServlet({"/vnpay-return", "/vnpay-ipn"})
public class VNPayCallbackServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(VNPayCallbackServlet.class.getName());
    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    
    public VNPayCallbackServlet() {
        this.orderDAO = new OrderDAO();
        this.cartDAO = new CartDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // VNPay gửi callback qua GET
        processCallback(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // VNPay có thể gửi callback qua POST
        processCallback(request, response);
    }
    
    /**
     * Xử lý callback từ VNPay
     */
    private void processCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        boolean isReturnUrl = "/vnpay-return".equals(servletPath);
        boolean isIpnUrl = "/vnpay-ipn".equals(servletPath);
        
        try {
            // Lấy các parameters từ VNPay
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
            String vnp_Amount = request.getParameter("vnp_Amount");
            String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
            
            LOGGER.log(Level.INFO, "VNPay callback received - ResponseCode: " + vnp_ResponseCode + 
                    ", TransactionStatus: " + vnp_TransactionStatus + ", TxnRef: " + vnp_TxnRef);
            
            // Verify signature
            boolean isValidSignature = VNPayUtil.verifyPayment(request);
            if (!isValidSignature) {
                LOGGER.log(Level.WARNING, "Invalid signature from VNPay for order: " + vnp_TxnRef);
                
                if (isReturnUrl) {
                    // Redirect về payment result page với lỗi
                    request.setAttribute("success", false);
                    request.setAttribute("error", "Chữ ký không hợp lệ. Thanh toán không được xác nhận.");
                    if (vnp_TxnRef != null) {
                        try {
                            request.setAttribute("orderId", Integer.parseInt(vnp_TxnRef));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    request.getRequestDispatcher("/views/customer/payment-result.jsp").forward(request, response);
                } else if (isIpnUrl) {
                    // IPN: Trả về response cho VNPay
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid signature");
                }
                return;
            }
            
            // Parse orderId từ vnp_TxnRef
            int orderId = 0;
            if (vnp_TxnRef != null && !vnp_TxnRef.trim().isEmpty()) {
                try {
                    orderId = Integer.parseInt(vnp_TxnRef);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Invalid orderId in vnp_TxnRef: " + vnp_TxnRef);
                }
            }
            
            if (orderId <= 0) {
                LOGGER.log(Level.WARNING, "Invalid orderId from VNPay callback");
                
                if (isReturnUrl) {
                    request.setAttribute("success", false);
                    request.setAttribute("error", "Mã đơn hàng không hợp lệ");
                    request.getRequestDispatcher("/views/customer/payment-result.jsp").forward(request, response);
                } else if (isIpnUrl) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid orderId");
                }
                return;
            }
            
            // Lấy order từ database
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                LOGGER.log(Level.WARNING, "Order not found: " + orderId);
                
                if (isReturnUrl) {
                    request.setAttribute("success", false);
                    request.setAttribute("error", "Không tìm thấy đơn hàng");
                    request.setAttribute("orderId", orderId);
                    request.getRequestDispatcher("/views/customer/payment-result.jsp").forward(request, response);
                } else if (isIpnUrl) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Order not found");
                }
                return;
            }
            
            // Kiểm tra số tiền (VNPay trả về số tiền nhân 100)
            long vnpAmount = 0;
            if (vnp_Amount != null && !vnp_Amount.trim().isEmpty()) {
                try {
                    vnpAmount = Long.parseLong(vnp_Amount);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Invalid amount from VNPay: " + vnp_Amount);
                }
            }
            
            long orderAmount = order.getTotalAmount().longValue() * 100;
            if (vnpAmount != orderAmount) {
                LOGGER.log(Level.WARNING, "Amount mismatch - Order: " + orderAmount + ", VNPay: " + vnpAmount);
                
                if (isReturnUrl) {
                    request.setAttribute("success", false);
                    request.setAttribute("error", "Số tiền thanh toán không khớp");
                    request.setAttribute("orderId", orderId);
                    request.getRequestDispatcher("/views/customer/payment-result.jsp").forward(request, response);
                } else if (isIpnUrl) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Amount mismatch");
                }
                return;
            }
            
            // Xử lý kết quả thanh toán
            // ResponseCode = "00" và TransactionStatus = "00" nghĩa là thanh toán thành công
            boolean isSuccess = "00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus);
            
            if (isSuccess) {
                // Thanh toán thành công
                LOGGER.log(Level.INFO, "Payment successful for order: " + orderId + ", TransactionNo: " + vnp_TransactionNo);
                
                // Cập nhật order: status = CONFIRMED, payment_status = PAID
                orderDAO.updateOrderStatus(orderId, "CONFIRMED");
                orderDAO.updateOrderPaymentStatus(orderId, "PAID", vnp_TransactionNo);
                
                // Xóa cart (nếu chưa xóa)
                cartDAO.clearCart(order.getUserId());
                
                if (isReturnUrl) {
                    // Redirect về payment result page với success
                    request.setAttribute("success", true);
                    request.setAttribute("orderId", orderId);
                    request.setAttribute("transactionNo", vnp_TransactionNo);
                    request.getRequestDispatcher("/views/customer/payment-result.jsp").forward(request, response);
                } else if (isIpnUrl) {
                    // IPN: Trả về response thành công cho VNPay
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Success");
                }
            } else {
                // Thanh toán thất bại
                String errorMessage = getErrorMessage(vnp_ResponseCode);
                LOGGER.log(Level.INFO, "Payment failed for order: " + orderId + ", ResponseCode: " + vnp_ResponseCode);
                
                // Cập nhật order: status = CANCELLED, payment_status = FAILED
                orderDAO.updateOrderStatus(orderId, "CANCELLED");
                orderDAO.updateOrderPaymentStatus(orderId, "FAILED", vnp_TransactionNo != null ? vnp_TransactionNo : "");
                
                if (isReturnUrl) {
                    // Redirect về payment result page với error
                    request.setAttribute("success", false);
                    request.setAttribute("error", errorMessage);
                    request.setAttribute("orderId", orderId);
                    request.getRequestDispatcher("/views/customer/payment-result.jsp").forward(request, response);
                } else if (isIpnUrl) {
                    // IPN: Trả về response cho VNPay
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Payment failed");
                }
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing VNPay callback", e);
            
            if (isReturnUrl) {
                request.setAttribute("success", false);
                request.setAttribute("error", "Có lỗi xảy ra khi xử lý thanh toán. Vui lòng liên hệ hỗ trợ.");
                request.getRequestDispatcher("/views/customer/payment-result.jsp").forward(request, response);
            } else if (isIpnUrl) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Internal server error");
            }
        }
    }
    
    /**
     * Lấy thông báo lỗi từ response code
     */
    private String getErrorMessage(String responseCode) {
        if (responseCode == null) {
            return "Thanh toán thất bại";
        }
        
        switch (responseCode) {
            case "00":
                return "Giao dịch thành công";
            case "07":
                return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09":
                return "Thẻ/Tài khoản chưa đăng ký dịch vụ InternetBanking";
            case "10":
                return "Xác thực thông tin thẻ/tài khoản không đúng. Vui lòng kiểm tra lại.";
            case "11":
                return "Đã hết hạn chờ thanh toán. Vui lòng thử lại.";
            case "12":
                return "Thẻ/Tài khoản bị khóa.";
            case "13":
                return "Nhập sai mật khẩu xác thực giao dịch (OTP). Vui lòng thử lại.";
            case "51":
                return "Tài khoản không đủ số dư để thực hiện giao dịch.";
            case "65":
                return "Tài khoản đã vượt quá hạn mức giao dịch trong ngày.";
            case "75":
                return "Ngân hàng thanh toán đang bảo trì.";
            case "79":
                return "Nhập sai mật khẩu thanh toán quá số lần quy định.";
            default:
                return "Thanh toán thất bại. Mã lỗi: " + responseCode;
        }
    }
}
