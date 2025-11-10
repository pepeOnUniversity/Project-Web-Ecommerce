<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Hồ sơ của tôi - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0"><i class="fas fa-user-circle me-2"></i>Hồ sơ của tôi</h4>
                </div>
                <div class="card-body p-4">
                    <c:if test="${error != null}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <c:if test="${success != null}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <c:if test="${user != null}">
                        <form method="post" action="${pageContext.request.contextPath}/profile" id="profileForm">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="username" class="form-label">
                                        <i class="fas fa-user me-2"></i>Tên đăng nhập
                                    </label>
                                    <input type="text" class="form-control" id="username" 
                                           value="${user.username}" disabled>
                                    <div class="form-text">Tên đăng nhập không thể thay đổi.</div>
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="fullName" class="form-label">
                                        <i class="fas fa-id-card me-2"></i>Họ và tên <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="fullName" name="fullName" 
                                           value="${user.fullName}" required
                                           placeholder="Nhập họ và tên">
                                </div>
                            </div>
                            
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="email" class="form-label">
                                        <i class="fas fa-envelope me-2"></i>Email <span class="text-danger">*</span>
                                    </label>
                                    <input type="email" class="form-control" id="email" name="email" 
                                           value="${user.email}" required
                                           placeholder="Nhập email">
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="phone" class="form-label">
                                        <i class="fas fa-phone me-2"></i>Số điện thoại
                                    </label>
                                    <input type="tel" class="form-control" id="phone" name="phone" 
                                           value="${user.phone != null ? user.phone : ''}"
                                           placeholder="Nhập số điện thoại">
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="address" class="form-label">
                                    <i class="fas fa-map-marker-alt me-2"></i>Địa chỉ
                                </label>
                                <textarea class="form-control" id="address" name="address" rows="3"
                                          placeholder="Nhập địa chỉ">${user.address != null ? user.address : ''}</textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-calendar me-2"></i>Ngày tạo tài khoản
                                </label>
                                <input type="text" class="form-control" 
                                       value="<fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm" />" 
                                       disabled>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-shield-alt me-2"></i>Trạng thái email
                                </label>
                                <div>
                                    <c:choose>
                                        <c:when test="${user.emailVerified}">
                                            <span class="badge bg-success">
                                                <i class="fas fa-check-circle me-1"></i>Đã xác thực
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">
                                                <i class="fas fa-exclamation-triangle me-1"></i>Chưa xác thực
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Quay lại
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-2"></i>Cập nhật thông tin
                                </button>
                            </div>
                        </form>
                        
                        <!-- Form đổi mật khẩu -->
                        <hr class="my-4">
                        <h5 class="mb-3"><i class="fas fa-key me-2"></i>Đổi mật khẩu</h5>
                        <form method="post" action="${pageContext.request.contextPath}/profile" id="changePasswordForm">
                            <div class="mb-3">
                                <label for="currentPassword" class="form-label">
                                    <i class="fas fa-lock me-2"></i>Mật khẩu hiện tại
                                </label>
                                <input type="password" class="form-control" id="currentPassword" name="currentPassword" 
                                       required
                                       placeholder="Nhập mật khẩu hiện tại">
                            </div>
                            
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">
                                    <i class="fas fa-key me-2"></i>Mật khẩu mới
                                </label>
                                <input type="password" class="form-control" id="newPassword" name="newPassword" 
                                       required minlength="6"
                                       placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)">
                                <div class="form-text">Mật khẩu phải có ít nhất 6 ký tự.</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">
                                    <i class="fas fa-check-circle me-2"></i>Xác nhận mật khẩu mới
                                </label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                       required minlength="6"
                                       placeholder="Nhập lại mật khẩu mới">
                            </div>
                            
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <button type="submit" class="btn btn-warning">
                                    <i class="fas fa-save me-2"></i>Đổi mật khẩu
                                </button>
                            </div>
                        </form>
                    </c:if>
                    
                    <c:if test="${user == null}">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle me-2"></i>Không thể tải thông tin người dùng.
                        </div>
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại trang chủ
                        </a>
                    </c:if>
                </div>
            </div>
            
            <!-- Quick Actions -->
            <div class="card mt-3 shadow-sm">
                <div class="card-body">
                    <h6 class="card-title"><i class="fas fa-bolt me-2"></i>Thao tác nhanh:</h6>
                    <div class="d-grid gap-2 d-md-flex">
                        <a href="${pageContext.request.contextPath}/orders" class="btn btn-outline-primary btn-sm">
                            <i class="fas fa-list me-2"></i>Đơn hàng của tôi
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Validate form profile trước khi submit
document.getElementById('profileForm').addEventListener('submit', function(e) {
    const fullName = document.getElementById('fullName').value.trim();
    const email = document.getElementById('email').value.trim();
    
    if (!fullName) {
        e.preventDefault();
        alert('Vui lòng nhập họ và tên!');
        document.getElementById('fullName').focus();
        return false;
    }
    
    if (!email) {
        e.preventDefault();
        alert('Vui lòng nhập email!');
        document.getElementById('email').focus();
        return false;
    }
    
    // Validate email format
    const emailPattern = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!emailPattern.test(email)) {
        e.preventDefault();
        alert('Email không hợp lệ!');
        document.getElementById('email').focus();
        return false;
    }
    
    return true;
});

// Validate form đổi mật khẩu trước khi submit
document.getElementById('changePasswordForm').addEventListener('submit', function(e) {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (newPassword !== confirmPassword) {
        e.preventDefault();
        alert('Mật khẩu mới và xác nhận mật khẩu không khớp!');
        document.getElementById('confirmPassword').focus();
        return false;
    }
    
    if (newPassword.length < 6) {
        e.preventDefault();
        alert('Mật khẩu mới phải có ít nhất 6 ký tự!');
        document.getElementById('newPassword').focus();
        return false;
    }
    
    return true;
});
</script>

<jsp:include page="../common/footer.jsp"/>

