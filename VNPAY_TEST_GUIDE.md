# 🧪 Hướng Dẫn Test Thanh Toán VNPay

## ✅ Đã Cấu Hình

Thông tin VNPay đã được cấu hình vào hệ thống:

- **Terminal ID (TMN Code)**: `OXPI7X5A`
- **Hash Secret**: `JHFEEOVQ8MLPL1054W0O0IUZDE8P2LIN`
- **Payment URL**: `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html`
- **Merchant Admin**: `https://sandbox.vnpayment.vn/merchantv2/`
  - Username: `contact.me.dothehung@gmail.com`
  - Password: `0586255568@Qa`

## 📋 Checklist Trước Khi Test

Trước khi test thanh toán VNPay, đảm bảo:

- [ ] **Tomcat đang chạy** trên port 9999 (hoặc port bạn đã cấu hình)
- [ ] **Ngrok đang chạy** và đã lấy được URL công khai
- [ ] **URL ngrok đã được cập nhật** vào `config.properties` (script tự động làm)
- [ ] **Đã đăng nhập** vào hệ thống với tài khoản user
- [ ] **Có sản phẩm trong giỏ hàng**

## 🚀 Các Bước Test Thanh Toán

### Bước 1: Khởi Động Ngrok

Chạy script để khởi động ngrok:

```cmd
start-ngrok.bat
```

Hoặc:

```powershell
powershell -ExecutionPolicy Bypass -File .\start-ngrok.ps1
```

**Lưu ý:** 
- Script sẽ tự động cập nhật URL ngrok vào `config.properties`
- **Giữ cửa sổ PowerShell/CMD mở** khi test (nếu đóng, ngrok sẽ dừng)

### Bước 2: Kiểm Tra URL Ngrok

Sau khi ngrok khởi động, bạn sẽ thấy output như:

```
[OK] NGROK DA KHOI DONG THANH CONG!
[OK] Da cap nhat ngrok.url=https://xxxx-xx-xx-xx-xx.ngrok-free.app vao config.properties

Cac URL callback cho VNPay:
  Return URL: https://xxxx-xx-xx-xx-xx.ngrok-free.app/WebEcommerce/vnpay-return
  IPN URL:    https://xxxx-xx-xx-xx-xx.ngrok-free.app/WebEcommerce/vnpay-ipn
```

**Copy URL này** để sử dụng sau.

### Bước 3: Restart Tomcat (Nếu Cần)

Nếu bạn đã chạy Tomcat trước khi chạy ngrok, cần **restart Tomcat** để nó đọc lại `config.properties` với URL ngrok mới.

**Cách restart:**
1. Stop Tomcat trong NetBeans
2. Start lại Tomcat
3. Hoặc restart project trong NetBeans

### Bước 4: Thêm Sản Phẩm Vào Giỏ Hàng

1. Đăng nhập vào hệ thống
2. Duyệt sản phẩm và thêm vào giỏ hàng
3. Vào trang **Giỏ hàng** để xem các sản phẩm

### Bước 5: Vào Trang Checkout

1. Click nút **"Thanh toán"** hoặc **"Checkout"** trong giỏ hàng
2. Điền thông tin giao hàng:
   - Họ tên
   - Số điện thoại
   - Địa chỉ
   - Ghi chú (nếu có)

### Bước 6: Chọn Phương Thức Thanh Toán

1. Chọn **"Thanh toán online qua VNPay"**
2. Click nút **"Thanh toán qua VNPay"**

### Bước 7: Thanh Toán Trên VNPay

Sau khi click, bạn sẽ được redirect đến trang thanh toán VNPay Sandbox.

**Thông tin thẻ test (VNPay Sandbox):**

| Loại thẻ | Số thẻ | Tên chủ thẻ | Ngày hết hạn | CVV |
|----------|--------|-------------|--------------|-----|
| Thẻ thành công | `9704198526191432198` | `NGUYEN VAN A` | `03/07` | `123456` |
| Thẻ thất bại | `9704198526191432199` | `NGUYEN VAN B` | `03/07` | `123456` |

**Các bước:**
1. Nhập thông tin thẻ test
2. Nhập OTP: `123456` (cho mọi giao dịch test)
3. Click **"Thanh toán"**

### Bước 8: Xem Kết Quả

Sau khi thanh toán, bạn sẽ được redirect về trang kết quả:

- **Nếu thành công:**
  - Hiển thị thông báo "Thanh toán thành công!"
  - Order status sẽ được cập nhật thành `CONFIRMED`
  - Payment status: `PAID`
  - Giỏ hàng sẽ được xóa tự động

- **Nếu thất bại:**
  - Hiển thị thông báo lỗi
  - Order status: `CANCELLED`
  - Payment status: `FAILED`

## 🔍 Kiểm Tra Giao Dịch

### 1. Kiểm Tra Trong Hệ Thống

- Vào trang **"Lịch sử đơn hàng"** (`/orders`)
- Xem order vừa tạo:
  - Status: `CONFIRMED` (nếu thành công)
  - Payment Method: `VNPAY`
  - Transaction ID: Có mã giao dịch từ VNPay

### 2. Kiểm Tra Trong VNPay Dashboard

1. Đăng nhập vào **Merchant Admin**:
   - URL: `https://sandbox.vnpayment.vn/merchantv2/`
   - Username: `contact.me.dothehung@gmail.com`
   - Password: `0586255568@Qa`

2. Vào mục **"Giao dịch"** hoặc **"Transaction"**
3. Xem danh sách giao dịch vừa test:
   - Transaction ID
   - Số tiền
   - Trạng thái
   - Thời gian

### 3. Kiểm Tra Ngrok Dashboard

1. Mở trình duyệt: `http://localhost:4040`
2. Xem các requests:
   - Request từ VNPay đến callback URL
   - Request/Response details
   - Status code

## 🐛 Troubleshooting

### Lỗi: "Không thể tạo liên kết thanh toán"

**Nguyên nhân:**
- VNPay config chưa đúng (TMN Code hoặc Hash Secret)
- Không đọc được config từ `config.properties`

**Giải pháp:**
1. Kiểm tra file `src/java/config.properties`:
   ```properties
   vnpay.tmn.code=OXPI7X5A
   vnpay.hash.secret=JHFEEOVQ8MLPL1054W0O0IUZDE8P2LIN
   ```

2. Đảm bảo file `config.properties` nằm trong `src/java/` hoặc `WEB-INF/classes/`

3. Restart Tomcat sau khi sửa config

### Lỗi: "Xác thực thanh toán không thành công"

**Nguyên nhân:**
- Hash Secret không đúng
- Signature verification failed

**Giải pháp:**
1. Kiểm tra lại Hash Secret trong `config.properties`
2. Đảm bảo Hash Secret đúng với thông tin từ VNPay
3. Restart Tomcat

### Lỗi: Không Nhận Được Callback Từ VNPay

**Nguyên nhân:**
- Ngrok không chạy
- URL ngrok không đúng
- VNPay không thể truy cập URL ngrok

**Giải pháp:**
1. Kiểm tra ngrok có đang chạy không:
   ```powershell
   Get-Process -Name ngrok -ErrorAction SilentlyContinue
   ```

2. Kiểm tra ngrok dashboard: `http://localhost:4040`

3. Kiểm tra URL trong `config.properties`:
   ```properties
   ngrok.url=https://xxxx-xx-xx-xx-xx.ngrok-free.app
   ```

4. Test URL ngrok trên trình duyệt:
   ```
   https://YOUR-NGROK-URL.ngrok-free.app/WebEcommerce
   ```
   - Lần đầu sẽ có warning page → Click "Visit Site"

5. Restart Tomcat sau khi cập nhật URL

### Lỗi: Order Không Được Cập Nhật Sau Khi Thanh Toán

**Nguyên nhân:**
- Callback không được xử lý đúng
- Database connection issue

**Giải pháp:**
1. Kiểm tra log trong Tomcat console
2. Kiểm tra ngrok dashboard xem có request từ VNPay không
3. Kiểm tra database xem order có được tạo không
4. Kiểm tra VNPayCallbackServlet có nhận được callback không

### Lỗi: "Invalid VNPay signature"

**Nguyên nhân:**
- Hash Secret không đúng
- Parameters bị thay đổi trong quá trình truyền

**Giải pháp:**
1. Kiểm tra lại Hash Secret
2. Xem log trong Tomcat để debug
3. Kiểm tra VNPayUtil.verifyPayment() có hoạt động đúng không

## 📝 Test Cases

### Test Case 1: Thanh Toán Thành Công

1. Chọn sản phẩm → Thêm vào giỏ hàng
2. Vào checkout → Chọn VNPay
3. Thanh toán với thẻ test thành công
4. **Kỳ vọng:**
   - Redirect về trang thành công
   - Order status: `CONFIRMED`
   - Payment status: `PAID`
   - Giỏ hàng được xóa

### Test Case 2: Thanh Toán Thất Bại

1. Chọn sản phẩm → Thêm vào giỏ hàng
2. Vào checkout → Chọn VNPay
3. Thanh toán với thẻ test thất bại
4. **Kỳ vọng:**
   - Redirect về trang thất bại
   - Order status: `CANCELLED`
   - Payment status: `FAILED`
   - Hiển thị thông báo lỗi

### Test Case 3: Hủy Thanh Toán

1. Chọn sản phẩm → Thêm vào giỏ hàng
2. Vào checkout → Chọn VNPay
3. Trên trang VNPay, click "Hủy" hoặc đóng tab
4. **Kỳ vọng:**
   - Order vẫn ở trạng thái `PENDING`
   - Có thể thanh toán lại sau

## 🔐 Thông Tin Bảo Mật

**QUAN TRỌNG:**
- **KHÔNG commit** file `config.properties` vào Git (đã có trong `.gitignore`)
- **KHÔNG chia sẻ** Hash Secret với người khác
- Trong production, sử dụng **Environment Variables** hoặc **System Properties** thay vì file config

## 📚 Tài Liệu Tham Khảo

- VNPay Sandbox: https://sandbox.vnpayment.vn/
- VNPay API Documentation: https://sandbox.vnpayment.vn/apis/docs/gioi-thieu/
- VNPay Merchant Admin: https://sandbox.vnpayment.vn/merchantv2/
- Hướng dẫn ngrok: `QUICK_START_NGROK.md`

## ✅ Checklist Sau Khi Test

Sau khi test xong, đảm bảo:

- [ ] Đã test thanh toán thành công
- [ ] Đã test thanh toán thất bại
- [ ] Order được cập nhật đúng trong database
- [ ] Giao dịch hiển thị trong VNPay dashboard
- [ ] Callback được xử lý đúng (kiểm tra trong ngrok dashboard)
- [ ] Giỏ hàng được xóa sau khi thanh toán thành công

---

**Chúc bạn test thành công! 🎉**



