package com.ecommerce.controller;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Home Servlet
 * Xử lý trang chủ
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", "/"})
public class HomeServlet extends HttpServlet {
    
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
        
        try {
            // Lấy featured products (8 sản phẩm)
            List<Product> featuredProducts = productDAO.getFeaturedProducts(8);
            
            // Lấy tất cả categories
            List<Category> categories = categoryDAO.getAllCategories();
            
            // Set attributes cho JSP
            request.setAttribute("featuredProducts", featuredProducts);
            request.setAttribute("categories", categories);
            
            // Forward đến home.jsp
            request.getRequestDispatcher("/views/customer/home.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải trang chủ");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
}



