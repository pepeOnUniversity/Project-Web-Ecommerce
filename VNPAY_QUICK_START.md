# ⚡ Quick Start: Test Thanh Toán VNPay

## ✅ Đã Cấu Hình Xong

Thông tin VNPay đã được cấu hình vào hệ thống:
- ✅ Terminal ID: `OXPI7X5A`
- ✅ Hash Secret: `JHFEEOVQ8MLPL1054W0O0IUZDE8P2LIN`
- ✅ Đã cập nhật vào `config.properties`

## 🚀 3 Bước Để Test

### 1️⃣ Khởi Động Ngrok

```cmd
start-ngrok.bat
```

**Lưu ý:** Giữ cửa sổ PowerShell/CMD mở khi test!

### 2️⃣ Restart Tomcat

- Stop và Start lại Tomcat trong NetBeans
- Hoặc restart project

### 3️⃣ Test Thanh Toán

1. Đăng nhập → Thêm sản phẩm vào giỏ hàng
2. Vào **Checkout** → Chọn **"Thanh toán online qua VNPay"**
3. Click **"Thanh toán qua VNPay"**
4. Trên trang VNPay, nhập thông tin thẻ test:
   - **Số thẻ**: `9704198526191432198`
   - **Tên chủ thẻ**: `NGUYEN VAN A`
   - **Ngày hết hạn**: `03/07`
   - **CVV**: `123456`
   - **OTP**: `123456`
5. Click **"Thanh toán"**

## ✅ Kết Quả

- **Thành công**: Order status → `CONFIRMED`, Payment → `PAID`
- **Thất bại**: Order status → `CANCELLED`, Payment → `FAILED`

## 🔍 Kiểm Tra

- **Hệ thống**: Vào `/orders` xem lịch sử đơn hàng
- **VNPay Dashboard**: https://sandbox.vnpayment.vn/merchantv2/
  - Username: `contact.me.dothehung@gmail.com`
  - Password: `0586255568@Qa`
- **Ngrok Dashboard**: http://localhost:4040

## 🐛 Nếu Gặp Lỗi

1. **"Không thể tạo liên kết thanh toán"**
   - Kiểm tra `config.properties` có đúng TMN Code và Hash Secret không
   - Restart Tomcat

2. **"Xác thực thanh toán không thành công"**
   - Kiểm tra Hash Secret
   - Restart Tomcat

3. **Không nhận được callback**
   - Kiểm tra ngrok có đang chạy không
   - Kiểm tra URL trong `config.properties`
   - Restart Tomcat

## 📚 Xem Thêm

- Hướng dẫn chi tiết: `VNPAY_TEST_GUIDE.md`
- Hướng dẫn ngrok: `QUICK_START_NGROK.md`

---

**Bắt đầu test ngay! 🎉**



