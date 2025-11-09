# 📸 Hướng dẫn Upload Ảnh Sản Phẩm cho Admin

## ✅ Đã hoàn thành

Hệ thống upload ảnh đã được tích hợp đầy đủ vào quyền admin. Admin có thể:

1. **Upload ảnh khi thêm sản phẩm mới**
2. **Upload ảnh mới khi sửa sản phẩm** (tự động xóa ảnh cũ)
3. **Xóa ảnh khi xóa sản phẩm** (tự động xóa file ảnh)

## 🎯 Tính năng

### 1. Tự động xử lý ảnh
- ✅ Tự động tạo tên file từ tên sản phẩm
- ✅ Thêm UUID để tránh trùng tên
- ✅ Validate định dạng ảnh (JPG, PNG, GIF, WEBP)
- ✅ Giới hạn kích thước file (tối đa 10MB)
- ✅ Tự động lưu vào thư mục `web/images/products/`
- ✅ Tự động cập nhật URL vào database

### 2. Preview ảnh
- ✅ Preview ảnh trước khi upload (trong form)
- ✅ Hiển thị ảnh hiện tại khi sửa sản phẩm

### 3. Quản lý file
- ✅ Tự động xóa ảnh cũ khi cập nhật
- ✅ Tự động xóa ảnh khi xóa sản phẩm

## 🚀 Cách sử dụng

### Bước 1: Đăng nhập với quyền Admin
Truy cập: `http://localhost:8080/WebEcommerce/admin`

### Bước 2: Vào trang Quản lý sản phẩm
Click vào menu "Quản lý sản phẩm" hoặc truy cập: `http://localhost:8080/WebEcommerce/admin/products`

### Bước 3: Thêm sản phẩm mới với ảnh

1. Click nút **"Thêm sản phẩm mới"**
2. Điền thông tin sản phẩm:
   - Tên sản phẩm (bắt buộc)
   - Danh mục (bắt buộc)
   - Mô tả
   - Giá (bắt buộc)
   - Giá khuyến mãi (tùy chọn)
   - Số lượng (bắt buộc)
   - **Chọn ảnh sản phẩm** (tùy chọn)
3. Xem preview ảnh (nếu đã chọn)
4. Click **"Thêm sản phẩm"**

**Kết quả:**
- Ảnh được tự động lưu vào `web/images/products/`
- Tên file được tạo tự động từ tên sản phẩm
- URL ảnh được tự động cập nhật vào database

### Bước 4: Sửa sản phẩm và thay đổi ảnh

1. Click nút **"Sửa"** ở sản phẩm cần sửa
2. Chỉnh sửa thông tin sản phẩm
3. **Chọn ảnh mới** (nếu muốn thay đổi)
   - Nếu không chọn ảnh mới, ảnh cũ sẽ được giữ nguyên
   - Nếu chọn ảnh mới, ảnh cũ sẽ tự động bị xóa
4. Click **"Cập nhật"**

**Kết quả:**
- Ảnh cũ được tự động xóa
- Ảnh mới được lưu vào `web/images/products/`
- URL ảnh mới được cập nhật vào database

### Bước 5: Xóa sản phẩm

1. Click nút **"Xóa"** ở sản phẩm cần xóa
2. Xác nhận xóa
3. **Kết quả:**
   - Ảnh sản phẩm được tự động xóa
   - Sản phẩm bị vô hiệu hóa trong database (soft delete)

## 📂 Cấu trúc file

```
WebEcommerce/
  ├── src/java/com/ecommerce/
  │   ├── controller/admin/
  │   │   ├── AdminProductServlet.java    ← Xử lý CRUD với upload
  │   │   └── AdminServlet.java
  │   └── util/
  │       ├── FileUploadUtil.java         ← Utility upload file
  │       └── ImagePathUtil.java          ← Utility xử lý đường dẫn ảnh
  └── web/
      ├── images/
      │   └── products/                    ← Thư mục lưu ảnh sản phẩm
      └── views/admin/
          └── manage-products.jsp          ← Form upload ảnh
```

## 🔧 Cấu hình

### Multipart Config
Đã được cấu hình trong `AdminProductServlet`:
```java
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,  // 1MB
    maxFileSize = 10 * 1024 * 1024,   // 10MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
```

### Định dạng ảnh được chấp nhận
- `.jpg` / `.jpeg`
- `.png`
- `.gif`
- `.webp`

### Kích thước tối đa
- **10MB** cho mỗi file

## 📝 Ví dụ tên file tự động

| Tên sản phẩm | Tên file được tạo |
|-------------|------------------|
| iPhone 15 Pro Max | `iphone-15-pro-max-abc12345.jpg` |
| Samsung Galaxy S24 | `samsung-galaxy-s24-def67890.png` |
| Laptop Dell XPS 13 | `laptop-dell-xps-13-ghi11111.jpg` |

**Lưu ý:** UUID được thêm vào để tránh trùng tên file.

## ⚠️ Lưu ý quan trọng

1. **Thư mục images/products phải tồn tại:**
   - Tự động tạo nếu chưa có
   - Nếu không tự tạo được, hãy tạo thủ công: `web/images/products/`

2. **Quyền ghi file:**
   - Đảm bảo server có quyền ghi vào thư mục `web/images/products/`
   - Trên Linux: `chmod 755 web/images/products/`

3. **Backup ảnh:**
   - Nên backup thư mục `web/images/products/` định kỳ
   - Khi deploy production, nên dùng thư mục bên ngoài webapp (xem `IMAGE_STORAGE_GUIDE.md`)

4. **Xóa ảnh cũ:**
   - Ảnh cũ chỉ được xóa khi:
     - Cập nhật sản phẩm với ảnh mới
     - Xóa sản phẩm
   - Nếu xóa sản phẩm bằng SQL trực tiếp, ảnh sẽ không tự động xóa

## 🐛 Xử lý lỗi

### Lỗi: "File size too large"
- **Nguyên nhân:** File ảnh > 10MB
- **Giải pháp:** Resize ảnh trước khi upload

### Lỗi: "Invalid file type"
- **Nguyên nhân:** File không phải là ảnh hoặc định dạng không được hỗ trợ
- **Giải pháp:** Chỉ upload file JPG, PNG, GIF, WEBP

### Lỗi: "Error uploading file"
- **Nguyên nhân:** 
  - Thư mục không tồn tại
  - Không có quyền ghi file
  - Đường dẫn không hợp lệ
- **Giải pháp:**
  - Kiểm tra thư mục `web/images/products/` có tồn tại không
  - Kiểm tra quyền ghi file
  - Xem log server để biết chi tiết lỗi

### Ảnh không hiển thị sau khi upload
- **Nguyên nhân:** 
  - Đường dẫn trong database không đúng
  - File không được lưu đúng vị trí
- **Giải pháp:**
  - Kiểm tra database: `SELECT image_url FROM products WHERE product_id = ?`
  - Kiểm tra file có tồn tại trong `web/images/products/` không
  - Restart server nếu cần

## ✅ Checklist

Trước khi deploy production:

- [ ] Thư mục `web/images/products/` đã được tạo
- [ ] Server có quyền ghi vào thư mục ảnh
- [ ] Đã test upload ảnh thành công
- [ ] Đã test sửa ảnh (xóa ảnh cũ)
- [ ] Đã test xóa sản phẩm (xóa ảnh)
- [ ] Đã backup thư mục ảnh
- [ ] Đã cấu hình backup tự động (nếu cần)

## 🎉 Hoàn thành!

Bây giờ admin có thể quản lý ảnh sản phẩm một cách dễ dàng và tự động!

---

**Lưu ý:** Nếu gặp vấn đề, hãy kiểm tra:
1. Console browser (F12) để xem lỗi JavaScript
2. Log server để xem lỗi backend
3. Database để kiểm tra `image_url` có đúng không


