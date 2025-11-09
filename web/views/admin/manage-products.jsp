<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Quản lý sản phẩm - Admin"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container-fluid my-4">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin"><i class="fas fa-home me-1"></i>Dashboard</a></li>
            <li class="breadcrumb-item active" aria-current="page">Quản lý sản phẩm</li>
        </ol>
    </nav>
    
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-box me-2"></i>Quản lý sản phẩm</h2>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal">
            <i class="fas fa-plus me-2"></i>Thêm sản phẩm mới
        </button>
    </div>
    
    <!-- Success/Error Messages -->
    <c:if test="${param.success != null}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <c:choose>
                <c:when test="${param.success == 'add'}">
                    <i class="fas fa-check-circle me-2"></i>Thêm sản phẩm thành công!
                </c:when>
                <c:when test="${param.success == 'update'}">
                    <i class="fas fa-check-circle me-2"></i>Cập nhật sản phẩm thành công!
                </c:when>
                <c:when test="${param.success == 'delete'}">
                    <i class="fas fa-check-circle me-2"></i>Xóa sản phẩm thành công!
                </c:when>
            </c:choose>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${param.error != null}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <c:choose>
                <c:when test="${param.error == 'add'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Có lỗi xảy ra khi thêm sản phẩm!
                </c:when>
                <c:when test="${param.error == 'update'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Có lỗi xảy ra khi cập nhật sản phẩm!
                </c:when>
                <c:when test="${param.error == 'delete'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Có lỗi xảy ra khi xóa sản phẩm!
                </c:when>
                <c:when test="${param.error == 'notfound'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Không tìm thấy sản phẩm!
                </c:when>
                <c:otherwise>
                    <i class="fas fa-exclamation-circle me-2"></i>Có lỗi xảy ra!
                </c:otherwise>
            </c:choose>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
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

<!-- Add/Edit Product Modal -->
<div class="modal fade" id="addProductModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">Thêm sản phẩm mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="productForm" method="POST" action="${pageContext.request.contextPath}/admin/products">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="productId" id="formProductId">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="productName" class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="productName" name="productName" required>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Mô tả</label>
                        <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="price" class="form-label">Giá <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="price" name="price" step="0.01" min="0" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="discountPrice" class="form-label">Giá khuyến mãi</label>
                            <input type="number" class="form-control" id="discountPrice" name="discountPrice" step="0.01" min="0">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="stockQuantity" class="form-label">Số lượng tồn kho <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="stockQuantity" name="stockQuantity" min="0" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="categoryId" class="form-label">Danh mục <span class="text-danger">*</span></label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <option value="">Chọn danh mục</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.categoryId}">${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="imageUrl" class="form-label">URL hình ảnh <span class="text-danger">*</span></label>
                        <input type="url" class="form-control" id="imageUrl" name="imageUrl" required>
                        <small class="form-text text-muted">Nhập URL hình ảnh của sản phẩm</small>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="isFeatured" name="isFeatured" value="true">
                                <label class="form-check-label" for="isFeatured">
                                    Sản phẩm nổi bật
                                </label>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="isActive" name="isActive" value="true" checked>
                                <label class="form-check-label" for="isActive">
                                    Đang hoạt động
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

<script>
// Edit product function
function editProduct(productId) {
    // Fetch product data via AJAX or use server-side data
    fetch('${pageContext.request.contextPath}/admin/products?action=get&productId=' + productId)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.product) {
                const product = data.product;
                document.getElementById('modalTitle').textContent = 'Sửa sản phẩm';
                document.getElementById('formAction').value = 'update';
                document.getElementById('formProductId').value = product.productId;
                document.getElementById('productName').value = product.productName || '';
                document.getElementById('description').value = product.description || '';
                document.getElementById('price').value = product.price || '';
                document.getElementById('discountPrice').value = product.discountPrice || '';
                document.getElementById('stockQuantity').value = product.stockQuantity || '';
                document.getElementById('categoryId').value = product.categoryId || '';
                document.getElementById('imageUrl').value = product.imageUrl || '';
                document.getElementById('isFeatured').checked = product.featured || false;
                document.getElementById('isActive').checked = product.active !== false;
                
                const modal = new bootstrap.Modal(document.getElementById('addProductModal'));
                modal.show();
            } else {
                alert('Không thể tải thông tin sản phẩm');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi tải thông tin sản phẩm');
        });
}

// Delete product function
function deleteProduct(productId) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/admin/products';
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'delete';
        form.appendChild(actionInput);
        
        const productIdInput = document.createElement('input');
        productIdInput.type = 'hidden';
        productIdInput.name = 'productId';
        productIdInput.value = productId;
        form.appendChild(productIdInput);
        
        document.body.appendChild(form);
        form.submit();
    }
}

// Reset form when modal is closed
document.getElementById('addProductModal').addEventListener('hidden.bs.modal', function() {
    document.getElementById('productForm').reset();
    document.getElementById('modalTitle').textContent = 'Thêm sản phẩm mới';
    document.getElementById('formAction').value = 'add';
    document.getElementById('formProductId').value = '';
    document.getElementById('isActive').checked = true;
});
</script>



