<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Thanh toán - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <h2 class="mb-4"><i class="fas fa-credit-card me-2"></i>Thanh toán</h2>
    
    <c:if test="${error != null}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    
    <form method="post" action="${pageContext.request.contextPath}/checkout">
        <div class="row">
            <div class="col-lg-8">
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="fas fa-truck me-2"></i>Thông tin giao hàng</h5>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <label for="shippingAddress" class="form-label">Địa chỉ giao hàng <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="shippingAddress" name="shippingAddress" rows="3" required>${user.address}</textarea>
                        </div>
                        
                        <div class="mb-3">
                            <label for="phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="tel" class="form-control" id="phone" name="phone" 
                                   value="${user.phone}" required>
                        </div>
                    </div>
                </div>
                
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="fas fa-box me-2"></i>Đơn hàng</h5>
                    </div>
                    <div class="card-body">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Sản phẩm</th>
                                    <th>Số lượng</th>
                                    <th>Giá</th>
                                    <th>Tổng</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${cartItems}">
                                    <tr>
                                        <td>${item.product.productName}</td>
                                        <td>${item.quantity}</td>
                                        <td><fmt:formatNumber value="${item.product.getFinalPrice()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                        <td><fmt:formatNumber value="${item.getSubtotal()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-4">
                <div class="card shadow-sm sticky-top" style="top: 20px;">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">Tổng thanh toán</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-flex justify-content-between mb-3">
                            <span>Tạm tính:</span>
                            <strong><fmt:formatNumber value="${totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></strong>
                        </div>
                        <div class="d-flex justify-content-between mb-3">
                            <span>Phí vận chuyển:</span>
                            <strong>Miễn phí</strong>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <strong>Tổng cộng:</strong>
                            <strong class="text-danger fs-4"><fmt:formatNumber value="${totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></strong>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 mt-3 btn-lg">
                            <i class="fas fa-check me-2"></i>Xác nhận đặt hàng
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<jsp:include page="../common/footer.jsp"/>




