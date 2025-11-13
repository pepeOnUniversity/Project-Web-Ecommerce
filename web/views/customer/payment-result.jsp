<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Kết quả thanh toán - Ecommerce Store"/>
</jsp:include>

<jsp:include page="../common/navbar.jsp"/>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-body text-center py-5">
                    <c:choose>
                        <c:when test="${success == true}">
                            <div class="mb-4">
                                <i class="fas fa-check-circle text-success" style="font-size: 80px;"></i>
                            </div>
                            <h2 class="text-success mb-3">Thanh toán thành công!</h2>
                            <p class="lead">Cảm ơn bạn đã mua hàng tại cửa hàng của chúng tôi.</p>
                            
                            <div class="alert alert-info mt-4">
                                <h5><i class="fas fa-info-circle me-2"></i>Thông tin đơn hàng</h5>
                                <p class="mb-1"><strong>Mã đơn hàng:</strong> #${orderId}</p>
                                <c:if test="${transactionNo != null}">
                                    <p class="mb-0"><strong>Mã giao dịch:</strong> ${transactionNo}</p>
                                </c:if>
                            </div>
                            
                            <div class="mt-4">
                                <a href="${pageContext.request.contextPath}/orders?orderId=${orderId}" class="btn btn-primary me-2">
                                    <i class="fas fa-list me-2"></i>Xem chi tiết đơn hàng
                                </a>
                                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                                    <i class="fas fa-home me-2"></i>Về trang chủ
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="mb-4">
                                <i class="fas fa-times-circle text-danger" style="font-size: 80px;"></i>
                            </div>
                            <h2 class="text-danger mb-3">Thanh toán thất bại</h2>
                            
                            <c:if test="${error != null}">
                                <div class="alert alert-danger">
                                    <i class="fas fa-exclamation-triangle me-2"></i>${error}
                                </div>
                            </c:if>
                            
                            <c:if test="${orderId != null}">
                                <div class="alert alert-warning mt-3">
                                    <p class="mb-1"><strong>Mã đơn hàng:</strong> #${orderId}</p>
                                    <p class="mb-0">Đơn hàng của bạn đã được hủy. Vui lòng thử lại hoặc chọn phương thức thanh toán khác.</p>
                                </div>
                            </c:if>
                            
                            <div class="mt-4">
                                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary me-2">
                                    <i class="fas fa-redo me-2"></i>Thử lại thanh toán
                                </a>
                                <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary">
                                    <i class="fas fa-shopping-cart me-2"></i>Về giỏ hàng
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>



