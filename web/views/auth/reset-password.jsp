<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Đặt lại mật khẩu - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-lg">
                <div class="card-header bg-danger text-white text-center">
                    <h4 class="mb-0"><i class="fas fa-lock me-2"></i>Đặt lại mật khẩu</h4>
                </div>
                <div class="card-body p-4">
                    <c:if test="${error != null}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                        </div>
                    </c:if>
                    
                    <c:if test="${validToken == true}">
                        <p class="text-muted mb-4">
                            Vui lòng nhập mật khẩu mới cho tài khoản của bạn.
                        </p>
                        
                        <form method="post" action="${pageContext.request.contextPath}/reset-password">
                            <input type="hidden" name="token" value="${token}">
                            
                            <div class="mb-3">
                                <label for="password" class="form-label">Mật khẩu mới</label>
                                <input type="password" class="form-control" id="password" name="password" 
                                       required autofocus minlength="6"
                                       placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)">
                                <div class="form-text">Mật khẩu phải có ít nhất 6 ký tự.</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                       required minlength="6"
                                       placeholder="Nhập lại mật khẩu mới">
                            </div>
                            
                            <button type="submit" class="btn btn-danger w-100 mb-3">
                                <i class="fas fa-save me-2"></i>Đặt lại mật khẩu
                            </button>
                        </form>
                    </c:if>
                    
                    <c:if test="${validToken != true}">
                        <div class="text-center">
                            <p class="text-muted">Token không hợp lệ hoặc đã hết hạn.</p>
                            <a href="${pageContext.request.contextPath}/forgot-password" class="btn btn-primary">
                                <i class="fas fa-redo me-2"></i>Yêu cầu link mới
                            </a>
                        </div>
                    </c:if>
                    
                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại đăng nhập
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

