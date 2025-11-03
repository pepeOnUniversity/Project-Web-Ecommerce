<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Lịch sử đơn hàng - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <h2 class="mb-4"><i class="fas fa-list me-2"></i>Lịch sử đơn hàng</h2>
    
    <c:choose>
        <c:when test="${empty orders}">
            <div class="text-center py-5">
                <i class="fas fa-shopping-bag fa-4x text-muted mb-3"></i>
                <h5 class="text-muted">Bạn chưa có đơn hàng nào</h5>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">
                    <i class="fas fa-shopping-bag me-2"></i>Mua sắm ngay
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <c:forEach var="order" items="${orders}">
                    <div class="col-12 mb-4">
                        <div class="card shadow-sm ${highlightOrderId == order.orderId ? 'border-success border-2' : ''}">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <div>
                                    <strong>Đơn hàng #${order.orderId}</strong>
                                    <br>
                                    <small class="text-muted">
                                        <i class="fas fa-calendar me-1"></i>
                                        <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </small>
                                </div>
                                <div>
                                    <c:choose>
                                        <c:when test="${order.status == 'PENDING'}">
                                            <span class="badge bg-warning">Đang chờ</span>
                                        </c:when>
                                        <c:when test="${order.status == 'CONFIRMED'}">
                                            <span class="badge bg-info">Đã xác nhận</span>
                                        </c:when>
                                        <c:when test="${order.status == 'SHIPPING'}">
                                            <span class="badge bg-primary">Đang giao</span>
                                        </c:when>
                                        <c:when test="${order.status == 'DELIVERED'}">
                                            <span class="badge bg-success">Đã giao</span>
                                        </c:when>
                                        <c:when test="${order.status == 'CANCELLED'}">
                                            <span class="badge bg-danger">Đã hủy</span>
                                        </c:when>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-8">
                                        <h6>Sản phẩm:</h6>
                                        <ul class="list-unstyled">
                                            <c:forEach var="item" items="${order.orderItems}">
                                                <li class="mb-2">
                                                    <i class="fas fa-check-circle text-success me-2"></i>
                                                    ${item.product.productName} x ${item.quantity}
                                                    - <fmt:formatNumber value="${item.getSubtotal()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                        <p class="mb-1"><strong>Địa chỉ giao hàng:</strong> ${order.shippingAddress}</p>
                                        <p class="mb-0"><strong>SĐT:</strong> ${order.phone}</p>
                                    </div>
                                    <div class="col-md-4 text-end">
                                        <h5 class="text-danger">
                                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </h5>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="../common/footer.jsp"/>



