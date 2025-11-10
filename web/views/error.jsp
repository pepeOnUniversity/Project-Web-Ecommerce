<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Lỗi - Ecommerce Store"/>
</jsp:include>

<jsp:include page="common/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-lg">
                <div class="card-header bg-danger text-white text-center">
                    <h4 class="mb-0">
                        <i class="fas fa-exclamation-triangle me-2"></i>Oops! Có lỗi xảy ra
                    </h4>
                </div>
                <div class="card-body">
                    <div class="text-center mb-4">
                        <i class="fas fa-exclamation-triangle fa-5x text-danger"></i>
                    </div>
                    
                    <div class="alert alert-danger">
                        <strong>Lỗi:</strong><br>
                        ${error != null ? error : (exception != null ? exception.message : 'Đã xảy ra lỗi không xác định. Vui lòng thử lại sau.')}
                    </div>
                    
                    <c:if test="${exception != null}">
                        <div class="mt-3">
                            <details>
                                <summary class="text-muted cursor-pointer">Chi tiết lỗi (Development only)</summary>
                                <pre class="bg-light p-3 mt-2" style="font-size: 12px; overflow-x: auto;"><c:out value="${exception}"/><c:forEach var="trace" items="${exception.stackTrace}">
<c:out value="${trace}"/></c:forEach></pre>
                            </details>
                        </div>
                    </c:if>
                    
                    <div class="text-center mt-4">
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                            <i class="fas fa-home me-2"></i>Về trang chủ
                        </a>
                        <a href="javascript:history.back()" class="btn btn-outline-secondary ms-2">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp"/>



