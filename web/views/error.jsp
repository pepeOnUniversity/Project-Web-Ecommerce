<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Lỗi - Ecommerce Store"/>
</jsp:include>

<jsp:include page="common/navbar.jsp"/>

<div class="container my-5 text-center">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <i class="fas fa-exclamation-triangle fa-5x text-danger mb-4"></i>
            <h2>Oops! Có lỗi xảy ra</h2>
            <p class="text-muted">${error != null ? error : 'Đã xảy ra lỗi không xác định. Vui lòng thử lại sau.'}</p>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-3">
                <i class="fas fa-home me-2"></i>Về trang chủ
            </a>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp"/>


