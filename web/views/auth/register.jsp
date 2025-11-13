<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Đăng ký - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="register-container">
    <div class="container h-100">
        <div class="row justify-content-center align-items-center h-100">
            <div class="col-lg-5 col-md-7 col-sm-9">
                <div class="card shadow-lg register-card">
                    <div class="card-header bg-primary text-white text-center py-2">
                        <h5 class="mb-0"><i class="fas fa-user-plus me-2"></i>Đăng ký</h5>
                    </div>
                    <div class="card-body p-3">
                        <c:if test="${error != null}">
                            <div class="alert alert-danger alert-sm py-2 mb-2">${error}</div>
                        </c:if>
                        
                        <form method="post" action="${pageContext.request.contextPath}/register" id="registerForm">
                            <div class="row g-2">
                                <div class="col-md-6">
                                    <label for="username" class="form-label form-label-sm">Username <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control form-control-sm" id="username" name="username" 
                                           value="${username}" required>
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="email" class="form-label form-label-sm">Email <span class="text-danger">*</span></label>
                                    <input type="email" class="form-control form-control-sm" id="email" name="email" 
                                           value="${email}" required>
                                </div>
                            </div>
                            
                            <div class="row g-2 mt-1">
                                <div class="col-12">
                                    <label for="fullName" class="form-label form-label-sm">Họ và tên <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control form-control-sm" id="fullName" name="fullName" 
                                           value="${fullName}" required>
                                </div>
                            </div>
                            
                            <div class="row g-2 mt-1">
                                <div class="col-md-6">
                                    <label for="password" class="form-label form-label-sm">Password <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control form-control-sm" id="password" name="password" 
                                           required minlength="6">
                                    <small class="text-muted" style="font-size: 0.7rem;">Tối thiểu 6 ký tự</small>
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="confirmPassword" class="form-label form-label-sm">Xác nhận Password <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control form-control-sm" id="confirmPassword" name="confirmPassword" required>
                                </div>
                            </div>
                            
                            <div class="row g-2 mt-1">
                                <div class="col-md-6">
                                    <label for="phone" class="form-label form-label-sm">Số điện thoại <span class="text-danger">*</span></label>
                                    <input type="tel" class="form-control form-control-sm" id="phone" name="phone" 
                                           value="${phone}" required>
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="address" class="form-label form-label-sm">Địa chỉ <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control form-control-sm" id="address" name="address" 
                                           value="${address}" required>
                                </div>
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100 mt-3 py-2">
                                <i class="fas fa-user-plus me-2"></i>Đăng ký
                            </button>
                            
                            <div class="text-center mt-2">
                                <p class="mb-0" style="font-size: 0.9rem;">Đã có tài khoản? 
                                    <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay</a>
                                </p>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.getElementById('registerForm').addEventListener('submit', function(e) {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (password !== confirmPassword) {
        e.preventDefault();
        alert('Password và Confirm Password không khớp!');
        return false;
    }
});
</script>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- AOS Animation -->
<script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>

<!-- Context Path for JavaScript -->
<script>
    window.APP_CONTEXT = '${pageContext.request.contextPath}';
</script>

<!-- Custom JS -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</body>
</html>

















