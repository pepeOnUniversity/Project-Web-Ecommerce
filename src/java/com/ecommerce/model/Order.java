package com.ecommerce.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Order Model Class
 */
public class Order {
    private int orderId;
    private int userId;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String phone;
    private String status; // PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
    private String paymentMethod; // COD, VNPAY
    private String paymentStatus; // PENDING, PAID, FAILED, CANCELLED, REFUNDED
    private String vnpTransactionId; // Transaction ID từ VNPay
    private String notes;
    private Timestamp orderDate;
    private List<OrderItem> orderItems;
    private User user; // For joined queries
    
    // Constructors
    public Order() {
    }
    
    public Order(int userId, BigDecimal totalAmount, String shippingAddress, String phone) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.phone = phone;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getVnpTransactionId() {
        return vnpTransactionId;
    }
    
    public void setVnpTransactionId(String vnpTransactionId) {
        this.vnpTransactionId = vnpTransactionId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}


