# Hướng Dẫn Tích Hợp VNPay

## Tổng Quan

Hệ thống đã được tích hợp thanh toán online qua VNPay. Người dùng có thể chọn thanh toán khi nhận hàng (COD) hoặc thanh toán online qua VNPay.

## Các Bước Thiết Lập

### 1. Đăng Ký Tài Khoản VNPay Sandbox

1. Truy cập: https://sandbox.vnpayment.vn/devreg/
2. Điền đầy đủ thông tin và đăng ký tài khoản
3. Sau khi đăng ký thành công, bạn sẽ nhận email chứa:
   - `vnp_TmnCode` (Mã website)
   - `vnp_HashSecret` (Chuỗi bí mật)

### 2. Cấu Hình VNPay

Mở file `src/java/com/ecommerce/config/VNPayConfig.java` và cập nhật:

```java
public static final String VNPAY_TMN_CODE = "YOUR_TMN_CODE_HERE"; // Thay bằng TMN Code từ VNPay
public static final String VNPAY_HASH_SECRET = "YOUR_HASH_SECRET_HERE"; // Thay bằng Hash Secret từ VNPay
```

### 3. Cập Nhật Database

Chạy file SQL để thêm các cột cần thiết:

```sql
-- Chạy file: add_payment_fields.sql
```

Hoặc chạy trực tiếp trong SQL Server Management Studio.

### 4. Cấu Hình Callback URL (Quan Trọng cho Localhost)

VNPay cần gửi callback về server của bạn. Khi chạy trên localhost, bạn cần:

#### Option 1: Sử dụng ngrok (Khuyến nghị)

1. Tải ngrok tại: https://ngrok.com/
2. Chạy ngrok để expose localhost:
   ```bash
   ngrok http 8080
   ```
3. Copy URL từ ngrok (ví dụ: `https://abc123.ngrok.io`)
4. Cập nhật trong VNPayConfig.java hoặc set system property:
   ```java
   // Trong VNPayConfig.java, method getReturnUrl() và getIpnUrl() 
   // sẽ tự động sử dụng ngrok URL nếu set system property
   ```
5. Hoặc cập nhật trực tiếp trong VNPayConfig:
   ```java
   public static final String VNPAY_RETURN_URL = "https://abc123.ngrok.io/WebEcommerce/vnpay-return";
   public static final String VNPAY_IPN_URL = "https://abc123.ngrok.io/WebEcommerce/vnpay-ipn";
   ```
6. Đăng ký URL này trong VNPay Dashboard (nếu có)

#### Option 2: Deploy lên server có IP công khai

Nếu bạn có server với IP công khai, cập nhật URL trong VNPayConfig.

### 5. Test Thanh Toán

#### Thông Tin Thẻ Test (VNPay Sandbox)

- **Ngân hàng**: NCB
- **Số thẻ**: 9704198526191432198
- **Tên chủ thẻ**: NGUYEN VAN A
- **Ngày phát hành**: 07/15
- **Mật khẩu OTP**: 123456

#### Quy Trình Test

1. Thêm sản phẩm vào giỏ hàng
2. Vào trang checkout
3. Chọn "Thanh toán online qua VNPay"
4. Điền thông tin giao hàng
5. Click "Thanh toán qua VNPay"
6. Sẽ được redirect đến trang VNPay
7. Sử dụng thông tin thẻ test ở trên
8. Sau khi thanh toán, sẽ được redirect về trang kết quả

## Cấu Trúc Code

### Các File Đã Tạo/Cập Nhật

1. **VNPayConfig.java**: Cấu hình VNPay (URLs, credentials)
2. **VNPayUtil.java**: Utility class để tạo payment URL và verify callback
3. **PaymentServlet.java**: Xử lý tạo order và redirect đến VNPay
4. **VNPayCallbackServlet.java**: Xử lý callback từ VNPay (Return URL và IPN)
5. **OrderServlet.java**: Cập nhật để hỗ trợ chọn payment method
6. **OrderDAO.java**: Thêm method để lưu payment status
7. **checkout.jsp**: Thêm option chọn phương thức thanh toán
8. **payment-result.jsp**: Trang hiển thị kết quả thanh toán

### Flow Thanh Toán

1. **User chọn VNPay** → Submit form checkout
2. **OrderServlet** → Tạo order với status PENDING, payment_method = VNPAY
3. **PaymentServlet** → Tạo payment URL từ VNPay và redirect
4. **User thanh toán trên VNPay**
5. **VNPayCallbackServlet** → Nhận callback:
   - Nếu thành công: Cập nhật order status = CONFIRMED, payment_status = PAID
   - Nếu thất bại: Cập nhật order status = CANCELLED, payment_status = FAILED
6. **Redirect về payment-result.jsp** → Hiển thị kết quả

## Database Schema

Các cột mới trong bảng `orders`:

- `payment_method`: NVARCHAR(50) - 'COD' hoặc 'VNPAY'
- `payment_status`: NVARCHAR(50) - 'PENDING', 'PAID', 'FAILED', 'CANCELLED', 'REFUNDED'
- `vnp_transaction_id`: NVARCHAR(100) - Lưu transaction ID từ VNPay

## Xử Lý Lỗi

### Lỗi Thường Gặp

1. **"Invalid signature"**
   - Kiểm tra `VNPAY_HASH_SECRET` đã đúng chưa
   - Kiểm tra VNPayUtil.verifyPayment() có hoạt động đúng không

2. **"Cannot create payment URL"**
   - Kiểm tra `VNPAY_TMN_CODE` đã đúng chưa
   - Kiểm tra kết nối internet

3. **Callback không hoạt động**
   - Kiểm tra ngrok đang chạy (nếu dùng localhost)
   - Kiểm tra URL trong VNPayConfig đã đúng chưa
   - Kiểm tra firewall/antivirus có chặn không

4. **Order không được cập nhật sau thanh toán**
   - Kiểm tra VNPayCallbackServlet có nhận được callback không
   - Kiểm tra log để xem có lỗi gì không
   - Kiểm tra database connection

## Lưu Ý Quan Trọng

1. **Bảo Mật**: 
   - KHÔNG commit `VNPAY_HASH_SECRET` lên Git
   - Sử dụng environment variables hoặc config file ngoài source code

2. **Production**:
   - Khi deploy lên production, cần đăng ký tài khoản thực với VNPay
   - Cập nhật URLs từ sandbox sang production
   - Cập nhật credentials mới

3. **Testing**:
   - Luôn test kỹ trên sandbox trước khi deploy production
   - Test cả trường hợp thành công và thất bại

## Hỗ Trợ

Nếu gặp vấn đề, tham khảo:
- Tài liệu VNPay: https://sandbox.vnpayment.vn/apis/docs/gioi-thieu/
- VNPay Developer Portal



