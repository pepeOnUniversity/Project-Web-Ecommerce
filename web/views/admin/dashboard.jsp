<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Admin Dashboard - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container-fluid my-4">
    <h2 class="mb-4"><i class="fas fa-chart-line me-2"></i>Admin Dashboard</h2>
    
    <!-- Statistics Cards -->
    <div class="row g-4 mb-4">
        <div class="col-md-3">
            <div class="card shadow-sm border-primary">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-muted mb-1">Tổng doanh thu</h6>
                            <h3 class="mb-0 text-primary">
                                <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </h3>
                        </div>
                        <i class="fas fa-dollar-sign fa-3x text-primary opacity-25"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-3">
            <div class="card shadow-sm border-success">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-muted mb-1">Tổng đơn hàng</h6>
                            <h3 class="mb-0 text-success">${totalOrders}</h3>
                        </div>
                        <i class="fas fa-shopping-bag fa-3x text-success opacity-25"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-3">
            <div class="card shadow-sm border-info">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-muted mb-1">Tổng sản phẩm</h6>
                            <h3 class="mb-0 text-info">${totalProducts}</h3>
                        </div>
                        <i class="fas fa-box fa-3x text-info opacity-25"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-3">
            <div class="card shadow-sm border-warning">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-muted mb-1">Tổng khách hàng</h6>
                            <h3 class="mb-0 text-warning">${totalCustomers}</h3>
                        </div>
                        <i class="fas fa-users fa-3x text-warning opacity-25"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Recent Orders -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="fas fa-list me-2"></i>Đơn hàng gần đây</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Khách hàng</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày đặt</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${recentOrders}">
                                    <tr>
                                        <td>#${order.orderId}</td>
                                        <td>${order.user.fullName}</td>
                                        <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                        <td>
                                            <span class="badge bg-warning">${order.status}</span>
                                        </td>
                                        <td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-primary">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Low Stock Products -->
    <c:if test="${not empty lowStockProducts}">
        <div class="row">
            <div class="col-12">
                <div class="card shadow-sm border-warning">
                    <div class="card-header bg-warning text-dark">
                        <h5 class="mb-0"><i class="fas fa-exclamation-triangle me-2"></i>Sản phẩm sắp hết hàng</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>Số lượng còn lại</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="product" items="${lowStockProducts}">
                                        <tr>
                                            <td>${product.productName}</td>
                                            <td><span class="badge bg-danger">${product.stockQuantity}</span></td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-sm btn-warning">
                                                    <i class="fas fa-edit"></i> Cập nhật
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="../common/footer.jsp"/>




