<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Xác minh Email - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-lg">
                <div class="card-header text-center">
                    <h4 class="mb-0">
                        <i class="fas fa-envelope me-2"></i>Xác minh Email
                    </h4>
                </div>
                <div class="card-body p-4">
                    <c:choose>
                        <c:when test="${not empty messageType and messageType == 'success'}">
                            <div class="alert alert-success alert-persistent" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                <strong>Thành công!</strong><br>
                                <c:out value="${message}" default="Email đã được xác minh thành công!"/>
                            </div>
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                                    <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập ngay
                                </a>
                            </div>
                        </c:when>
                        
                        <c:when test="${not empty messageType and messageType == 'error'}">
                            <div class="alert alert-danger alert-persistent" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>
                                <strong>Lỗi!</strong><br>
                                <c:out value="${message}" default="Có lỗi xảy ra khi xác minh email."/>
                            </div>
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">
                                    <i class="fas fa-user-plus me-2"></i>Đăng ký lại
                                </a>
                            </div>
                        </c:when>
                        
                        <c:when test="${not empty messageType and messageType == 'warning'}">
                            <div class="alert alert-warning alert-persistent" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                <strong>Cảnh báo!</strong><br>
                                <c:out value="${message}" default="Có cảnh báo về xác minh email."/>
                            </div>
                            <c:if test="${not empty email}">
                                <div class="mt-3">
                                    <p class="text-muted">Email của bạn: <strong><c:out value="${email}"/></strong></p>
                                    <p class="text-muted">Vui lòng liên hệ hỗ trợ để được hỗ trợ xác minh email.</p>
                                </div>
                            </c:if>
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                                    <i class="fas fa-sign-in-alt me-2"></i>Thử đăng nhập
                                </a>
                            </div>
                        </c:when>
                        
                        <c:otherwise>
                            <div class="alert alert-info alert-persistent" role="alert">
                                <i class="fas fa-info-circle me-2"></i>
                                <strong>Thông tin:</strong><br>
                                <c:choose>
                                    <c:when test="${not empty message}">
                                        <c:out value="${message}"/>
                                    </c:when>
                                    <c:otherwise>
                                        Vui lòng kiểm tra email để lấy link xác minh.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <c:if test="${not empty email}">
                                <div class="mt-3">
                                    <p class="text-muted">Email của bạn: <strong><c:out value="${email}"/></strong></p>
                                    <p class="text-muted">
                                        Chúng tôi đã gửi email xác minh đến địa chỉ email này. 
                                        Vui lòng kiểm tra hộp thư đến (và cả thư mục spam) và click vào link xác minh.
                                    </p>
                                </div>
                            </c:if>
                            <div class="text-center mt-4">
                                <p class="text-muted mb-3">Chưa nhận được email?</p>
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary">
                                    <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập để gửi lại email
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    
                    <hr class="my-4">
                    
                    <div class="text-center">
                        <p class="mb-0">
                            <a href="${pageContext.request.contextPath}/home">
                                <i class="fas fa-home me-2"></i>Về trang chủ
                            </a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>


