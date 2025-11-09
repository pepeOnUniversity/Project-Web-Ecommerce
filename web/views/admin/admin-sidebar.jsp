<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String currentPage = request.getParameter("currentPage");
    if (currentPage == null) {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/admin/products")) {
            currentPage = "products";
        } else if (requestURI.contains("/admin/orders")) {
            currentPage = "orders";
        } else {
            currentPage = "dashboard";
        }
    }
    pageContext.setAttribute("currentPage", currentPage);
%>
<div class="col-md-3 mb-4">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0"><i class="fas fa-cog me-2"></i>Admin Menu</h5>
        </div>
        <div class="list-group list-group-flush">
            <a href="${pageContext.request.contextPath}/admin" 
               class="list-group-item list-group-item-action ${currentPage == 'dashboard' ? 'active' : ''}">
                <i class="fas fa-chart-line me-2"></i>Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/admin/products" 
               class="list-group-item list-group-item-action ${currentPage == 'products' ? 'active' : ''}">
                <i class="fas fa-box me-2"></i>Quản lý sản phẩm
            </a>
            <a href="${pageContext.request.contextPath}/admin/orders" 
               class="list-group-item list-group-item-action ${currentPage == 'orders' ? 'active' : ''}">
                <i class="fas fa-shopping-bag me-2"></i>Quản lý đơn hàng
            </a>
        </div>
    </div>
</div>

