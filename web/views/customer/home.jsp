<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Trang chủ - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<!-- Hero Banner Slider -->
<div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
    <div class="carousel-indicators">
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="2"></button>
    </div>
    <div class="carousel-inner">
        <div class="carousel-item active">
            <div class="hero-slide" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); height: 400px; display: flex; align-items: center; justify-content: center;">
                <div class="text-center text-white">
                    <h1 class="display-4 fw-bold">Chào mừng đến Ecommerce Store</h1>
                    <p class="lead">Khám phá những sản phẩm công nghệ tuyệt vời</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-light btn-lg mt-3">Mua ngay</a>
                </div>
            </div>
        </div>
        <div class="carousel-item">
            <div class="hero-slide" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); height: 400px; display: flex; align-items: center; justify-content: center;">
                <div class="text-center text-white">
                    <h1 class="display-4 fw-bold">Giảm giá lên đến 50%</h1>
                    <p class="lead">Ưu đãi đặc biệt cho khách hàng mới</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-light btn-lg mt-3">Xem ngay</a>
                </div>
            </div>
        </div>
        <div class="carousel-item">
            <div class="hero-slide" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); height: 400px; display: flex; align-items: center; justify-content: center;">
                <div class="text-center text-white">
                    <h1 class="display-4 fw-bold">Sản phẩm mới nhất</h1>
                    <p class="lead">Cập nhật những công nghệ tiên tiến nhất</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-light btn-lg mt-3">Khám phá</a>
                </div>
            </div>
        </div>
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon"></span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon"></span>
    </button>
</div>

<!-- Categories Section -->
<div class="container my-5">
    <h2 class="text-center mb-4" data-aos="fade-up">Danh mục sản phẩm</h2>
    <div class="row g-4">
        <c:forEach var="category" items="${categories}" varStatus="status">
            <div class="col-md-3 col-sm-6" data-aos="fade-up" data-aos-delay="${status.index * 100}">
                <div class="card category-card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <c:choose>
                            <c:when test="${not empty category.imageUrl}">
                                <div class="category-image-wrapper mb-3" style="width: 100%; height: 200px; display: flex; align-items: center; justify-content: center; border-radius: 8px; overflow: hidden;">
                                    <img src="${category.imageUrl}" 
                                         alt="${category.categoryName}" 
                                         class="category-image"
                                         style="max-width: 100%; max-height: 100%; width: auto; height: auto; object-fit: contain; border-radius: 8px;">
                                </div>
                            </c:when>
                            <c:otherwise>
                                <i class="fas fa-box fa-3x text-primary mb-3"></i>
                            </c:otherwise>
                        </c:choose>
                        <h5 class="card-title">${category.categoryName}</h5>
                        <a href="${pageContext.request.contextPath}/products?category=${category.categoryId}" 
                           class="btn btn-outline-primary btn-sm">Xem sản phẩm</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<!-- Featured Products Section -->
<div class="container my-5">
    <h2 class="text-center mb-4" data-aos="fade-up">Sản phẩm nổi bật</h2>
    <div class="row g-4">
        <c:forEach var="product" items="${featuredProducts}" varStatus="status">
            <div class="col-lg-3 col-md-4 col-sm-6" data-aos="fade-up" data-aos-delay="${status.index * 100}">
                <div class="card product-card h-100 shadow-sm clickable-product-card" 
                     data-product-url="${pageContext.request.contextPath}/product/${product.productUrl}"
                     style="cursor: pointer;">
                    <a href="${pageContext.request.contextPath}/product/${product.productUrl}" class="text-decoration-none">
                        <c:if test="${product.discountPrice != null && product.discountPrice > 0}">
                            <span class="badge bg-danger position-absolute top-0 start-0 m-2">
                                -${product.discountPercentage}%
                            </span>
                        </c:if>
                        <div class="product-image-wrapper" style="width: 100%; height: 200px; display: flex; align-items: center; justify-content: center; border-radius: 8px 8px 0 0; overflow: hidden;">
                            <img src="${product.displayImageUrl}" 
                                 class="card-img-top" 
                                 alt="${product.productName}"
                                 style="max-width: 100%; max-height: 100%; width: auto; height: auto; object-fit: contain;"
                                 onerror="if(this.src.indexOf('data:image') === -1) this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'200\' height=\'200\'%3E%3Crect fill=\'%23ddd\' width=\'200\' height=\'200\'/%3E%3Ctext fill=\'%23999\' font-family=\'sans-serif\' font-size=\'14\' x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\'%3ENo Image%3C/text%3E%3C/svg%3E'">
                        </div>
                    </a>
                    <div class="card-body d-flex flex-column">
                        <h6 class="card-title">
                            <a href="${pageContext.request.contextPath}/product/${product.productUrl}" 
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
                                    data-product-id="${product.productId}"
                                    onclick="event.stopPropagation();">
                                <i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>




