<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Đổi mật khẩu - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0"><i class="fas fa-key me-2"></i>Đổi mật khẩu</h4>
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
                    
                    <p class="text-muted mb-4">
                        Để bảo mật tài khoản của bạn, vui lòng nhập mật khẩu hiện tại và mật khẩu mới.
                    </p>
                    
                    <form method="post" action="${pageContext.request.contextPath}/change-password" id="changePasswordForm">
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">
                                <i class="fas fa-lock me-2"></i>Mật khẩu hiện tại
                            </label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" 
                                   required autofocus
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
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="fas fa-save me-2"></i>Đổi mật khẩu
                            </button>
                            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Quay lại trang chủ
                            </a>
                        </div>
                    </form>
                </div>
            </div>
            
            <!-- Security Tips -->
            <div class="card mt-3 shadow-sm">
                <div class="card-body">
                    <h6 class="card-title"><i class="fas fa-shield-alt me-2"></i>Mẹo bảo mật mật khẩu:</h6>
                    <ul class="card-text small text-muted mb-0">
                        <li>Sử dụng ít nhất 8 ký tự</li>
                        <li>Kết hợp chữ hoa, chữ thường, số và ký tự đặc biệt</li>
                        <li>Không sử dụng thông tin cá nhân trong mật khẩu</li>
                        <li>Đổi mật khẩu định kỳ để bảo mật tài khoản</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Validate form trước khi submit
document.getElementById('changePasswordForm').addEventListener('submit', function(e) {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (newPassword !== confirmPassword) {
        e.preventDefault();
        alert('Mật khẩu mới và xác nhận mật khẩu không khớp!');
        return false;
    }
    
    if (newPassword.length < 6) {
        e.preventDefault();
        alert('Mật khẩu mới phải có ít nhất 6 ký tự!');
        return false;
    }
    
    return true;
});
</script>

<jsp:include page="../common/footer.jsp"/>

