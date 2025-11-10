// Main JavaScript file

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Initialize AOS
    if (typeof AOS !== 'undefined') {
        AOS.init({
            duration: 800,
            once: true
        });
    }
    
    // Auto-hide alerts after 5 seconds (except alerts with 'alert-persistent' class)
    setTimeout(function() {
        document.querySelectorAll('.alert:not(.alert-persistent)').forEach(function(alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
    
    // Smooth scroll
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Make product cards clickable
    document.querySelectorAll('.clickable-product-card').forEach(card => {
        card.addEventListener('click', function(e) {
            // Don't navigate if clicking on a link or button inside the card
            if (e.target.closest('a') || e.target.closest('button')) {
                return;
            }
            const productUrl = this.getAttribute('data-product-url');
            if (productUrl) {
                window.location.href = productUrl;
            }
        });
    });
});

// Format currency helper
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Validate form
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return false;
    }
    
    return true;
}



