<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<footer class="bg-dark text-light mt-5 py-4">
    <div class="container">
        <div class="row">
            <div class="col-md-4 mb-3">
                <h5><i class="fas fa-shopping-cart me-2"></i>Ecommerce Store</h5>
                <p class="text-muted">Chuyên cung cấp các sản phẩm công nghệ chất lượng cao với giá tốt nhất.</p>
            </div>
            <div class="col-md-4 mb-3">
                <h5>Liên hệ</h5>
                <p class="mb-1"><i class="fas fa-phone me-2"></i>Hotline: 0123 456 789</p>
                <p class="mb-1"><i class="fas fa-envelope me-2"></i>Email: support@ecommerce.com</p>
                <p class="mb-0"><i class="fas fa-map-marker-alt me-2"></i>123 Đường ABC, TP.HCM</p>
            </div>
            <div class="col-md-4 mb-3">
                <h5>Theo dõi chúng tôi</h5>
                <div class="social-links">
                    <a href="#" class="text-light me-3"><i class="fab fa-facebook fa-2x"></i></a>
                    <a href="#" class="text-light me-3"><i class="fab fa-twitter fa-2x"></i></a>
                    <a href="#" class="text-light me-3"><i class="fab fa-instagram fa-2x"></i></a>
                </div>
            </div>
        </div>
        <hr class="bg-light">
        <div class="text-center">
            <p class="mb-0">&copy; 2024 Ecommerce Store. All rights reserved.</p>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- AOS Animation -->
<script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>

<!-- Context Path for JavaScript -->
<script>
    window.APP_CONTEXT = '${pageContext.request.contextPath}';
</script>

<!-- Custom JS -->
<script src="${pageContext.request.contextPath}/assets/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</body>
</html>

