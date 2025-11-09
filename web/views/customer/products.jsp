<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Sản phẩm - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-4">
    <div class="row">
        <!-- Sidebar Filter -->
        <div class="col-lg-3 mb-4">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="fas fa-filter me-2"></i>Bộ lọc</h5>
                </div>
                <div class="card-body">
                    <h6 class="fw-bold">Danh mục</h6>
                    <div class="list-group">
                        <a href="${pageContext.request.contextPath}/products" 
                           class="list-group-item list-group-item-action ${selectedCategory == null ? 'active' : ''}">
                            Tất cả
                        </a>
                        <c:forEach var="cat" items="${categories}">
                            <a href="${pageContext.request.contextPath}/products?category=${cat.categoryId}" 
                               class="list-group-item list-group-item-action ${selectedCategory == cat.categoryId.toString() ? 'active' : ''}">
                                ${cat.categoryName}
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Products Grid -->
        <div class="col-lg-9">
            <!-- Search and Sort Bar -->
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/products" class="row g-3">
                        <div class="col-md-6">
                            <input type="text" name="search" class="form-control" 
                                   placeholder="Tìm kiếm sản phẩm..." value="${searchKeyword}">
                        </div>
                        <div class="col-md-4">
                            <select name="sort" class="form-select">
                                <option value="">Sắp xếp</option>
                                <option value="newest" ${sortBy == 'newest' ? 'selected' : ''}>Mới nhất</option>
                                <option value="price_asc" ${sortBy == 'price_asc' ? 'selected' : ''}>Giá: Thấp đến cao</option>
                                <option value="price_desc" ${sortBy == 'price_desc' ? 'selected' : ''}>Giá: Cao đến thấp</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="fas fa-search me-1"></i>Tìm
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            
            <!-- Products List -->
            <c:if test="${empty products}">
                <div class="text-center py-5">
                    <i class="fas fa-box-open fa-4x text-muted mb-3"></i>
                    <h5 class="text-muted">Không tìm thấy sản phẩm nào</h5>
                </div>
            </c:if>
            
            <div class="row g-4">
                <c:forEach var="product" items="${products}">
                    <div class="col-lg-3 col-md-4 col-sm-6" data-aos="fade-up">
                        <div class="card product-card h-100 shadow-sm">
                            <a href="${pageContext.request.contextPath}/product/${product.productId}">
                                <c:if test="${product.discountPrice != null && product.discountPrice > 0}">
                                    <span class="badge bg-danger position-absolute top-0 start-0 m-2">
                                        -${product.discountPercentage}%
                                    </span>
                                </c:if>
                                <img src="${product.displayImageUrl}" class="card-img-top" alt="${product.productName}" 
                                     style="height: 200px; object-fit: cover;" 
                                     onerror="if(this.src.indexOf('data:image') === -1) this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'200\' height=\'200\'%3E%3Crect fill=\'%23ddd\' width=\'200\' height=\'200\'/%3E%3Ctext fill=\'%23999\' font-family=\'sans-serif\' font-size=\'14\' x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\'%3ENo Image%3C/text%3E%3C/svg%3E'">
                            </a>
                            <div class="card-body d-flex flex-column">
                                <h6 class="card-title">
                                    <a href="${pageContext.request.contextPath}/product/${product.productId}" 
                                       class="text-decoration-none text-dark">
                                        ${product.productName}
                                    </a>
                                </h6>
                                <div class="mt-auto">
                                    <div class="d-flex align-items-center mb-2">
                                        <span class="text-danger fw-bold fs-5">
                                            <fmt:formatNumber value="${product.getFinalPrice()}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </span>
                                        <c:if test="${product.discountPrice != null && product.discountPrice > 0}">
                                            <small class="text-muted text-decoration-line-through ms-2">
                                                <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </small>
                                        </c:if>
                                    </div>
                                    <button class="btn btn-primary w-100 btn-add-cart" 
                                            data-product-id="${product.productId}">
                                        <i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>



