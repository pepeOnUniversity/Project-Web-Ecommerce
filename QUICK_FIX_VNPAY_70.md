# ⚡ Quick Fix: VNPay Error Code 70

## 🎯 Nguyên Nhân Chính

**Lỗi Code 70** xảy ra vì **VNPay Dashboard chưa được cấu hình Return URL**.

VNPay Sandbox **BẮT BUỘC** phải có Return URL được cấu hình trong dashboard, nếu không sẽ bị lỗi Code 70 ngay lập tức.

## ✅ Cách Khắc Phục (5 Phút)

### Bước 1: Lấy Ngrok URL

1. Chạy ngrok (nếu chưa chạy):
   ```powershell
   .\start-ngrok.ps1
   ```

2. Copy URL ngrok, ví dụ:
   ```
   https://d30df267a1d4.ngrok-free.app
   ```

### Bước 2: Cấu Hình VNPay Dashboard

1. **Đăng nhập VNPay Dashboard:**
   - URL: https://sandbox.vnpayment.vn/merchantv2/
   - Username: `contact.me.dothehung@gmail.com`
   - Password: `0586255568@Qa`

2. **Tìm mục cấu hình URL:**
   - Vào **"Thông tin website"** hoặc **"Cấu hình"** hoặc **"Cài đặt"**
   - Tìm phần **"Return URL"** hoặc **"URL trả về"**

3. **Nhập Return URL:**
   ```
   https://d30df267a1d4.ngrok-free.app/WebEcommerce/vnpay-return
   ```
   (Thay URL ngrok bằng URL thực tế của bạn)

4. **Nhập IPN URL** (nếu có):
   ```
   https://d30df267a1d4.ngrok-free.app/WebEcommerce/vnpay-ipn
   ```

5. **Lưu** cấu hình

### Bước 3: Restart và Test

1. **Restart Tomcat**
2. **Test lại thanh toán**

## 🔍 Kiểm Tra Nhanh

Truy cập Debug Servlet để kiểm tra:
```
http://localhost:9999/WebEcommerce/debug/vnpay
```

Servlet này sẽ hiển thị:
- ✅ TMN Code có đúng không
- ✅ Hash Secret có đúng không  
- ✅ Ngrok URL có được cấu hình không
- ✅ Payment URL được tạo ra như thế nào

## ⚠️ Lưu Ý Quan Trọng

1. **Ngrok URL thay đổi mỗi lần chạy mới:**
   - Mỗi lần chạy ngrok mới, URL sẽ thay đổi
   - **Phải cập nhật lại Return URL trong VNPay Dashboard**

2. **Hoặc dùng ngrok domain cố định:**
   - Đăng ký ngrok account (miễn phí)
   - Dùng domain cố định: `ngrok http 9999 --domain=your-domain.ngrok-free.app`
   - Chỉ cần cấu hình VNPay Dashboard một lần

## 📋 Checklist

- [ ] Ngrok đang chạy
- [ ] Đã copy ngrok URL
- [ ] Đã đăng nhập VNPay Dashboard
- [ ] Đã cấu hình Return URL trong VNPay Dashboard
- [ ] Return URL = `https://YOUR-NGROK-URL.ngrok-free.app/WebEcommerce/vnpay-return`
- [ ] Đã lưu cấu hình trong VNPay Dashboard
- [ ] Đã restart Tomcat
- [ ] Đã test lại thanh toán

## 🐛 Vẫn Bị Lỗi?

1. **Kiểm tra Debug Servlet:**
   - Mở: `http://localhost:9999/WebEcommerce/debug/vnpay`
   - Xem có lỗi gì không

2. **Kiểm tra Return URL:**
   - Return URL trong VNPay Dashboard phải **KHỚP CHÍNH XÁC** với ngrok URL hiện tại
   - Không có trailing slash
   - Phải là HTTPS

3. **Kiểm tra log Tomcat:**
   - Xem có lỗi gì trong console không
   - Xem Payment URL được tạo ra như thế nào

4. **Xem hướng dẫn chi tiết:**
   - File: `FIX_VNPAY_ERROR_70.md`

---

**99% trường hợp lỗi Code 70 là do chưa cấu hình Return URL trong VNPay Dashboard!**


