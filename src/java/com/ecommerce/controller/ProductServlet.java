package com.ecommerce.controller;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.util.ImagePathUtil;
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
     */
    private void showProductDetail(HttpServletRequest request, HttpServletResponse response, String productIdStr)
            throws ServletException, IOException {
        
        try {
            int productId = Integer.parseInt(productIdStr);
            Product product = productDAO.getProductById(productId);
            
            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                return;
            }
            
            // Lấy related products (cùng category)
            List<Product> relatedProducts = productDAO.getProductsByCategory(product.getCategoryId());
            relatedProducts.removeIf(p -> p.getProductId() == productId);
            if (relatedProducts.size() > 4) {
                relatedProducts = relatedProducts.subList(0, 4);
            }
            
            request.setAttribute("product", product);
            request.setAttribute("relatedProducts", relatedProducts);
            
            request.getRequestDispatcher("/views/customer/product-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải chi tiết sản phẩm");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
}



