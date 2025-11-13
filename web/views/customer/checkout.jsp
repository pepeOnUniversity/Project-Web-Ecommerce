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
                
                <div class="card shadow-sm mb-4">
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
                
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="fas fa-credit-card me-2"></i>Phương thức thanh toán</h5>
                    </div>
                    <div class="card-body">
                        <div class="form-check mb-3">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="paymentCOD" value="COD" checked>
                            <label class="form-check-label" for="paymentCOD">
                                <i class="fas fa-money-bill-wave me-2"></i>Thanh toán khi nhận hàng (COD)
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="paymentVNPay" value="VNPAY">
                            <label class="form-check-label" for="paymentVNPay">
                                <i class="fas fa-credit-card me-2"></i>Thanh toán online qua VNPay
                            </label>
                        </div>
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
                        <button type="submit" class="btn btn-primary w-100 mt-3 btn-lg" id="submitBtn">
                            <i class="fas fa-check me-2"></i>Xác nhận đặt hàng
                        </button>
                        <div id="paymentInfo" class="mt-3 text-muted small" style="display: none;">
                            <i class="fas fa-info-circle me-1"></i>
                            <span id="paymentInfoText"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<jsp:include page="../common/footer.jsp"/>

<script>
    // Xử lý khi chọn phương thức thanh toán
    document.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
        radio.addEventListener('change', function() {
            const paymentInfo = document.getElementById('paymentInfo');
            const paymentInfoText = document.getElementById('paymentInfoText');
            const submitBtn = document.getElementById('submitBtn');
            
            if (this.value === 'VNPAY') {
                paymentInfo.style.display = 'block';
                paymentInfoText.textContent = 'Bạn sẽ được chuyển đến trang thanh toán VNPay sau khi xác nhận';
                submitBtn.innerHTML = '<i class="fas fa-credit-card me-2"></i>Thanh toán qua VNPay';
            } else {
                paymentInfo.style.display = 'block';
                paymentInfoText.textContent = 'Bạn sẽ thanh toán khi nhận hàng';
                submitBtn.innerHTML = '<i class="fas fa-check me-2"></i>Xác nhận đặt hàng';
            }
        });
    });
    
    // Trigger change event khi page load
    document.querySelector('input[name="paymentMethod"]:checked').dispatchEvent(new Event('change'));
</script>

















