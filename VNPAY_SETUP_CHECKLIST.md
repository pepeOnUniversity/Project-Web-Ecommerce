# ✅ Checklist Thiết Lập VNPay

## 📋 Tổng Quan

Hệ thống VNPay đã được tích hợp vào code. Bạn cần thực hiện các bước sau để hoàn tất thiết lập:

## ✅ Bước 1: Cập Nhật Database

### 1.1. Chạy Script SQL

Mở **SQL Server Management Studio** hoặc **Azure Data Studio** và chạy file:

```
add_vnpay_columns.sql
```

**Lưu ý:** Nhớ thay `[YourDatabaseName]` bằng tên database thực tế của bạn trong file SQL.

Script này sẽ thêm 3 cột vào bảng `orders`:
- `payment_method` (NVARCHAR(50)) - 'COD' hoặc 'VNPAY'
- `payment_status` (NVARCHAR(50)) - 'PENDING', 'PAID', 'FAILED', 'CANCELLED', 'REFUNDED'
- `vnp_transaction_id` (NVARCHAR(100)) - Lưu transaction ID từ VNPay

### 1.2. Kiểm Tra Kết Quả

Sau khi chạy script, kiểm tra bằng cách:

```sql
SELECT TOP 1 * FROM orders;
```

Đảm bảo các cột mới đã xuất hiện.

## ✅ Bước 2: Kiểm Tra Cấu Hình VNPay

### 2.1. Kiểm Tra config.properties

Mở file `src/java/config.properties` và đảm bảo có:

```properties
vnpay.tmn.code=OXPI7X5A
vnpay.hash.secret=JHFEEOVQ8MLPL1054W0O0IUZDE8P2LIN
```

**✅ Đã có sẵn trong file!**

### 2.2. Kiểm Tra Ngrok URL

Trong `config.properties`, kiểm tra:

```properties
ngrok.url=https://xxxx-xx-xx-xx-xx.ngrok-free.app
```

**Lưu ý:** URL này sẽ được tự động cập nhật khi chạy script `start-ngrok.ps1`.

## ✅ Bước 3: Build và Deploy

### 3.1. Clean and Build Project

Trong NetBeans:
1. Nhấn **Shift+F11** (Clean and Build)
2. Đợi đến khi thấy "BUILD SUCCESSFUL"

### 3.2. Deploy lên Tomcat

1. Đảm bảo Tomcat đang chạy trên port **9999** (hoặc port bạn đã cấu hình)
2. Deploy project lên Tomcat
3. Kiểm tra: Mở `http://localhost:9999/WebEcommerce`

## ✅ Bước 4: Khởi Động Ngrok

### 4.1. Chạy Script Ngrok

Mở PowerShell trong thư mục project và chạy:

```powershell
.\start-ngrok.ps1
```

**Lưu ý:** 
- Script sẽ tự động cập nhật URL ngrok vào `config.properties`
- **Giữ cửa sổ PowerShell mở** khi test (nếu đóng, ngrok sẽ dừng)

### 4.2. Copy URL Ngrok

Sau khi script chạy, bạn sẽ thấy output như:

```
[OK] NGROK DA KHOI DONG THANH CONG!
Public URL: https://xxxx-xx-xx-xx-xx.ngrok-free.app
```

**Copy URL này** để sử dụng sau.

## ✅ Bước 5: Restart Tomcat

Sau khi ngrok đã chạy và URL đã được cập nhật vào `config.properties`:

1. **Stop Tomcat** trong NetBeans
2. **Start lại Tomcat**
3. Đợi đến khi thấy "Server startup in XXXX ms"

**Lý do:** Tomcat cần đọc lại `config.properties` với URL ngrok mới.

## ✅ Bước 6: Test Thanh Toán VNPay

### 6.1. Chuẩn Bị

1. ✅ Đăng nhập vào hệ thống với tài khoản user
2. ✅ Thêm sản phẩm vào giỏ hàng
3. ✅ Vào trang **Giỏ hàng** → Click **"Thanh toán"**

### 6.2. Test Thanh Toán

1. Trong trang **Checkout**:
   - Điền thông tin giao hàng
   - Chọn **"Thanh toán online qua VNPay"**
   - Click **"Thanh toán qua VNPay"**

2. Trên trang **VNPay Sandbox**:
   - **Số thẻ**: `9704198526191432198`
   - **Tên chủ thẻ**: `NGUYEN VAN A`
   - **Ngày hết hạn**: `03/07`
   - **CVV**: `123456`
   - **OTP**: `123456`
   - Click **"Thanh toán"**

3. **Kết quả:**
   - Nếu thành công: Redirect về trang "Thanh toán thành công!"
   - Order status: `CONFIRMED`
   - Payment status: `PAID`
   - Giỏ hàng được xóa tự động

## ✅ Bước 7: Kiểm Tra Kết Quả

### 7.1. Kiểm Tra Trong Hệ Thống

1. Vào trang **"Lịch sử đơn hàng"** (`/orders`)
2. Xem order vừa tạo:
   - Status: `CONFIRMED`
   - Payment Method: `VNPAY`
   - Payment Status: `PAID`
   - Transaction ID: Có mã giao dịch từ VNPay

### 7.2. Kiểm Tra Trong VNPay Dashboard

1. Đăng nhập: https://sandbox.vnpayment.vn/merchantv2/
   - Username: `contact.me.dothehung@gmail.com`
   - Password: `0586255568@Qa`
2. Vào mục **"Giao dịch"** → Xem giao dịch vừa test

### 7.3. Kiểm Tra Ngrok Dashboard

1. Mở trình duyệt: `http://localhost:4040`
2. Xem các requests:
   - Request từ VNPay đến callback URL
   - Request/Response details

## 🐛 Troubleshooting

### Lỗi: "Không thể tạo liên kết thanh toán"

**Nguyên nhân:**
- VNPay config chưa đúng (TMN Code hoặc Hash Secret)
- Không đọc được config từ `config.properties`

**Giải pháp:**
1. Kiểm tra `src/java/config.properties` có đúng TMN Code và Hash Secret không
2. Đảm bảo file `config.properties` nằm trong `src/java/` hoặc `WEB-INF/classes/`
3. Restart Tomcat

### Lỗi: "Xác thực thanh toán không thành công"

**Nguyên nhân:**
- Hash Secret không đúng
- Signature verification failed

**Giải pháp:**
1. Kiểm tra lại Hash Secret trong `config.properties`
2. Restart Tomcat

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
3. Kiểm tra URL trong `config.properties`
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
- Các cột database chưa được thêm

**Giải pháp:**
1. Kiểm tra đã chạy script `add_vnpay_columns.sql` chưa
2. Kiểm tra log trong Tomcat console
3. Kiểm tra ngrok dashboard xem có request từ VNPay không
4. Kiểm tra database xem order có được tạo không

## 📚 Tài Liệu Tham Khảo

- **Hướng dẫn test chi tiết**: `VNPAY_TEST_GUIDE.md`
- **Quick start**: `VNPAY_QUICK_START.md`
- **Hướng dẫn ngrok**: `QUICK_START_NGROK.md`
- **VNPay Sandbox**: https://sandbox.vnpayment.vn/
- **VNPay API Docs**: https://sandbox.vnpayment.vn/apis/docs/gioi-thieu/

## ✅ Checklist Hoàn Thành

Sau khi hoàn thành tất cả các bước, đảm bảo:

- [ ] Đã chạy script `add_vnpay_columns.sql` thành công
- [ ] `config.properties` có đầy đủ thông tin VNPay
- [ ] Đã build và deploy project lên Tomcat
- [ ] Ngrok đang chạy và URL đã được cập nhật
- [ ] Đã restart Tomcat sau khi cập nhật ngrok URL
- [ ] Đã test thanh toán thành công
- [ ] Order được cập nhật đúng trong database
- [ ] Giao dịch hiển thị trong VNPay dashboard
- [ ] Callback được xử lý đúng (kiểm tra trong ngrok dashboard)

---

**Chúc bạn thiết lập thành công! 🎉**


