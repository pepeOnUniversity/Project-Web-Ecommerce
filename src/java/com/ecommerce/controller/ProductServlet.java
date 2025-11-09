package com.ecommerce.controller;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.util.SlugUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Product Servlet
 * Xử lý hiển thị danh sách sản phẩm và chi tiết sản phẩm
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/products", "/product/*"})
public class ProductServlet extends HttpServlet {
    
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    
    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // Nếu có pathInfo, hiển thị chi tiết sản phẩm
        if (pathInfo != null && pathInfo.length() > 1) {
            showProductDetail(request, response, pathInfo.substring(1));
        } else {
            // Nếu không, hiển thị danh sách sản phẩm
            showProductList(request, response);
        }
    }
    
    /**
     * Hiển thị danh sách sản phẩm
     */
    private void showProductList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            
            String categoryIdParam = request.getParameter("category");
            String searchKeyword = request.getParameter("search");
            String sortBy = request.getParameter("sort");
            
            List<Product> products;
            
            // Lọc theo category
            if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                try {
                    int categoryId = Integer.parseInt(categoryIdParam);
                    products = productDAO.getProductsByCategory(categoryId);
                } catch (NumberFormatException e) {
                    products = productDAO.getAllProducts();
                }
            }
            // Tìm kiếm
            else if (searchKeyword != null && !searchKeyword.isEmpty()) {
                products = productDAO.searchProducts(searchKeyword);
            }
            // Tất cả sản phẩm
            else {
                products = productDAO.getAllProducts();
            }
            
            // Sắp xếp
            if (sortBy != null) {
                switch (sortBy) {
                    case "price_asc":
                        products.sort((p1, p2) -> p1.getFinalPrice().compareTo(p2.getFinalPrice()));
                        break;
                    case "price_desc":
                        products.sort((p1, p2) -> p2.getFinalPrice().compareTo(p1.getFinalPrice()));
                        break;
                    case "newest":
                        // Đã được sort DESC theo ID trong DAO
                        break;
                }
            }
            
            request.setAttribute("products", products);
            request.setAttribute("selectedCategory", categoryIdParam);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("sortBy", sortBy);
            
            request.getRequestDispatcher("/views/customer/products.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải danh sách sản phẩm");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Hiển thị chi tiết sản phẩm
     * Hỗ trợ cả ID và slug: /product/123 hoặc /product/123-slug-name hoặc /product/slug-name
     */
    private void showProductDetail(HttpServletRequest request, HttpServletResponse response, String pathSegment)
            throws ServletException, IOException {
        
        try {
            Product product = null;
            
            // Thử extract ID từ path (format: "123-slug" hoặc chỉ "123")
            Integer productId = SlugUtil.extractProductId(pathSegment);
            
            if (productId != null) {
                // Có ID trong URL, dùng ID để lookup
                product = productDAO.getProductById(productId);
                
                // Nếu tìm thấy product và có slug, redirect đến URL có slug (SEO-friendly)
                if (product != null && product.getSlug() != null && !product.getSlug().isEmpty()) {
                    String expectedUrl = productId + "-" + product.getSlug();
                    if (!pathSegment.equals(expectedUrl)) {
                        // Redirect đến URL đúng với slug
                        String redirectUrl = request.getContextPath() + "/product/" + expectedUrl;
                        response.sendRedirect(redirectUrl);
                        return;
                    }
                }
            } else {
                // Không có ID, thử lookup bằng slug
                product = productDAO.getProductBySlug(pathSegment);
            }
            
            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                return;
            }
            
            // Lấy related products (cùng category)
            List<Product> relatedProducts = productDAO.getProductsByCategory(product.getCategoryId());
            final int currentProductId = product.getProductId();
            relatedProducts.removeIf(p -> p.getProductId() == currentProductId);
            if (relatedProducts.size() > 4) {
                relatedProducts = relatedProducts.subList(0, 4);
            }
            
            request.setAttribute("product", product);
            request.setAttribute("relatedProducts", relatedProducts);
            
            request.getRequestDispatcher("/views/customer/product-detail.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải chi tiết sản phẩm");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
}



