// Cart Management JavaScript

// Get context path
const contextPath = window.APP_CONTEXT || '';

// Add to cart function
function addToCart(productId, quantity = 1) {
    const params = new URLSearchParams();
    params.append('action', 'add');
    params.append('productId', productId);
    params.append('quantity', quantity);
    
    fetch(contextPath + '/cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: params
    })
    .then(response => {
        // Kiểm tra content type
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            // Nếu không phải JSON, đọc text để debug
            return response.text().then(text => {
                console.error('Expected JSON but got:', contentType);
                console.error('Response text:', text.substring(0, 500));
                throw new Error('Server returned non-JSON response');
            });
        }
        // Kiểm tra status code
        if (!response.ok) {
            // Nếu là lỗi, vẫn parse JSON để lấy message
            return response.json().then(data => {
                throw new Error(data.message || 'Có lỗi xảy ra');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showToast('success', data.message);
            updateCartBadge(data.cartCount || 0);
        } else {
            showToast('error', data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', error.message || 'Có lỗi xảy ra khi thêm vào giỏ hàng');
    });
}

// Update cart item quantity
function updateCartItem(cartItemId, quantity) {
    const params = new URLSearchParams();
    params.append('cartItemId', cartItemId);
    params.append('quantity', quantity);
    
    fetch(contextPath + '/cart', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: params
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Có lỗi xảy ra');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showToast('success', data.message);
            location.reload(); // Reload to update totals
        } else {
            showToast('error', data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', error.message || 'Có lỗi xảy ra khi cập nhật giỏ hàng');
    });
}

// Remove cart item
function removeCartItem(cartItemId) {
    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }
    
    const params = new URLSearchParams();
    params.append('cartItemId', cartItemId);
    
    fetch(contextPath + '/cart', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: params
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Có lỗi xảy ra');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            showToast('success', data.message);
            updateCartBadge(data.cartCount || 0);
            // Remove row from table
            document.querySelector(`tr[data-cart-item-id="${cartItemId}"]`)?.remove();
            // Update cart total after removing item
            if (typeof updateCartTotal === 'function') {
                updateCartTotal();
            }
            if (document.querySelectorAll('tbody tr').length === 0) {
                location.reload();
            }
        } else {
            showToast('error', data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', error.message || 'Có lỗi xảy ra khi xóa sản phẩm');
    });
}

// Update cart badge
function updateCartBadge(count) {
    const badge = document.getElementById('cartBadge');
    if (badge) {
        badge.textContent = count;
        if (count > 0) {
            badge.classList.remove('d-none');
        } else {
            badge.classList.add('d-none');
        }
    }
}

// Show toast notification
function showToast(type, message) {
    const toastContainer = document.querySelector('.toast-container') || createToastContainer();
    
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    
    toastContainer.appendChild(toast);
    
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
    
    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}

// Create toast container if not exists
function createToastContainer() {
    const container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    // Add to cart buttons
    document.querySelectorAll('.btn-add-cart').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.dataset.productId;
            const quantityInput = document.getElementById('quantity');
            const quantity = quantityInput ? parseInt(quantityInput.value) : 1;
            
            addToCart(productId, quantity);
        });
    });
    
    // Remove cart item buttons
    document.querySelectorAll('.btn-remove-cart').forEach(button => {
        button.addEventListener('click', function() {
            const cartItemId = this.dataset.cartItemId;
            removeCartItem(cartItemId);
        });
    });
});

