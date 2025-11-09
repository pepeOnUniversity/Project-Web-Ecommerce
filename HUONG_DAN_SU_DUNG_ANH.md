# 📸 Hướng dẫn sử dụng ảnh sản phẩm

## 🎯 Cách sử dụng nhanh (3 bước)

### Bước 1: Đặt ảnh vào thư mục
Copy ảnh của bạn vào thư mục:
```
web/images/products/
```

**Ví dụ:** Nếu bạn có ảnh tên `iphone15.jpg`, đặt vào:
```
web/images/products/iphone15.jpg
```

### Bước 2: Cập nhật Database
Cập nhật cột `image_url` trong bảng `products`:

```sql
UPDATE products 
SET image_url = '/images/products/iphone15.jpg' 
WHERE product_id = 1;
```

**Lưu ý quan trọng:**
- Đường dẫn phải bắt đầu bằng `/images/products/`
- Tên file phải khớp với tên file thực tế (phân biệt chữ hoa/thường)

### Bước 3: Xem kết quả
- Restart server (nếu đang chạy)
- Truy cập trang web
- Ảnh sẽ tự động hiển thị!

---

## 📋 Ví dụ đầy đủ

### Giả sử bạn có ảnh `samsung-s24.jpg`:

1. **Copy ảnh:**
   ```
   web/images/products/samsung-s24.jpg
   ```

2. **Cập nhật database:**
   ```sql
   -- Xem sản phẩm hiện tại
   SELECT product_id, product_name, image_url 
   FROM products;
   
   -- Cập nhật ảnh cho sản phẩm ID = 2
   UPDATE products 
   SET image_url = '/images/products/samsung-s24.jpg' 
   WHERE product_id = 2;
   ```

3. **Kiểm tra:**
   - Mở browser → trang sản phẩm
   - Ảnh sẽ hiển thị!

---

## 🔍 Kiểm tra nếu ảnh không hiển thị

### 1. Kiểm tra file có tồn tại không:
```
web/images/products/ten-file-anh.jpg
```
- File có đúng tên không?
- File có đúng thư mục không?

### 2. Kiểm tra database:
```sql
SELECT product_id, product_name, image_url 
FROM products 
WHERE product_id = 1;
```
- `image_url` có đúng format `/images/products/ten-file.jpg` không?

### 3. Kiểm tra browser:
- Mở F12 → Console
- Xem có lỗi 404 không?
- Thử truy cập trực tiếp: `http://localhost:8080/WebEcommerce/images/products/ten-file.jpg`

### 4. Restart server:
- Đôi khi cần restart để server nhận file mới

---

## 📝 Format đường dẫn trong Database

### ✅ ĐÚNG:
```
/images/products/iphone15.jpg
/images/products/samsung-s24.png
/images/products/laptop-dell.jpg
```

### ❌ SAI:
```
iphone15.jpg                    (thiếu /images/products/)
images/products/iphone15.jpg    (thiếu dấu / đầu)
/images/iphone15.jpg            (thiếu products/)
```

---

## 🎨 Tạo ảnh placeholder (tùy chọn)

Nếu muốn có ảnh mặc định khi ảnh không tìm thấy:

1. Tạo ảnh placeholder (kích thước nhỏ, ví dụ 200x200px)
2. Đặt tên `placeholder.jpg`
3. Copy vào: `web/images/placeholder.jpg`

Code đã tự động xử lý: nếu ảnh không tìm thấy, sẽ hiển thị placeholder.

---

## 🚀 Tự động tạo tên file (nâng cao)

Nếu bạn muốn tự động tạo tên file từ tên sản phẩm trong code Java:

```java
// Tạo tên file từ tên sản phẩm
String fileName = ImagePathUtil.generateImageFileName("iPhone 15 Pro Max", "jpg");
// Kết quả: "iphone-15-pro-max.jpg"

// Tạo đường dẫn đầy đủ
String imagePath = ImagePathUtil.generateImagePath("iPhone 15 Pro Max", "products", "jpg");
// Kết quả: "/images/products/iphone-15-pro-max.jpg"
```

---

## 📂 Cấu trúc thư mục

```
WebEcommerce/
  └── web/
      └── images/
          ├── products/          ← Đặt ảnh sản phẩm ở đây
          │   ├── iphone15.jpg
          │   ├── samsung-s24.png
          │   └── laptop-dell.jpg
          └── placeholder.jpg    ← Ảnh mặc định (tùy chọn)
```

---

## ✅ Checklist

Trước khi hỏi "Tại sao ảnh không hiển thị?", hãy kiểm tra:

- [ ] File ảnh đã được copy vào `web/images/products/`
- [ ] Tên file trong database khớp với tên file thực tế
- [ ] Đường dẫn trong database bắt đầu bằng `/images/products/`
- [ ] Đã restart server sau khi thêm file mới
- [ ] Đã kiểm tra browser console (F12) xem có lỗi không
- [ ] Đã thử truy cập trực tiếp URL ảnh

---

## 💡 Tips

1. **Đặt tên file:**
   - Dùng chữ thường: `iphone15.jpg` thay vì `iPhone15.JPG`
   - Dùng dấu gạch ngang: `samsung-s24.jpg` thay vì `samsung s24.jpg`
   - Tránh ký tự đặc biệt: không dùng `@`, `#`, `$`, `%`, etc.

2. **Tối ưu ảnh:**
   - Resize ảnh trước khi upload (không quá 2MB)
   - Dùng format JPG cho ảnh sản phẩm (kích thước nhỏ hơn PNG)

3. **Backup:**
   - Backup thư mục `web/images/` định kỳ
   - Khi deploy production, nên dùng thư mục bên ngoài (xem `IMAGE_STORAGE_GUIDE.md`)

---

**Chúc bạn thành công! 🎉**

Nếu vẫn gặp vấn đề, hãy kiểm tra file `IMAGE_STORAGE_GUIDE.md` để xem hướng dẫn chi tiết hơn.


