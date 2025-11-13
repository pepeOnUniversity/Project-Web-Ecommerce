# ✅ Triển Khai Thanh Toán VNPay - Hoàn Tất

## 📋 Tổng Quan

Đã triển khai đầy đủ chức năng thanh toán qua VNPay cho hệ thống e-commerce. Người dùng có thể chọn thanh toán khi nhận hàng (COD) hoặc thanh toán online qua VNPay.

## ✅ Các File Đã Tạo/Cập Nhật

### 1. **VNPayUtil.java** ✅
- **Vị trí:** `src/java/com/ecommerce/util/VNPayUtil.java`
- **Chức năng:**
  - Tạo payment URL từ VNPay
  - Verify signature từ callback VNPay
  - Tính HMAC SHA512
  - Xử lý IP address và datetime

### 2. **PaymentServlet.java** ✅
- **Vị trí:** `src/java/com/ecommerce/controller/PaymentServlet.java`
- **URL:** `/payment/vnpay`
- **Chức năng:**
  - Nhận orderId từ request
  - Kiểm tra quyền truy cập
  - Tạo payment URL và redirect đến VNPay

### 3. **VNPayCallbackServlet.java** ✅
- **Vị trí:** `src/java/com/ecommerce/controller/VNPayCallbackServlet.java`
- **URLs:** `/vnpay-return` và `/vnpay-ipn`
- **Chức năng:**
  - Xử lý callback từ VNPay (Return URL và IPN)
  - Verify signature
  - Cập nhật order status và payment status
  - Xóa cart sau khi thanh toán thành công
  - Redirect về payment result page

### 4. **DebugVNPayServlet.java** ✅
- **Vị trí:** `src/java/com/ecommerce/controller/DebugVNPayServlet.java`
- **URL:** `/debug/vnpay`
- **Chức năng:**
  - Hiển thị thông tin cấu hình VNPay
  - Test tạo payment URL
  - Hỗ trợ debug

### 5. **VNPayConfig.java** ✅
- **Vị trí:** `src/java/com/ecommerce/config/VNPayConfig.java`
- **Đã cấu hình:**
  - TMN Code: `OXPI7X5A`
  - Hash Secret: Đọc từ `config.properties`
  - Payment URL: `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html`
  - Return URL và IPN URL: Tự động detect từ ngrok hoặc request

### 6. **config.properties** ✅
- **Vị trí:** `src/java/config.properties` và `web/WEB-INF/classes/config.properties`
- **Đã cấu hình:**
  ```properties
  vnpay.tmn.code=OXPI7X5A
  vnpay.hash.secret=Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7
  ngrok.url=https://3e409176bf50.ngrok-free.app
  ```

### 7. **OrderServlet.java** ✅
- **Đã có sẵn:** Xử lý redirect đến `/payment/vnpay` khi chọn VNPay

### 8. **checkout.jsp** ✅
- **Đã có sẵn:** Có option chọn phương thức thanh toán VNPay

### 9. **payment-result.jsp** ✅
- **Đã có sẵn:** Hiển thị kết quả thanh toán

### 10. **OrderDAO.java** ✅
- **Đã có sẵn:** Có method `updateOrderPaymentStatus()` để cập nhật payment status

## 🔄 Flow Thanh Toán

1. **User thêm sản phẩm vào cart** → Vào checkout
2. **Chọn phương thức thanh toán VNPay** → Submit form
3. **OrderServlet** → Tạo order với:
   - `status = PENDING`
   - `payment_method = VNPAY`
   - `payment_status = PENDING`
4. **Redirect đến PaymentServlet** (`/payment/vnpay?orderId=xxx`)
5. **PaymentServlet** → Tạo payment URL và redirect đến VNPay
6. **User thanh toán trên VNPay** → Sử dụng thẻ test
7. **VNPay gửi callback** → VNPayCallbackServlet nhận callback:
   - **Return URL** (`/vnpay-return`): User được redirect về
   - **IPN URL** (`/vnpay-ipn`): VNPay gửi callback để xác nhận
8. **VNPayCallbackServlet xử lý:**
   - Verify signature
   - Kiểm tra số tiền
   - Nếu thành công:
     - `status = CONFIRMED`
     - `payment_status = PAID`
     - `vnp_transaction_id = xxx`
     - Xóa cart
   - Nếu thất bại:
     - `status = CANCELLED`
     - `payment_status = FAILED`
9. **Redirect về payment-result.jsp** → Hiển thị kết quả

## 📝 Thông Tin Cấu Hình

### VNPay Sandbox
- **Terminal ID:** `OXPI7X5A`
- **Hash Secret:** `Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7`
- **Payment URL:** `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html`

### VNPay Dashboard
- **URL:** https://sandbox.vnpayment.vn/merchantv2/
- **Username:** `contact.me.dothehung@gmail.com`
- **Password:** `0586255568@Qa`

### Thẻ Test
- **Ngân hàng:** NCB
- **Số thẻ:** `9704198526191432198`
- **Tên chủ thẻ:** `NGUYEN VAN A`
- **Ngày phát hành:** `07/15`
- **OTP:** `123456`

## 🚀 Các Bước Tiếp Theo

### 1. Rebuild Project
```bash
# Clean và build lại project
mvn clean compile
# Hoặc trong IDE: Right-click project → Clean → Build
```

### 2. Restart Tomcat
**QUAN TRỌNG:** Sau khi build, phải restart Tomcat để:
- Load các servlet mới
- Load config.properties
- Áp dụng thay đổi code

### 3. Kiểm Tra Debug Servlet
1. Truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`
2. Kiểm tra:
   - ✅ TMN Code: `OXPI7X5A`
   - ✅ Hash Secret: `Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7` (32 ký tự)
   - ✅ Return URL: Có ngrok URL hoặc localhost
   - ✅ Payment URL: Có thể tạo được

### 4. Cấu Hình Return URL trong VNPay Dashboard
**QUAN TRỌNG NHẤT:** VNPay Sandbox yêu cầu cấu hình Return URL trong dashboard.

1. **Đăng nhập VNPay Dashboard:**
   - URL: https://sandbox.vnpayment.vn/merchantv2/
   - Username: `contact.me.dothehung@gmail.com`
   - Password: `0586255568@Qa`

2. **Tìm phần cấu hình Return URL:**
   - Vào "Thông tin website" hoặc "Cấu hình"
   - Tìm "Return URL" hoặc "URL trả về"

3. **Nhập Return URL:**
   ```
   https://YOUR-NGROK-URL.ngrok-free.app/WebEcommerce/vnpay-return
   ```
   ⚠️ **Lưu ý:** 
   - Thay `YOUR-NGROK-URL` bằng ngrok URL thực tế của bạn
   - URL này phải **KHỚP CHÍNH XÁC** với ngrok URL hiện tại
   - Nếu ngrok URL thay đổi, phải cập nhật lại trong VNPay Dashboard

4. **Lưu cấu hình**
   - Đợi 1-2 phút để VNPay cập nhật

### 5. Test Thanh Toán
1. Thêm sản phẩm vào giỏ hàng
2. Vào trang checkout
3. Chọn "Thanh toán online qua VNPay"
4. Điền thông tin giao hàng
5. Click "Thanh toán qua VNPay"
6. Sẽ được redirect đến trang VNPay
7. Sử dụng thông tin thẻ test:
   - Ngân hàng: NCB
   - Số thẻ: 9704198526191432198
   - Tên: NGUYEN VAN A
   - Ngày phát hành: 07/15
   - OTP: 123456
8. Sau khi thanh toán, sẽ được redirect về trang kết quả

## ⚠️ Lưu Ý Quan Trọng

### 1. Hash Data Phải Là Giá Trị RAW
- ❌ **SAI:** `vnp_ReturnUrl=https%3A%2F%2F...` (đã encode)
- ✅ **ĐÚNG:** `vnp_ReturnUrl=https://...` (RAW, không encode)

### 2. Query String Phải Encode
- Query string trong URL phải encode: `vnp_ReturnUrl=https%3A%2F%2F...`
- Nhưng hash data phải là RAW: `vnp_ReturnUrl=https://...`

### 3. Return URL Phải Khớp
- Return URL trong VNPay Dashboard phải **KHỚP CHÍNH XÁC** với ngrok URL hiện tại
- Nếu ngrok URL thay đổi, phải cập nhật lại trong VNPay Dashboard

### 4. Ngrok URL
- Nếu dùng localhost, cần chạy ngrok để expose localhost
- Cập nhật `ngrok.url` trong `config.properties`
- Hoặc set system property: `-Dvnpay.ngrok.url=https://xxx.ngrok-free.app`

### 5. Database Schema
Đảm bảo bảng `orders` có các cột:
- `payment_method` (NVARCHAR(50))
- `payment_status` (NVARCHAR(50))
- `vnp_transaction_id` (NVARCHAR(100))

## 🐛 Xử Lý Lỗi

### Lỗi Code 70 - "Sai chữ ký"
- Kiểm tra Hash Secret đã đúng chưa
- Kiểm tra Return URL đã được cấu hình trong VNPay Dashboard chưa
- Kiểm tra hash data có đúng format không (RAW, không encode)

### Lỗi Code 03 - "Dữ liệu gửi không đúng định dạng"
- Kiểm tra tất cả parameters có đầy đủ không
- Kiểm tra format của các giá trị (amount phải nhân 100)

### Callback Không Hoạt Động
- Kiểm tra ngrok đang chạy (nếu dùng localhost)
- Kiểm tra Return URL trong VNPay Dashboard
- Kiểm tra firewall/antivirus có chặn không

## 📚 Tài Liệu Tham Khảo

- **VNPay Sandbox Dashboard:** https://sandbox.vnpayment.vn/merchantv2/
- **VNPay API Docs:** https://sandbox.vnpayment.vn/apis/docs/gioi-thieu/
- **Debug Servlet:** http://localhost:9999/WebEcommerce/debug/vnpay

---

**✅ Triển khai hoàn tất! Hãy rebuild project, restart Tomcat, và test thanh toán VNPay!**

