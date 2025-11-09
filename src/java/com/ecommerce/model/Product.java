package com.ecommerce.model;

import com.ecommerce.util.ImagePathUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Product Model Class
 */
public class Product {

    private int productId;
    private String productName;
    private String slug; // URL-friendly identifier
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
    
    /**
     * Lấy URL ảnh đã được xử lý bởi ImagePathUtil
     * Sử dụng method này trong JSP thay vì getImageUrl() trực tiếp
     * 
     * @return URL ảnh đầy đủ hoặc relative URL
     */
    public String getDisplayImageUrl() {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return ImagePathUtil.getDefaultImageUrl();
        }
        return ImagePathUtil.getImageUrl(imageUrl);
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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
     * Check if product has discount
     * @return true if has valid discount
     */
    public boolean hasDiscount() {
        return discountPrice != null 
                && discountPrice.compareTo(BigDecimal.ZERO) > 0
                && price != null
                && price.compareTo(BigDecimal.ZERO) > 0
                && price.compareTo(discountPrice) > 0;
    }
    
    /**
     * Getter for JSP EL to access discount status
     * JSP EL will call this as ${product.discount}
     * @return true if has valid discount
     */
    public boolean isDiscount() {
        return hasDiscount();
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

    /**
     * Get product URL with slug for SEO-friendly URLs
     * Format: /product/{id}-{slug} hoặc /product/{id} nếu chưa có slug
     * @return Product URL path
     */
    public String getProductUrl() {
        if (slug != null && !slug.trim().isEmpty()) {
            return productId + "-" + slug;
        }
        return String.valueOf(productId);
    }

}
