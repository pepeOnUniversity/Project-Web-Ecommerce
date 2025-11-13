# 🎨 Hướng dẫn cấu hình Favicon

## ✅ Đã cấu hình

File `web/views/common/header.jsp` đã được cập nhật với các link favicon:
- `favicon.ico` (file chính)
- `favicon-32x32.png` (32x32 pixels)
- `favicon-16x16.png` (16x16 pixels)
- `apple-touch-icon.png` (180x180 pixels cho iOS)

## 📁 Vị trí file

Đặt các file favicon vào thư mục: **`web/images/`**

```
web/
└── images/
    ├── favicon.ico          ← File chính (bắt buộc)
    ├── favicon-32x32.png    ← Tùy chọn
    ├── favicon-16x16.png    ← Tùy chọn
    └── apple-touch-icon.png ← Tùy chọn (cho iOS)
```

## 🔧 Cách thêm favicon

### **Cách 1: Sử dụng file ảnh local**

1. **Chuẩn bị file ảnh:**
   - File ảnh logo của bạn (PNG, JPG, SVG)
   - Kích thước khuyến nghị: 512x512 hoặc 256x256 pixels

2. **Chuyển đổi sang favicon:**
   - Sử dụng công cụ online: https://favicon.io/ hoặc https://realfavicongenerator.net/
   - Upload ảnh của bạn
   - Download các file favicon đã tạo

3. **Đặt file vào thư mục:**
   - Copy file `favicon.ico` vào `web/images/favicon.ico`
   - (Tùy chọn) Copy các file PNG khác nếu có

4. **Restart Tomcat và test:**
   - Restart server
   - Mở trình duyệt và xem tab (có thể cần clear cache: Ctrl+F5)

### **Cách 2: Sử dụng link ảnh online**

Nếu bạn muốn dùng link ảnh online thay vì file local, sửa trong `header.jsp`:

```jsp
<!-- Thay đổi từ: -->
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/favicon.ico">

<!-- Thành: -->
<link rel="icon" type="image/x-icon" href="https://your-domain.com/path/to/your-logo.ico">
<!-- hoặc -->
<link rel="icon" type="image/png" href="https://your-domain.com/path/to/your-logo.png">
```

### **Cách 3: Sử dụng SVG (Modern browsers)**

Nếu bạn muốn dùng SVG (vector, sắc nét ở mọi kích thước):

```jsp
<!-- Thêm vào header.jsp -->
<link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg">
```

## 🛠️ Công cụ tạo Favicon

1. **Favicon.io** - https://favicon.io/
   - Tạo từ text, emoji, hoặc upload ảnh
   - Tự động tạo tất cả kích thước

2. **RealFaviconGenerator** - https://realfavicongenerator.net/
   - Upload ảnh, tự động tạo tất cả formats
   - Hỗ trợ nhiều platforms

3. **Favicon Generator** - https://www.favicon-generator.org/
   - Upload ảnh, download package

## 📝 Ví dụ cấu hình đơn giản (chỉ cần 1 file)

Nếu bạn chỉ muốn dùng 1 file favicon đơn giản, có thể sửa lại `header.jsp`:

```jsp
<!-- Favicon - Chỉ cần 1 file -->
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/favicon.ico">
```

Sau đó chỉ cần đặt file `favicon.ico` vào `web/images/` là xong!

## ⚠️ Lưu ý

1. **Clear cache:** Sau khi thay đổi favicon, có thể cần:
   - Hard refresh: `Ctrl + F5` (Windows) hoặc `Cmd + Shift + R` (Mac)
   - Clear browser cache
   - Restart Tomcat

2. **File size:** Favicon nên nhỏ (< 100KB) để load nhanh

3. **Format:** 
   - `.ico` - Tương thích tốt nhất
   - `.png` - Chất lượng tốt, hỗ trợ transparency
   - `.svg` - Vector, sắc nét (chỉ modern browsers)

4. **Kích thước:** 
   - Favicon.ico: 16x16, 32x32, 48x48 (multi-size)
   - PNG: 16x16, 32x32, 180x180 (Apple)

## 🎯 Quick Start

**Cách nhanh nhất:**

1. Lấy logo của bạn (file PNG/JPG)
2. Vào https://favicon.io/favicon-converter/
3. Upload ảnh → Download
4. Copy file `favicon.ico` vào `web/images/`
5. Restart Tomcat
6. Done! ✅

---

**File đã được cấu hình sẵn trong `header.jsp`, bạn chỉ cần thêm file favicon vào thư mục `web/images/` là xong!**



