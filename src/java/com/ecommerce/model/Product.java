package com.ecommerce.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Product Model Class
 */
public class Product {

    private int productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private int stockQuantity;
    private int categoryId;
    private String imageUrl;
    private boolean isFeatured;
    private boolean isActive;
    private Category category; // For joined queries

    // Constructors
    public Product() {
    }

    public Product(String productName, String description, BigDecimal price,
            BigDecimal discountPrice, int stockQuantity, int categoryId, String imageUrl) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.isFeatured = false;
        this.isActive = true;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * price after discount
     *
     * @return
     */
    public BigDecimal getFinalPrice() {
        if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0) {
            return discountPrice;
        }
        return price;
    }

    /**
     * % calculate discount
     * @return
     */
    public int getDiscountPercentage() {
        if (price == null || discountPrice == null
                || price.compareTo(BigDecimal.ZERO) <= 0
                || discountPrice.compareTo(BigDecimal.ZERO) <= 0
                || price.compareTo(discountPrice) <= 0) {
            return 0;
        }

        try {
            // = ((x - discount)*100)/x
            return price.subtract(discountPrice)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(price, 0, RoundingMode.HALF_UP)
                    .intValue();
                    //init value : type cast BigDecimal -> Integer : cut decimal part
        } catch (ArithmeticException e) {
            return 0;
        }
    }

}
