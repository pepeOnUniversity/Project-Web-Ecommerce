package com.ecommerce.model;

import java.math.BigDecimal;

/**
 * OrderItem Model Class
 */
public class OrderItem {
    private int orderDetailId;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private Product product; // For joined queries
    
    // Constructors
    public OrderItem() {
    }
    
    public OrderItem(int orderId, int productId, int quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public int getOrderDetailId() {
        return orderDetailId;
    }
    
    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }
    
    // Alias method for backward compatibility
    public int getOrderItemId() {
        return orderDetailId;
    }
    
    public void setOrderItemId(int orderItemId) {
        this.orderDetailId = orderItemId;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        // Auto calculate subtotal when unitPrice changes
        if (unitPrice != null && quantity > 0) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    // Alias method for backward compatibility
    public BigDecimal getPrice() {
        return unitPrice;
    }
    
    public void setPrice(BigDecimal price) {
        setUnitPrice(price);
    }
    
    public BigDecimal getSubtotal() {
        if (subtotal != null) {
            return subtotal;
        }
        // Calculate if not set
        if (unitPrice != null && quantity > 0) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}


