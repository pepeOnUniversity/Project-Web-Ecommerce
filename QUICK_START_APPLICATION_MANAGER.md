# 🚀 Quick Start: Deploy qua Application Manager iNET.vn

## ✅ Đã Xác Nhận: Có Application Manager!

**IP Hosting**: `103.57.220.209`  
**Control Panel**: cPanel/Plesk với Application Manager  
**Hỗ trợ**: Java/Tomcat ✅

---

## 📋 Checklist Nhanh

### Bước 1: Build WAR File ⏱️ 5 phút

```bash
# Chạy script build
build-war.bat

# Hoặc build từ NetBeans:
# Click chuột phải project → Clean and Build
```

**Kết quả**: File `dist/WebEcommerce.war` được tạo

---

### Bước 2: Deploy WAR File ⏱️ 10 phút

1. **Đăng nhập cPanel/Plesk:**
   - URL: `http://103.57.220.209:2083` (cPanel) hoặc `https://103.57.220.209:8443` (Plesk)

2. **Vào Application Manager:**
   - Tìm mục **Application Manager** hoặc **Java Applications**
   - Click vào

3. **Deploy WAR:**
   - Click **Deploy Application** hoặc **Upload WAR**
   - Chọn file `dist/WebEcommerce.war`
   - Click **Deploy**
   - Đợi vài phút để deploy xong

4. **Kiểm tra:**
   - Truy cập: `http://103.57.220.209:8080/WebEcommerce/`
   - Nếu thấy trang chủ → Thành công! ✅

---

### Bước 3: Tạo Database ⏱️ 10 phút

1. **Tạo database:**
   - Vào **MySQL Databases** hoặc **SQL Server** trong cPanel/Plesk
   - Tạo database mới: `ecommerce_db`
   - Tạo user và cấp quyền **ALL PRIVILEGES**
   - **Lưu lại**: database name, username, password

2. **Import schema:**
   - Vào **phpMyAdmin** (MySQL) hoặc công cụ SQL Server
   - Chọn database vừa tạo
   - Click **Import** → Chọn file `schema.sql` → **Go**

---

### Bước 4: Cấu Hình Environment Variables ⏱️ 5 phút

1. **Vào Application Manager:**
   - Tìm ứng dụng `WebEcommerce` đã deploy
   - Click vào ứng dụng → Tìm **Environment Variables** hoặc **Configuration**

2. **Thêm các biến sau:**

```bash
# Database (thay thông tin thực tế)
db.url=jdbc:sqlserver://localhost:1433;databaseName=ecommerce_db;encrypt=false;trustServerCertificate=true;
db.user=your_db_user
db.password=your_db_password

# Email SMTP (thay thông tin thực tế)
smtp.host=smtp.gmail.com
smtp.port=587
smtp.user=your_email@gmail.com
smtp.password=your_app_password
email.from=your_email@gmail.com
email.from.name=Ecommerce Store
```

**Lưu ý:**
- Thay `your_db_user`, `your_db_password` bằng thông tin database thực tế
- Thay `your_email@gmail.com` và `your_app_password` bằng thông tin email thực tế
- Với Gmail: Tạo App Password tại https://myaccount.google.com/apppasswords

3. **Restart ứng dụng** (nếu cần)

---

### Bước 5: Upload Images ⏱️ 5 phút

1. **Vào File Manager:**
   - Tìm thư mục: `/webapps/WebEcommerce/` hoặc `/tomcat/webapps/WebEcommerce/`

2. **Upload thư mục images:**
   - Upload thư mục `web/images/` từ project
   - Đảm bảo cấu trúc: `/webapps/WebEcommerce/images/`

3. **Kiểm tra quyền:**
   - Quyền đọc: `755` cho thư mục, `644` cho files
   - Quyền ghi: `775` cho thư mục, `664` cho files (nếu cần upload)

---

### Bước 6: Test ⏱️ 5 phút

1. **Truy cập ứng dụng:**
   - `http://103.57.220.209:8080/WebEcommerce/`
   - Hoặc: `http://your-domain.com/WebEcommerce/`

2. **Test các chức năng:**
   - ✅ Đăng ký tài khoản mới
   - ✅ Đăng nhập
   - ✅ Xem danh sách sản phẩm
   - ✅ Xem chi tiết sản phẩm
   - ✅ Thêm sản phẩm vào giỏ hàng
   - ✅ Đặt hàng
   - ✅ Xem lịch sử đơn hàng

---

## ⚠️ Troubleshooting Nhanh

### Lỗi: Ứng dụng không truy cập được

**Giải pháp:**
- Đợi thêm vài phút để Tomcat deploy xong
- Kiểm tra URL: `http://103.57.220.209:8080/WebEcommerce/`
- Kiểm tra logs trong Application Manager

### Lỗi: Database connection failed

**Giải pháp:**
- Kiểm tra lại thông tin database trong environment variables
- Đảm bảo database đã được tạo và import schema
- Kiểm tra username/password có đúng không

### Lỗi: Images không load được

**Giải pháp:**
- Kiểm tra thư mục `images/` đã được upload chưa
- Kiểm tra quyền truy cập file (chmod 755)
- Kiểm tra đường dẫn trong code

---

## 📞 Cần Hỗ Trợ?

**Liên hệ iNET.vn:**
- Website: https://inet.vn
- Email: support@inet.vn
- Ticket: Đăng nhập client area

**Thông tin cần cung cấp:**
- IP hosting: `103.57.220.209`
- Loại hosting: Shared Hosting
- Vấn đề: (mô tả chi tiết)

---

## 📚 Tài Liệu Chi Tiết

Xem file `DEPLOY_SHARED_HOSTING_INET.md` để biết hướng dẫn chi tiết hơn.

---

**Chúc bạn deploy thành công! 🚀**


