<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Quản lý sản phẩm - Admin"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container-fluid my-4">
    <div class="row">
        <jsp:include page="admin-sidebar.jsp">
            <jsp:param name="currentPage" value="products"/>
        </jsp:include>
        
        <!-- Main Content -->
        <div class="col-md-9">
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
                <c:when test="${param.error == 'product_in_use'}">
                    <i class="fas fa-exclamation-circle me-2"></i>Không thể xóa sản phẩm này vì sản phẩm đang được sử dụng trong giỏ hàng hoặc đơn hàng!
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
        <div class="d-flex gap-2">
            <div class="form-check form-switch align-self-center">
                <input class="form-check-input" type="checkbox" id="showDeleted" ${param.showDeleted == 'true' ? 'checked' : ''} onchange="toggleDeletedProducts()">
                <label class="form-check-label" for="showDeleted">Hiển thị sản phẩm đã xóa</label>
            </div>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal" onclick="resetAddForm()">
            <i class="fas fa-plus me-2"></i>Thêm sản phẩm mới
        </button>
        </div>
    </div>
    
    <!-- Filter Section -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-light">
            <h5 class="mb-0">
                <i class="fas fa-filter me-2"></i>Bộ lọc sản phẩm
                <button class="btn btn-sm btn-outline-secondary float-end" onclick="clearFilters()">
                    <i class="fas fa-times me-1"></i>Xóa bộ lọc
                </button>
            </h5>
        </div>
        <div class="card-body">
            <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/admin/products">
                <div class="row g-3">
                    <!-- Search -->
                    <div class="col-md-3">
                        <label for="search" class="form-label">Tìm kiếm</label>
                        <input type="text" class="form-control" id="search" name="search" 
                               placeholder="Tên sản phẩm..." value="${filterSearch}">
                    </div>
                    
                    <!-- Category -->
                    <div class="col-md-2">
                        <label for="categoryId" class="form-label">Danh mục</label>
                        <select class="form-select" id="categoryId" name="categoryId">
                            <option value="">Tất cả</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}" ${filterCategoryId == category.categoryId ? 'selected' : ''}>
                                    ${category.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <!-- Status -->
                    <div class="col-md-2">
                        <label for="status" class="form-label">Trạng thái</label>
                        <select class="form-select" id="status" name="status">
                            <option value="">Tất cả</option>
                            <option value="active" ${filterStatus == 'active' ? 'selected' : ''}>Hoạt động</option>
                            <option value="inactive" ${filterStatus == 'inactive' ? 'selected' : ''}>Ngừng bán</option>
                        </select>
                    </div>
                    
                    <!-- Featured -->
                    <div class="col-md-2">
                        <label for="featured" class="form-label">Nổi bật</label>
                        <select class="form-select" id="featured" name="featured">
                            <option value="">Tất cả</option>
                            <option value="yes" ${filterFeatured == 'yes' ? 'selected' : ''}>Có</option>
                            <option value="no" ${filterFeatured == 'no' ? 'selected' : ''}>Không</option>
                        </select>
                    </div>
                    
                    <!-- Low Stock -->
                    <div class="col-md-3">
                        <label class="form-label">&nbsp;</label>
                        <div class="form-check mt-2">
                            <input class="form-check-input" type="checkbox" id="lowStock" name="lowStock" 
                                   value="true" ${filterLowStock == 'true' ? 'checked' : ''}>
                            <label class="form-check-label" for="lowStock">
                                Tồn kho thấp (< 10)
                            </label>
                        </div>
                    </div>
                </div>
                
                <div class="row g-3 mt-2">
                    <!-- Min Price -->
                    <div class="col-md-3">
                        <label for="minPrice" class="form-label">Giá tối thiểu (₫)</label>
                        <input type="number" class="form-control" id="minPrice" name="minPrice" 
                               placeholder="0" min="0" step="1000" value="${filterMinPrice}">
                    </div>
                    
                    <!-- Max Price -->
                    <div class="col-md-3">
                        <label for="maxPrice" class="form-label">Giá tối đa (₫)</label>
                        <input type="number" class="form-control" id="maxPrice" name="maxPrice" 
                               placeholder="Không giới hạn" min="0" step="1000" value="${filterMaxPrice}">
                    </div>
                    
                    <!-- Filter Button -->
                    <div class="col-md-6 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary me-2">
                            <i class="fas fa-search me-2"></i>Lọc
                        </button>
                        <c:if test="${param.showDeleted == 'true'}">
                            <input type="hidden" name="showDeleted" value="true">
                        </c:if>
                    </div>
                </div>
            </form>
        </div>
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
                            <c:if test="${param.showDeleted == 'true' || product.active}">
                            <tr class="${!product.active ? 'table-secondary' : ''}">
                                <td>
                                    <c:set var="imageUrl" value="${product.displayImageUrl}"/>
                                    <c:if test="${not empty product.imageUrl && !product.imageUrl.contains('placeholder')}">
                                        <c:choose>
                                            <c:when test="${param.t != null && param.t != ''}">
                                                <c:set var="imageUrl" value="${product.displayImageUrl}?v=${product.productId}&t=${param.t}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="imageUrl" value="${product.displayImageUrl}?v=${product.productId}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    <img src="${imageUrl}" alt="${product.productName}" 
                                         class="img-thumbnail product-image" 
                                         data-product-id="${product.productId}"
                                         style="width: 60px; height: 60px; object-fit: cover;"
                                         onerror="this.onerror=null; this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'60\' height=\'60\'%3E%3Crect fill=\'%23ddd\' width=\'60\' height=\'60\'/%3E%3Ctext fill=\'%23999\' font-family=\'sans-serif\' font-size=\'10\' x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\'%3ENo Image%3C/text%3E%3C/svg%3E'">
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
                                    <div class="btn-group" role="group">
                                        <button class="btn btn-sm btn-warning" onclick="editProduct(${product.productId})" title="Sửa thông tin sản phẩm">
                                        <i class="fas fa-edit"></i> Sửa
                                    </button>
                                        <button class="btn btn-sm btn-danger" onclick="deleteProduct(${product.productId})" title="Xóa sản phẩm">
                                        <i class="fas fa-trash"></i> Xóa
                                    </button>
                                    </div>
                                </td>
                            </tr>
                            </c:if>
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
            <form id="addProductForm" action="${pageContext.request.contextPath}/admin/products" method="POST">
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
                        <label for="imageUrl" class="form-label">Link ảnh sản phẩm</label>
                        <input type="url" class="form-control" id="imageUrl" name="imageUrl" placeholder="https://example.com/image.jpg">
                        <small class="form-text text-muted">Nhập link ảnh từ internet (URL)</small>
                        <div id="imagePreview" class="mt-2" style="display: none;">
                            <img id="previewImg" src="" alt="Preview" style="max-width: 200px; max-height: 200px; object-fit: contain;" onerror="this.style.display='none';">
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
            <form id="editProductForm" action="${pageContext.request.contextPath}/admin/products" method="POST">
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
                        <label for="editImageUrl" class="form-label">Link ảnh sản phẩm</label>
                        <input type="url" class="form-control" id="editImageUrl" name="imageUrl" placeholder="https://example.com/image.jpg">
                        <small class="form-text text-muted">Nhập link ảnh mới từ internet (URL). Để trống nếu không muốn thay đổi.</small>
                        <div id="editImagePreview" class="mt-2">
                            <div class="mb-2">
                                <strong>Ảnh hiện tại:</strong>
                            </div>
                            <img id="editPreviewImg" src="" alt="Current image" 
                                 style="max-width: 200px; max-height: 200px; object-fit: contain; border: 2px solid #dee2e6; border-radius: 4px;"
                                 onerror="this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'200\' height=\'200\'%3E%3Crect fill=\'%23ddd\' width=\'200\' height=\'200\'/%3E%3Ctext fill=\'%23999\' font-family=\'sans-serif\' font-size=\'14\' x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\'%3ENo Image%3C/text%3E%3C/svg%3E'">
                            <div class="mt-2 text-muted small" id="editCurrentImageInfo"></div>
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
// Preview ảnh khi nhập URL (Add form)
document.getElementById('imageUrl').addEventListener('input', function(e) {
    const imageUrl = e.target.value.trim();
    if (imageUrl && isValidUrl(imageUrl)) {
        document.getElementById('previewImg').src = imageUrl;
        document.getElementById('imagePreview').style.display = 'block';
    } else {
        document.getElementById('imagePreview').style.display = 'none';
    }
});

// Preview ảnh khi nhập URL (Edit form)
document.getElementById('editImageUrl').addEventListener('input', function(e) {
    const imageUrl = e.target.value.trim();
    if (imageUrl && isValidUrl(imageUrl)) {
        document.getElementById('editPreviewImg').src = imageUrl;
    }
});

// Helper function để validate URL
function isValidUrl(string) {
    try {
        const url = new URL(string);
        return url.protocol === 'http:' || url.protocol === 'https:';
    } catch (_) {
        return false;
    }
}

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
        const currentImageUrl = '<c:out value="${product.displayImageUrl}" escapeXml="true"/>';
        document.getElementById('editPreviewImg').src = currentImageUrl;
        
        // Hiển thị thông tin ảnh hiện tại
        const imageInfo = document.getElementById('editCurrentImageInfo');
        if (currentImageUrl && !currentImageUrl.includes('placeholder') && !currentImageUrl.includes('No Image')) {
            imageInfo.textContent = 'Ảnh hiện tại: ' + currentImageUrl;
        } else {
            imageInfo.textContent = 'Sản phẩm chưa có ảnh';
            imageInfo.className = 'mt-2 text-warning small';
        }
        
        // Show modal
        const editModal = new bootstrap.Modal(document.getElementById('editProductModal'));
        editModal.show();
    });
</c:if>

// Delete product
function deleteProduct(productId) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này? Sản phẩm sẽ bị ẩn khỏi danh sách nhưng vẫn có thể khôi phục.')) {
        window.location.href = '${pageContext.request.contextPath}/admin/products?action=delete&id=' + productId;
    }
}

// Toggle hiển thị sản phẩm đã xóa
function toggleDeletedProducts() {
    const showDeleted = document.getElementById('showDeleted').checked;
    const url = new URL(window.location.href);
    if (showDeleted) {
        url.searchParams.set('showDeleted', 'true');
    } else {
        url.searchParams.delete('showDeleted');
    }
    window.location.href = url.toString();
}

// Xóa tất cả filters
function clearFilters() {
    const url = new URL(window.location.href);
    url.searchParams.delete('categoryId');
    url.searchParams.delete('status');
    url.searchParams.delete('featured');
    url.searchParams.delete('search');
    url.searchParams.delete('minPrice');
    url.searchParams.delete('maxPrice');
    url.searchParams.delete('lowStock');
    // Giữ lại showDeleted nếu đang bật
    if (document.getElementById('showDeleted').checked) {
        url.searchParams.set('showDeleted', 'true');
    }
    window.location.href = url.toString();
}

</script>




