# 🚀 Hướng dẫn nhanh: Hiển thị ảnh sản phẩm

## Bước 1: Đặt ảnh vào thư mục

1. Copy ảnh của bạn vào thư mục:
   ```
   web/images/products/
   ```

2. **Đặt tên file ảnh:**
   - Dùng tên đơn giản, không có ký tự đặc biệt
   - Ví dụ: `iphone15.jpg`, `samsung-s24.png`, `laptop-dell.jpg`
   - Nên dùng chữ thường và dấu gạch ngang

## Bước 2: Cập nhật Database

Trong database, cập nhật cột `image_url` của sản phẩm với đường dẫn:

```sql
UPDATE products 
SET image_url = '/images/products/ten-file-anh.jpg' 
WHERE product_id = 1;
```

**Ví dụ:**
- Nếu file ảnh là `iphone15.jpg` → URL: `/images/products/iphone15.jpg`
- Nếu file ảnh là `samsung-s24.png` → URL: `/images/products/samsung-s24.png`

## Bước 3: Kiểm tra

1. **Restart server** (nếu đang chạy)
2. **Truy cập trang web** và xem sản phẩm
3. **Ảnh sẽ tự động hiển thị!**

---

## 📝 Ví dụ cụ thể

### Giả sử bạn có ảnh tên `iphone15.jpg`:

1. **Copy ảnh vào:**
   ```
   web/images/products/iphone15.jpg
   ```

2. **Cập nhật database:**
   ```sql
   UPDATE products 
   SET image_url = '/images/products/iphone15.jpg' 
   WHERE product_name LIKE '%iPhone%';
   ```

3. **Xong!** Ảnh sẽ hiển thị trên web.

---

## ⚠️ Lưu ý

- **Đường dẫn trong DB phải bắt đầu bằng `/images/products/`**
- **Tên file trong DB phải khớp với tên file thực tế**
- **Hỗ trợ các format:** `.jpg`, `.jpeg`, `.png`, `.gif`, `.webp`
- **Nếu ảnh không hiển thị:** Kiểm tra console browser (F12) để xem lỗi

---

## 🔧 Nếu ảnh không hiển thị

1. **Kiểm tra đường dẫn file:**
   - File có tồn tại trong `web/images/products/` không?
   - Tên file có đúng không? (phân biệt chữ hoa/thường)

2. **Kiểm tra database:**
   ```sql
   SELECT product_id, product_name, image_url 
   FROM products 
   WHERE product_id = 1;
   ```
   - `image_url` phải là `/images/products/ten-file.jpg`

3. **Kiểm tra browser console:**
   - Mở F12 → Console
   - Xem có lỗi 404 không?

4. **Restart server:**
   - Đôi khi cần restart để server nhận file mới

---

## 🎯 Tự động tạo tên file từ tên sản phẩm

Nếu bạn muốn tự động tạo tên file từ tên sản phẩm, có thể dùng:

```java
String fileName = ImagePathUtil.generateImageFileName("iPhone 15 Pro Max", "jpg");
// Kết quả: "iphone-15-pro-max.jpg"

String imagePath = ImagePathUtil.generateImagePath("iPhone 15 Pro Max", "products", "jpg");
// Kết quả: "/images/products/iphone-15-pro-max.jpg"
```

---

## 📂 Cấu trúc thư mục

```
web/
  └── images/
      └── products/
          ├── iphone15.jpg
          ├── samsung-s24.png
          └── laptop-dell.jpg
```

---

## ✅ Checklist

- [ ] Đã copy ảnh vào `web/images/products/`
- [ ] Đã cập nhật `image_url` trong database
- [ ] Đường dẫn trong DB bắt đầu bằng `/images/products/`
- [ ] Tên file khớp với tên trong database
- [ ] Đã restart server (nếu cần)
- [ ] Đã kiểm tra trên browser

---

**Chúc bạn thành công! 🎉**

