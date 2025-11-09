<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Quản lý sản phẩm - Admin"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container-fluid my-4">
    <!-- Success/Error Messages -->
    <c:if test="${param.success != null}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <c:choose>
                <c:when test="${param.success == 'added'}">
                    <i class="fas fa-check-circle me-2"></i>Thêm sản phẩm thành công!
                </c:when>
                <c:when test="${param.success == 'updated'}">
                    <i class="fas fa-check-circle me-2"></i>Cập nhật sản phẩm thành công!
                </c:when>
                <c:when test="${param.success == 'deleted'}">
                    <i class="fas fa-check-circle me-2"></i>Xóa sản phẩm thành công!
                </c:when>
            </c:choose>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${param.error != null}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <c:choose>
                <c:when test="${param.error == 'add_failed'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Thêm sản phẩm thất bại!
                </c:when>
                <c:when test="${param.error == 'update_failed'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Cập nhật sản phẩm thất bại!
                </c:when>
                <c:when test="${param.error == 'delete_failed'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Xóa sản phẩm thất bại!
                </c:when>
                <c:otherwise>
                    <i class="fas fa-exclamation-circle me-2"></i>Có lỗi xảy ra: ${param.error}
                </c:otherwise>
            </c:choose>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-box me-2"></i>Quản lý sản phẩm</h2>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal" onclick="resetAddForm()">
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
                                    <img src="${product.displayImageUrl}" alt="${product.productName}" 
                                         class="img-thumbnail" style="width: 60px; height: 60px; object-fit: cover;"
                                         onerror="if(this.src.indexOf('data:image') === -1) this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'60\' height=\'60\'%3E%3Crect fill=\'%23ddd\' width=\'60\' height=\'60\'/%3E%3Ctext fill=\'%23999\' font-family=\'sans-serif\' font-size=\'10\' x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\'%3ENo Image%3C/text%3E%3C/svg%3E'">
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
            <form id="addProductForm" action="${pageContext.request.contextPath}/admin/products" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title">Thêm sản phẩm mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="productName" class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="productName" name="productName" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="categoryId" class="form-label">Danh mục <span class="text-danger">*</span></label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <option value="">Chọn danh mục</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}">${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Mô tả</label>
                        <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="price" class="form-label">Giá <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="price" name="price" step="0.01" min="0" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="discountPrice" class="form-label">Giá khuyến mãi</label>
                            <input type="number" class="form-control" id="discountPrice" name="discountPrice" step="0.01" min="0">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="stockQuantity" class="form-label">Số lượng <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="stockQuantity" name="stockQuantity" min="0" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="productImage" class="form-label">Hình ảnh sản phẩm</label>
                        <input type="file" class="form-control" id="productImage" name="productImage" accept="image/*">
                        <small class="form-text text-muted">Chấp nhận: JPG, PNG, GIF, WEBP (tối đa 10MB)</small>
                        <div id="imagePreview" class="mt-2" style="display: none;">
                            <img id="previewImg" src="" alt="Preview" style="max-width: 200px; max-height: 200px; object-fit: cover;">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="isFeatured" name="isFeatured">
                                <label class="form-check-label" for="isFeatured">
                                    Sản phẩm nổi bật
                                </label>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="isActive" name="isActive" checked>
                                <label class="form-check-label" for="isActive">
                                    Đang bán
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Thêm sản phẩm</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Product Modal -->
<div class="modal fade" id="editProductModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form id="editProductForm" action="${pageContext.request.contextPath}/admin/products" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editProductId" name="productId">
                <div class="modal-header">
                    <h5 class="modal-title">Sửa sản phẩm</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editProductName" class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="editProductName" name="productName" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editCategoryId" class="form-label">Danh mục <span class="text-danger">*</span></label>
                            <select class="form-select" id="editCategoryId" name="categoryId" required>
                                <option value="">Chọn danh mục</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}">${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="editDescription" class="form-label">Mô tả</label>
                        <textarea class="form-control" id="editDescription" name="description" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="editPrice" class="form-label">Giá <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="editPrice" name="price" step="0.01" min="0" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="editDiscountPrice" class="form-label">Giá khuyến mãi</label>
                            <input type="number" class="form-control" id="editDiscountPrice" name="discountPrice" step="0.01" min="0">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="editStockQuantity" class="form-label">Số lượng <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="editStockQuantity" name="stockQuantity" min="0" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="editProductImage" class="form-label">Hình ảnh sản phẩm</label>
                        <input type="file" class="form-control" id="editProductImage" name="productImage" accept="image/*">
                        <small class="form-text text-muted">Chấp nhận: JPG, PNG, GIF, WEBP (tối đa 10MB). Để trống nếu không muốn thay đổi.</small>
                        <div id="editImagePreview" class="mt-2">
                            <img id="editPreviewImg" src="" alt="Current image" style="max-width: 200px; max-height: 200px; object-fit: cover;">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="editIsFeatured" name="isFeatured">
                                <label class="form-check-label" for="editIsFeatured">
                                    Sản phẩm nổi bật
                                </label>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="editIsActive" name="isActive">
                                <label class="form-check-label" for="editIsActive">
                                    Đang bán
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-warning">Cập nhật</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

<script>
// Preview ảnh khi chọn file (Add form)
document.getElementById('productImage').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('previewImg').src = e.target.result;
            document.getElementById('imagePreview').style.display = 'block';
        };
        reader.readAsDataURL(file);
    } else {
        document.getElementById('imagePreview').style.display = 'none';
    }
});

// Preview ảnh khi chọn file (Edit form)
document.getElementById('editProductImage').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('editPreviewImg').src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

// Reset form khi mở modal add
function resetAddForm() {
    document.getElementById('addProductForm').reset();
    document.getElementById('imagePreview').style.display = 'none';
    document.getElementById('isActive').checked = true;
}

// Edit product
function editProduct(productId) {
    // Fetch product data via AJAX
    fetch('${pageContext.request.contextPath}/admin/products?action=edit&id=' + productId)
        .then(response => response.text())
        .then(html => {
            // Parse response và lấy product data từ server
            // Tạm thời dùng cách đơn giản: load lại trang với param
            window.location.href = '${pageContext.request.contextPath}/admin/products?action=edit&id=' + productId;
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Không thể tải thông tin sản phẩm');
        });
}

// Load product data vào edit form (khi có product trong request)
<c:if test="${product != null}">
    document.addEventListener('DOMContentLoaded', function() {
        // Fill form trực tiếp từ JSP để tránh lỗi escape
        document.getElementById('editProductId').value = ${product.productId};
        document.getElementById('editProductName').value = '<c:out value="${product.productName}" escapeXml="true" />';
        <c:set var="descValue" value="${product.description != null ? product.description : ''}" />
        document.getElementById('editDescription').value = '<c:out value="${descValue}" escapeXml="true" />';
        document.getElementById('editPrice').value = ${product.price};
        <c:choose>
            <c:when test="${product.discountPrice != null}">
                document.getElementById('editDiscountPrice').value = ${product.discountPrice};
            </c:when>
            <c:otherwise>
                document.getElementById('editDiscountPrice').value = '';
            </c:otherwise>
        </c:choose>
        document.getElementById('editStockQuantity').value = ${product.stockQuantity};
        document.getElementById('editCategoryId').value = ${product.categoryId};
        document.getElementById('editIsFeatured').checked = ${product.featured};
        document.getElementById('editIsActive').checked = ${product.active};
        document.getElementById('editPreviewImg').src = '<c:out value="${product.displayImageUrl}" escapeXml="true"/>';
        
        // Show modal
        const editModal = new bootstrap.Modal(document.getElementById('editProductModal'));
        editModal.show();
    });
</c:if>

// Delete product
function deleteProduct(productId) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
        window.location.href = '${pageContext.request.contextPath}/admin/products?action=delete&id=' + productId;
    }
}
</script>




