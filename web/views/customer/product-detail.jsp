<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="${product.productName} - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <c:if test="${product == null}">
        <div class="alert alert-danger">Sản phẩm không tồn tại</div>
    </c:if>
    
    <c:if test="${product != null}">
        <div class="row">
            <div class="col-md-6">
                <img src="${product.imageUrl}" class="img-fluid rounded shadow" alt="${product.productName}">
            </div>
            <div class="col-md-6">
                <h2>${product.productName}</h2>
                
                <div class="my-3">
                    <c:if test="${product.discountPrice != null && product.discountPrice > 0}">
                        <span class="badge bg-danger fs-6">-${product.discountPercentage}%</span>
                    </c:if>
                </div>
                
                <div class="mb-3">
                    <span class="text-danger fw-bold fs-3">
                        <fmt:formatNumber value="${product.getFinalPrice()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </span>
                    <c:if test="${product.discountPrice != null && product.discountPrice > 0}">
                        <span class="text-muted text-decoration-line-through ms-3 fs-5">
                            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </span>
                    </c:if>
                </div>
                
                <p class="text-muted">${product.description}</p>
                
                <div class="mb-3">
                    <strong>Trạng thái:</strong>
                    <c:if test="${product.stockQuantity > 0}">
                        <span class="badge bg-success ms-2">Còn hàng (${product.stockQuantity})</span>
                    </c:if>
                    <c:if test="${product.stockQuantity <= 0}">
                        <span class="badge bg-danger ms-2">Hết hàng</span>
                    </c:if>
                </div>
                
                <div class="d-flex gap-2 mb-3">
                    <input type="number" id="quantity" class="form-control" value="1" min="1" max="${product.stockQuantity}" 
                           style="width: 100px;" ${product.stockQuantity <= 0 ? 'disabled' : ''}>
                    <button class="btn btn-primary btn-lg flex-grow-1 btn-add-cart" 
                            data-product-id="${product.productId}"
                            ${product.stockQuantity <= 0 ? 'disabled' : ''}>
                        <i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ hàng
                    </button>
                </div>
            </div>
        </div>
        
        <!-- Related Products -->
        <c:if test="${not empty relatedProducts}">
            <hr class="my-5">
            <h3 class="mb-4">Sản phẩm liên quan</h3>
            <div class="row g-4">
                <c:forEach var="related" items="${relatedProducts}">
                    <div class="col-md-3">
                        <div class="card product-card h-100 shadow-sm">
                            <a href="${pageContext.request.contextPath}/product/${related.productUrl}">
                                <img src="${related.imageUrl}" class="card-img-top" alt="${related.productName}" 
                                     style="height: 200px; object-fit: cover;">
                            </a>
                            <div class="card-body">
                                <h6 class="card-title">
                                    <a href="${pageContext.request.contextPath}/product/${related.productUrl}" 
                                       class="text-decoration-none text-dark">
                                        ${related.productName}
                                    </a>
                                </h6>
                                <div class="d-flex align-items-center">
                                    <span class="text-danger fw-bold">
                                        <fmt:formatNumber value="${related.getFinalPrice()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </c:if>
</div>

<jsp:include page="../common/footer.jsp"/>




