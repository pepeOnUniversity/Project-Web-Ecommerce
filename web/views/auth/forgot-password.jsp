<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Quên mật khẩu - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-lg">
                <div class="card-header bg-warning text-dark text-center">
                    <h4 class="mb-0"><i class="fas fa-key me-2"></i>Quên mật khẩu</h4>
                </div>
                <div class="card-body p-4">
                    <c:if test="${error != null}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                        </div>
                    </c:if>
                    
                    <c:if test="${success != null}">
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle me-2"></i>${success}
                        </div>
                    </c:if>
                    
                    <c:if test="${success == null}">
                        <p class="text-muted mb-4">
                            Nhập email đã đăng ký với tài khoản của bạn. 
                            Chúng tôi sẽ gửi link đặt lại mật khẩu đến email của bạn.
                        </p>
                        
                        <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       value="${email}" required autofocus 
                                       placeholder="Nhập email đã đăng ký">
                            </div>
                            
                            <button type="submit" class="btn btn-warning w-100 mb-3">
                                <i class="fas fa-paper-plane me-2"></i>Gửi link đặt lại mật khẩu
                            </button>
                        </form>
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

