# 📝 Hướng Dẫn Điền Form Application Manager iNET.vn

## 📋 Thông Tin Cần Điền

### 1. **Application Name** ✅
**Điền gì:**
```
WebEcommerce
```
hoặc
```
Ecommerce Store
```

**Giải thích:**
- Đây là tên hiển thị của ứng dụng trong Application Manager
- Có thể đặt tên bất kỳ, nhưng nên dùng tên dễ nhớ
- Không ảnh hưởng đến URL hoặc đường dẫn

---

### 2. **Base Application URL** ✅
**Điền gì:**
```
ecommerce.com/WebEcommerce
```

**Giải thích:**
- Domain của bạn: `ecommerce.com`
- Context path của ứng dụng: `/WebEcommerce`
- **LƯU Ý:** Nếu bạn muốn ứng dụng chạy ở root (không có `/WebEcommerce`), có thể điền:
  ```
  ecommerce.com/
  ```
  hoặc
  ```
  ecommerce.com
  ```
- Sau khi deploy, bạn sẽ truy cập ứng dụng tại: `http://ecommerce.com/WebEcommerce/`

**Các lựa chọn:**
- **Option 1 (Khuyến nghị):** `ecommerce.com/WebEcommerce` - Ứng dụng chạy tại `/WebEcommerce`
- **Option 2:** `ecommerce.com/` - Ứng dụng chạy tại root (cần cấu hình thêm)

---

### 3. **Application Path** ✅
**Điền gì:**
```
/webapps
```
hoặc
```
/tomcat/webapps
```
hoặc
```
/home/your_username/webapps
```

**Giải thích:**
- Đây là đường dẫn đến thư mục chứa source code của ứng dụng
- **QUAN TRỌNG:** Đường dẫn này phải tương đối với **home directory** của bạn trên server
- Thường là một trong các đường dẫn sau:
  - `/webapps` - Nếu Tomcat được cài đặt ở `/opt/tomcat/webapps`
  - `/tomcat/webapps` - Nếu Tomcat được cài đặt ở `/home/username/tomcat/webapps`
  - `/home/username/webapps` - Nếu bạn tạo thư mục riêng

**Cách xác định đường dẫn chính xác:**
1. **Đăng nhập SSH** vào server iNET.vn
2. **Kiểm tra home directory:**
   ```bash
   pwd
   # Kết quả ví dụ: /home/username
   ```
3. **Tìm thư mục webapps:**
   ```bash
   find ~ -name "webapps" -type d
   # Hoặc
   ls -la ~/webapps
   # Hoặc
   ls -la ~/tomcat/webapps
   ```
4. **Xác định đường dẫn tương đối:**
   - Nếu home directory là `/home/username`
   - Và webapps ở `/home/username/webapps`
   - Thì Application Path là: `/webapps`
   - Nếu webapps ở `/home/username/tomcat/webapps`
   - Thì Application Path là: `/tomcat/webapps`

**LƯU Ý:**
- Đường dẫn phải bắt đầu bằng `/` (slash)
- Đường dẫn phải tương đối với home directory
- Nếu không chắc, liên hệ iNET.vn support để hỏi đường dẫn chính xác

---

### 4. **Deployment Environment** ✅
**Chọn:**
```
Production
```

**Giải thích:**
- **Production:** Môi trường thực tế, dùng cho người dùng cuối
- **Development:** Môi trường phát triển, dùng để test

**Khuyến nghị:**
- Nếu đây là ứng dụng thực tế cho người dùng → Chọn **Production**
- Nếu chỉ để test → Có thể chọn **Development**

---

## 📝 Tóm Tắt - Copy & Paste

### Form Điền Mẫu:

```
Application Name: WebEcommerce
Base Application URL: ecommerce.com/WebEcommerce
Application Path: /webapps
Deployment Environment: Production
```

---

## ⚠️ Lưu Ý Quan Trọng

### 1. **Application Path**
- **PHẢI** kiểm tra đường dẫn chính xác trên server trước khi điền
- Nếu điền sai, ứng dụng sẽ không deploy được
- Liên hệ iNET.vn support nếu không chắc

### 2. **Base Application URL**
- Nếu muốn ứng dụng chạy ở root (`ecommerce.com/`), cần:
  - Đổi tên WAR file thành `ROOT.war`
  - Hoặc cấu hình Tomcat để map root path
  - Hoặc điền `ecommerce.com/` và cấu hình thêm

### 3. **Sau Khi Điền Form**
1. **Upload WAR file:**
   - Upload file `dist/WebEcommerce.war` vào Application Path đã chỉ định
   - Hoặc upload qua Application Manager (nếu có chức năng này)

2. **Kiểm tra deploy:**
   - Truy cập: `http://ecommerce.com/WebEcommerce/`
   - Nếu thấy trang chủ → Deploy thành công! ✅

3. **Cấu hình database:**
   - Thêm environment variables trong Application Manager
   - Xem file `DEPLOY_SHARED_HOSTING_INET.md` để biết chi tiết

---

## 🆘 Cần Hỗ Trợ?

**Nếu không chắc về Application Path:**
1. **Liên hệ iNET.vn support:**
   - Email: support@inet.vn
   - Ticket: Đăng nhập client area
   - Hỏi: "Đường dẫn webapps trên server của tôi là gì?"

2. **Hoặc đăng nhập SSH và kiểm tra:**
   ```bash
   # Kiểm tra home directory
   pwd
   
   # Tìm thư mục webapps
   find ~ -name "webapps" -type d
   
   # Kiểm tra Tomcat
   which tomcat
   # Hoặc
   ps aux | grep tomcat
   ```

---

## 📚 Tài Liệu Tham Khảo

- `DEPLOY_SHARED_HOSTING_INET.md` - Hướng dẫn deploy chi tiết
- `QUICK_START_APPLICATION_MANAGER.md` - Quick start guide

---

**Chúc bạn deploy thành công! 🚀**


