# Hướng dẫn sử dụng Three.js trong Ecommerce Store

## Tổng quan

Three.js đã được tích hợp vào website để tạo các hiệu ứng 3D sống động và hấp dẫn hơn. Hiện tại có 3 loại effects chính:

1. **3D Hero Banner Effect** - Hiệu ứng 3D trên banner đầu trang
2. **Particle Background** - Hiệu ứng hạt nền (có thể thêm vào bất kỳ section nào)
3. **3D Product Card Hover** - Hiệu ứng 3D khi hover vào product card (tùy chọn)

## Các tính năng đã tích hợp

### 1. 3D Hero Banner Effect ✅

**Vị trí:** Trang chủ (`home.jsp`), slide đầu tiên của carousel

**Tính năng:**
- Các hình khối 3D (box, sphere, torus) bay lơ lửng
- Tương tác với chuột (các hình khối di chuyển theo chuột)
- Animation mượt mà và tự động
- Tự động resize khi thay đổi kích thước màn hình

**Cách sử dụng:**
- Effect tự động kích hoạt khi có element với id `hero-3d-container`
- Đã được tích hợp sẵn trong slide đầu tiên của hero carousel

### 2. Particle Background Effect

**Vị trí:** Có thể thêm vào bất kỳ section nào

**Tính năng:**
- 1000 hạt particles với màu sắc gradient
- Animation xoay và di chuyển tự động
- Hiệu suất tối ưu với WebGL

**Cách sử dụng:**
Thêm vào bất kỳ JSP file nào:

```html
<div id="particle-background"></div>
```

Effect sẽ tự động khởi tạo khi tìm thấy element này.

### 3. 3D Product Card Hover Effect (Tùy chọn)

**Vị trí:** Product cards trên trang home và products

**Tính năng:**
- Hiển thị hình khối 3D khi hover vào product card
- Tương tác với chuột (xoay theo chuột)
- Animation mượt mà

**Cách kích hoạt:**
Mở file `web/assets/js/three-effects.js` và bỏ comment phần code:

```javascript
// Uncomment to enable 3D effects on product cards
document.querySelectorAll('.product-card').forEach(card => {
    new ProductCard3D(card);
});
```

## Cấu trúc file

```
web/
├── assets/
│   ├── js/
│   │   └── three-effects.js    # File chứa tất cả Three.js effects
│   └── css/
│       └── style.css           # CSS cho Three.js effects
└── views/
    ├── common/
    │   ├── header.jsp          # Đã thêm Three.js CDN
    │   └── footer.jsp          # Đã thêm script three-effects.js
    └── customer/
        └── home.jsp            # Đã tích hợp 3D hero effect
```

## Tùy chỉnh

### Thay đổi màu sắc particles

Trong `three-effects.js`, tìm hàm `createParticles()` và thay đổi:

```javascript
const color1 = new THREE.Color(0x667eea); // Màu 1
const color2 = new THREE.Color(0x764ba2); // Màu 2
```

### Thay đổi số lượng particles

```javascript
const particleCount = 1000; // Thay đổi số này
```

### Thay đổi tốc độ animation

```javascript
this.particles.rotation.x += 0.0005; // Tăng/giảm để thay đổi tốc độ
this.particles.rotation.y += 0.001;
```

### Thay đổi hình khối trong Hero 3D

Trong hàm `createShapes()`, bạn có thể:
- Thêm/xóa shapes
- Thay đổi loại hình (box, sphere, torus, cylinder, etc.)
- Thay đổi màu sắc và vị trí

## Hiệu suất

- Three.js sử dụng WebGL để render, rất nhanh và mượt
- Effects tự động tối ưu cho mobile (giảm số lượng particles nếu cần)
- Canvas được cleanup khi không sử dụng

## Lưu ý

1. **CDN Three.js**: Đang sử dụng version r128 từ CDN. Nếu muốn update, thay đổi trong `header.jsp`

2. **Browser Support**: 
   - Chrome/Edge: ✅ Full support
   - Firefox: ✅ Full support
   - Safari: ✅ Full support
   - IE11: ❌ Không hỗ trợ

3. **Mobile Performance**: 
   - Effects tự động điều chỉnh cho mobile
   - Nếu cần tắt effects trên mobile, thêm check:
   ```javascript
   if (window.innerWidth > 768) {
       // Initialize effects
   }
   ```

## Mở rộng

### Thêm effect mới

1. Tạo class mới trong `three-effects.js`:

```javascript
class MyNewEffect {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        if (!this.container) return;
        this.init();
    }
    
    init() {
        // Setup scene, camera, renderer
        // Create 3D objects
        // Start animation
    }
    
    animate() {
        // Animation loop
    }
}
```

2. Khởi tạo trong `DOMContentLoaded`:

```javascript
const myEffect = new MyNewEffect('my-container-id');
```

### Thêm 3D Product Preview

Nếu có 3D models (GLTF/GLB), có thể thêm:

```javascript
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';

const loader = new GLTFLoader();
loader.load('path/to/model.glb', (gltf) => {
    scene.add(gltf.scene);
});
```

## Troubleshooting

**Effect không hiển thị:**
- Kiểm tra console có lỗi không
- Đảm bảo Three.js đã load (check `typeof THREE !== 'undefined'`)
- Kiểm tra element container có tồn tại không

**Performance chậm:**
- Giảm số lượng particles
- Giảm số lượng shapes trong hero effect
- Tắt effects trên mobile

**Canvas bị lỗi:**
- Kiểm tra WebGL support: `renderer.getContext('webgl')`
- Thử fallback: `renderer.getContext('webgl2')`

## Tài liệu tham khảo

- [Three.js Documentation](https://threejs.org/docs/)
- [Three.js Examples](https://threejs.org/examples/)
- [WebGL Fundamentals](https://webglfundamentals.org/)

