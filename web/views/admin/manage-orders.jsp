<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Quản lý đơn hàng - Admin"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container-fluid my-4">
    <div class="row">
        <jsp:include page="admin-sidebar.jsp">
            <jsp:param name="currentPage" value="orders"/>
        </jsp:include>
        
        <!-- Main Content -->
        <div class="col-md-9">
    <h2 class="mb-4"><i class="fas fa-shopping-bag me-2"></i>Quản lý đơn hàng</h2>
    
    <!-- Success/Error Messages -->
    <c:if test="${param.success != null}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>Cập nhật trạng thái đơn hàng thành công!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${param.error != null}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i>Có lỗi xảy ra khi cập nhật trạng thái đơn hàng!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <div class="card shadow-sm">
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
                        <c:choose>
                            <c:when test="${empty orders}">
                                <tr>
                                    <td colspan="6" class="text-center py-5">
                                        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                        <p class="text-muted">Chưa có đơn hàng nào</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                        <c:forEach var="order" items="${orders}">
                            <tr>
                                <td>#${order.orderId}</td>
                                <td>${order.user.fullName}</td>
                                <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="d-inline">
                                        <input type="hidden" name="action" value="updateOrderStatus">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <select name="status" class="form-select form-select-sm d-inline-block" style="width: auto;" 
                                                onchange="this.form.submit()">
                                            <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Đang chờ</option>
                                            <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
                                            <option value="SHIPPING" ${order.status == 'SHIPPING' ? 'selected' : ''}>Đang giao</option>
                                            <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Đã giao</option>
                                            <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                    </form>
                                </td>
                                <td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                    <button class="btn btn-sm btn-info" onclick="viewOrderDetails(${order.orderId})">
                                        <i class="fas fa-eye"></i> Chi tiết
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>





