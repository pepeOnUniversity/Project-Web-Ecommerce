<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Giỏ hàng - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <h2 class="mb-4"><i class="fas fa-shopping-cart me-2"></i>Giỏ hàng của bạn</h2>
    
    <c:choose>
        <c:when test="${empty cartItems}">
            <div class="text-center py-5">
                <i class="fas fa-shopping-cart fa-4x text-muted mb-3"></i>
                <h5 class="text-muted">Giỏ hàng của bạn đang trống</h5>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">
                    <i class="fas fa-shopping-bag me-2"></i>Tiếp tục mua sắm
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <div class="col-lg-8">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>Giá</th>
                                        <th>Số lượng</th>
                                        <th>Tổng</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${cartItems}">
                                        <tr data-cart-item-id="${item.cartItemId}">
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <img src="${item.product.displayImageUrl}" alt="${item.product.productName}" 
                                                         onerror="if(this.src.indexOf('data:image') === -1) this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'100\' height=\'100\'%3E%3Crect fill=\'%23ddd\' width=\'100\' height=\'100\'/%3E%3Ctext fill=\'%23999\' font-family=\'sans-serif\' font-size=\'12\' x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\'%3ENo Image%3C/text%3E%3C/svg%3E'" 
                                                         class="img-thumbnail" style="width: 80px; height: 80px; object-fit: cover;">
                                                    <div class="ms-3">
                                                        <strong>${item.product.productName}</strong>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${item.product.getFinalPrice()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </td>
                                            <td>
                                                <input type="number" class="form-control cart-quantity" 
                                                       value="${item.quantity}" min="1" max="${item.product.stockQuantity}"
                                                       data-cart-item-id="${item.cartItemId}" style="width: 80px;">
                                            </td>
                                            <td>
                                                <strong class="item-subtotal">
                                                    <fmt:formatNumber value="${item.getSubtotal()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <button class="btn btn-danger btn-sm btn-remove-cart" 
                                                        data-cart-item-id="${item.cartItemId}">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </td>
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
                            <div class="d-flex justify-content-between mb-2">
                                <span>Tạm tính:</span>
                                <strong id="subtotal">0₫</strong>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between">
                                <strong>Tổng cộng:</strong>
                                <strong class="text-danger fs-4" id="total">0₫</strong>
                            </div>
                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary w-100 mt-3">
                                <i class="fas fa-credit-card me-2"></i>Thanh toán
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
// Tính tổng giỏ hàng
function updateCartTotal() {
    let subtotal = 0;
    document.querySelectorAll('.item-subtotal').forEach(function(el) {
        const text = el.textContent.replace(/[^\d]/g, '');
        subtotal += parseInt(text);
    });
    
    document.getElementById('subtotal').textContent = new Intl.NumberFormat('vi-VN').format(subtotal) + '₫';
    document.getElementById('total').textContent = new Intl.NumberFormat('vi-VN').format(subtotal) + '₫';
}

updateCartTotal();

// Cập nhật số lượng
document.querySelectorAll('.cart-quantity').forEach(function(input) {
    input.addEventListener('change', function() {
        const cartItemId = this.dataset.cartItemId;
        const quantity = this.value;
        updateCartItem(cartItemId, quantity);
    });
});
</script>

<jsp:include page="../common/footer.jsp"/>




