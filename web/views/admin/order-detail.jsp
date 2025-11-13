<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Chi tiết đơn hàng #${order.orderId} - Admin"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container-fluid my-4">
    <div class="row">
        <jsp:include page="admin-sidebar.jsp">
            <jsp:param name="currentPage" value="orders"/>
        </jsp:include>
        
        <!-- Main Content -->
        <div class="col-md-9">
            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="mb-0">
                    <i class="fas fa-file-invoice me-2"></i>Chi tiết đơn hàng #${order.orderId}
                </h2>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Quay lại
                </a>
            </div>
            
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
            
            <c:if test="${order == null}">
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle me-2"></i>Không tìm thấy đơn hàng
                </div>
            </c:if>
            
            <c:if test="${order != null}">
                <!-- Order Info Card -->
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Thông tin đơn hàng</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <table class="table table-borderless">
                                    <tr>
                                        <th width="40%">Mã đơn hàng:</th>
                                        <td><strong>#${order.orderId}</strong></td>
                                    </tr>
                                    <tr>
                                        <th>Ngày đặt hàng:</th>
                                        <td>
                                            <i class="fas fa-calendar me-2"></i>
                                            <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Trạng thái:</th>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.status == 'PENDING'}">
                                                    <span class="badge bg-warning text-dark">Đang chờ</span>
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
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${order.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Phương thức thanh toán:</th>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.paymentMethod == 'VNPAY'}">
                                                    <span class="badge bg-info">
                                                        <i class="fas fa-credit-card me-1"></i>VNPay
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.paymentMethod == 'COD'}">
                                                    <span class="badge bg-secondary">
                                                        <i class="fas fa-money-bill-wave me-1"></i>Thanh toán khi nhận hàng
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${order.paymentMethod != null ? order.paymentMethod : 'COD'}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Trạng thái thanh toán:</th>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.paymentStatus == 'PAID'}">
                                                    <span class="badge bg-success">
                                                        <i class="fas fa-check-circle me-1"></i>Đã thanh toán
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.paymentStatus == 'PENDING'}">
                                                    <span class="badge bg-warning text-dark">
                                                        <i class="fas fa-clock me-1"></i>Chờ thanh toán
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.paymentStatus == 'FAILED'}">
                                                    <span class="badge bg-danger">
                                                        <i class="fas fa-times-circle me-1"></i>Thanh toán thất bại
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.paymentStatus == 'CANCELLED'}">
                                                    <span class="badge bg-secondary">
                                                        <i class="fas fa-ban me-1"></i>Đã hủy
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.paymentStatus == 'REFUNDED'}">
                                                    <span class="badge bg-info">
                                                        <i class="fas fa-undo me-1"></i>Đã hoàn tiền
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${order.paymentStatus != null ? order.paymentStatus : 'PENDING'}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    <c:if test="${order.vnpTransactionId != null && !order.vnpTransactionId.isEmpty()}">
                                        <tr>
                                            <th>Mã giao dịch VNPay:</th>
                                            <td><code>${order.vnpTransactionId}</code></td>
                                        </tr>
                                    </c:if>
                                </table>
                            </div>
                            <div class="col-md-6">
                                <table class="table table-borderless">
                                    <tr>
                                        <th width="40%">Khách hàng:</th>
                                        <td>
                                            <strong>${order.user.fullName}</strong>
                                            <br>
                                            <small class="text-muted">${order.user.email}</small>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Số điện thoại:</th>
                                        <td>
                                            <i class="fas fa-phone me-2"></i>${order.phone}
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Địa chỉ giao hàng:</th>
                                        <td>
                                            <i class="fas fa-map-marker-alt me-2"></i>${order.shippingAddress}
                                        </td>
                                    </tr>
                                    <c:if test="${order.notes != null && !order.notes.isEmpty()}">
                                        <tr>
                                            <th>Ghi chú:</th>
                                            <td>${order.notes}</td>
                                        </tr>
                                    </c:if>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Order Items Card -->
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0"><i class="fas fa-shopping-cart me-2"></i>Danh sách sản phẩm</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Sản phẩm</th>
                                        <th class="text-center">Số lượng</th>
                                        <th class="text-end">Đơn giá</th>
                                        <th class="text-end">Thành tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${order.orderItems}" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <c:if test="${item.product.imageUrl != null && !item.product.imageUrl.isEmpty()}">
                                                        <img src="${pageContext.request.contextPath}${item.product.imageUrl}" 
                                                             alt="${item.product.productName}" 
                                                             class="me-3" 
                                                             style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;">
                                                    </c:if>
                                                    <div>
                                                        <strong>${item.product.productName}</strong>
                                                        <br>
                                                        <small class="text-muted">Mã SP: #${item.product.productId}</small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="text-center">${item.quantity}</td>
                                            <td class="text-end">
                                                <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </td>
                                            <td class="text-end">
                                                <strong>
                                                    <fmt:formatNumber value="${item.getSubtotal()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </strong>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="4" class="text-end"><strong>Tổng cộng:</strong></td>
                                        <td class="text-end">
                                            <h5 class="text-danger mb-0">
                                                <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </h5>
                                        </td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- Update Status Card -->
                <div class="card shadow-sm">
                    <div class="card-header bg-warning text-dark">
                        <h5 class="mb-0"><i class="fas fa-edit me-2"></i>Cập nhật trạng thái đơn hàng</h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/admin/orders">
                            <input type="hidden" name="action" value="updateOrderStatus">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <input type="hidden" name="returnToDetail" value="true">
                            <div class="row align-items-end">
                                <div class="col-md-6">
                                    <label for="status" class="form-label">Trạng thái mới:</label>
                                    <select name="status" id="status" class="form-select">
                                        <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Đang chờ</option>
                                        <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
                                        <option value="SHIPPING" ${order.status == 'SHIPPING' ? 'selected' : ''}>Đang giao</option>
                                        <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Đã giao</option>
                                        <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <button type="submit" class="btn btn-primary w-100">
                                        <i class="fas fa-save me-2"></i>Cập nhật trạng thái
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

