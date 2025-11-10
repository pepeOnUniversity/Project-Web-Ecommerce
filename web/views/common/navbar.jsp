<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    // Xử lý an toàn session và user
    com.ecommerce.model.User user = null;
    int cartCount = 0;
    
    try {
        HttpSession currentSession = request.getSession(false);
        if (currentSession != null) {
            user = (com.ecommerce.model.User) currentSession.getAttribute("user");
            
            // Đếm số lượng cart items (cần load từ CartDAO)
            if (user != null) {
                try {
                    com.ecommerce.dao.CartDAO cartDAO = new com.ecommerce.dao.CartDAO();
                    cartCount = cartDAO.getCartItemsByUserId(user.getUserId()).size();
                } catch (Exception e) {
                    // Nếu có lỗi, chỉ log và giữ cartCount = 0
                    System.err.println("Lỗi khi load cart count: " + e.getMessage());
                    cartCount = 0;
                }
            }
        }
    } catch (Exception e) {
        // Nếu có lỗi, chỉ log và giữ user = null
        System.err.println("Lỗi khi load user từ session: " + e.getMessage());
        user = null;
        cartCount = 0;
    }
    
    pageContext.setAttribute("currentUser", user);
    pageContext.setAttribute("cartCount", cartCount);
%>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">
            <i class="fas fa-shopping-cart me-2"></i>Ecommerce Store
        </a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/home">
                        <i class="fas fa-home me-1"></i>Trang chủ
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/products">
                        <i class="fas fa-box me-1"></i>Sản phẩm
                    </a>
                </li>
            </ul>
            
            <ul class="navbar-nav">
                <c:choose>
                    <c:when test="${currentUser != null}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/cart">
                                <i class="fas fa-shopping-cart me-1"></i>Giỏ hàng
                                <span class="badge bg-danger ms-1" id="cartBadge">${cartCount}</span>
                            </a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                                <i class="fas fa-user me-1"></i>${currentUser.fullName}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders">
                                    <i class="fas fa-list me-2"></i>Đơn hàng của tôi</a></li>
                                <c:if test="${currentUser.admin}">
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin">
                                        <i class="fas fa-cog me-2"></i>Admin Panel</a></li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/login">
                                <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/register">
                                <i class="fas fa-user-plus me-1"></i>Đăng ký
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>




