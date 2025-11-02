<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Quản lý sản phẩm - Admin"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container-fluid my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-box me-2"></i>Quản lý sản phẩm</h2>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal">
            <i class="fas fa-plus me-2"></i>Thêm sản phẩm mới
        </button>
    </div>
    
    <div class="card shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Hình ảnh</th>
                            <th>Tên sản phẩm</th>
                            <th>Giá</th>
                            <th>Giá KM</th>
                            <th>Tồn kho</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="product" items="${products}">
                            <tr>
                                <td>
                                    <img src="${product.imageUrl}" alt="${product.productName}" 
                                         class="img-thumbnail" style="width: 60px; height: 60px; object-fit: cover;">
                                </td>
                                <td>${product.productName}</td>
                                <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                <td>
                                    <c:if test="${product.discountPrice != null && product.discountPrice > 0}">
                                        <fmt:formatNumber value="${product.discountPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </c:if>
                                    <c:if test="${product.discountPrice == null || product.discountPrice == 0}">
                                        <span class="text-muted">-</span>
                                    </c:if>
                                </td>
                                <td>
                                    <span class="badge ${product.stockQuantity < 10 ? 'bg-danger' : 'bg-success'}">
                                        ${product.stockQuantity}
                                    </span>
                                </td>
                                <td>
                                    <span class="badge ${product.active ? 'bg-success' : 'bg-secondary'}">
                                        ${product.active ? 'Hoạt động' : 'Ngừng bán'}
                                    </span>
                                </td>
                                <td>
                                    <button class="btn btn-sm btn-warning" onclick="editProduct(${product.productId})">
                                        <i class="fas fa-edit"></i> Sửa
                                    </button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteProduct(${product.productId})">
                                        <i class="fas fa-trash"></i> Xóa
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Add Product Modal -->
<div class="modal fade" id="addProductModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Thêm sản phẩm mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="text-muted">Chức năng này cần được tích hợp với form và servlet để thêm/sửa sản phẩm.</p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

<script>
function editProduct(productId) {
    alert('Chức năng sửa sản phẩm sẽ được tích hợp sau. Product ID: ' + productId);
}

function deleteProduct(productId) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
        alert('Chức năng xóa sản phẩm sẽ được tích hợp sau. Product ID: ' + productId);
    }
}
</script>


