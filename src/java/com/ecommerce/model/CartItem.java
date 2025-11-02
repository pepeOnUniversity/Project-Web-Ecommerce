package com.ecommerce.model;

import java.math.BigDecimal;

/**
 * CartItem Model Class
 */
public class CartItem {
    private int cartId;
    private int userId;
    private int productId;
    private int quantity;
    private Product product; // For joined queries
    
    // Constructors
    public CartItem() {
    }
    
    public CartItem(int userId, int productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public int getCartId() {
        return cartId;
    }
    
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
    
    // Alias method for backward compatibility
    public int getCartItemId() {
        return cartId;
    }
    
    public void setCartItemId(int cartItemId) {
        this.cartId = cartItemId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
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
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    /**
     * Tính tổng giá của item này
     */
    public BigDecimal getSubtotal() {
        if (product != null && quantity > 0) {
            return product.getFinalPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}


