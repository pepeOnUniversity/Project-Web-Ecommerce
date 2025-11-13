# 🔧 Hướng Dẫn Khắc Phục Lỗi VNPay Code 70

## ❌ Vấn Đề

Khi click "Thanh toán qua VNPay", bạn bị redirect đến:
```
https://sandbox.vnpayment.vn/paymentv2/Payment/Error.html?code=70
```

**Lỗi Code 70** thường có nghĩa là:
- ❌ Signature không hợp lệ
- ❌ Thiếu tham số bắt buộc
- ❌ TMN Code hoặc Hash Secret không đúng
- ❌ **URL callback chưa được cấu hình trong VNPay Dashboard**

## ✅ Giải Pháp

### Bước 1: Kiểm Tra Cấu Hình Trong Code

#### 1.1. Kiểm Tra File config.properties

Mở file: `src/java/config.properties`

Đảm bảo có các dòng sau:

```properties
vnpay.tmn.code=OXPI7X5A
vnpay.hash.secret=JHFEEOVQ8MLPL1054W0O0IUZDE8P2LIN
ngrok.url=https://xxxx-xx-xx-xx-xx.ngrok-free.app
```

**Lưu ý:** 
- Thay `xxxx-xx-xx-xx-xx.ngrok-free.app` bằng URL ngrok thực tế của bạn
- Nếu chưa có ngrok URL, chạy script `start-ngrok.ps1` trước

#### 1.2. Sử Dụng Debug Servlet

Truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`

Servlet này sẽ hiển thị:
- ✅ TMN Code có đúng không
- ✅ Hash Secret có đúng không
- ✅ Ngrok URL có được cấu hình không
- ✅ Payment URL được tạo ra như thế nào
- ✅ Tất cả các tham số trong URL

**Nếu có lỗi ở đây, sửa trước khi tiếp tục!**

### Bước 2: Cấu Hình VNPay Dashboard (QUAN TRỌNG NHẤT!)

**Đây là bước QUAN TRỌNG NHẤT mà nhiều người bỏ qua!**

VNPay Sandbox yêu cầu bạn phải cấu hình **Return URL** và **IPN URL** trong dashboard của họ. Nếu không cấu hình, sẽ bị lỗi Code 70.

#### 2.1. Đăng Nhập VNPay Dashboard

1. Truy cập: https://sandbox.vnpayment.vn/merchantv2/
2. Đăng nhập với thông tin:
   - **Username**: `contact.me.dothehung@gmail.com`
   - **Password**: `0586255568@Qa`

#### 2.2. Lấy Ngrok URL

**Trước khi cấu hình, bạn cần có ngrok URL:**

1. Chạy script ngrok:
   ```powershell
   .\start-ngrok.ps1
   ```

2. Copy URL ngrok được hiển thị, ví dụ:
   ```
   https://abcd-1234-5678-90ef.ngrok-free.app
   ```

3. **Lưu ý:** URL này sẽ thay đổi mỗi lần chạy ngrok (trừ khi dùng ngrok account có domain cố định)

#### 2.3. Cấu Hình Return URL và IPN URL

Trong VNPay Dashboard:

1. Vào mục **"Thông tin website"** hoặc **"Cấu hình"** hoặc **"Cài đặt"**
2. Tìm phần **"Return URL"** hoặc **"URL trả về"**
3. Nhập URL:
   ```
   https://YOUR-NGROK-URL.ngrok-free.app/WebEcommerce/vnpay-return
   ```
   (Thay `YOUR-NGROK-URL` bằng URL ngrok thực tế)

4. Tìm phần **"IPN URL"** hoặc **"Instant Payment Notification URL"** (nếu có)
5. Nhập URL:
   ```
   https://YOUR-NGROK-URL.ngrok-free.app/WebEcommerce/vnpay-ipn
   ```

6. **Lưu** cấu hình

#### 2.4. Kiểm Tra Lại

Sau khi cấu hình xong:
- Đợi 1-2 phút để VNPay cập nhật
- Restart Tomcat
- Test lại thanh toán

### Bước 3: Kiểm Tra Các Vấn Đề Khác

#### 3.1. Kiểm Tra Signature

Lỗi Code 70 có thể do signature không đúng. Để kiểm tra:

1. Mở Debug Servlet: `http://localhost:9999/WebEcommerce/debug/vnpay`
2. Xem Payment URL được tạo
3. Copy URL và test trực tiếp trên trình duyệt

#### 3.2. Kiểm Tra Các Tham Số Bắt Buộc

Đảm bảo các tham số sau có trong URL:
- ✅ `vnp_Version` = "2.1.0"
- ✅ `vnp_Command` = "pay"
- ✅ `vnp_TmnCode` = "OXPI7X5A"
- ✅ `vnp_Amount` = số tiền (đã nhân 100)
- ✅ `vnp_CurrCode` = "VND"
- ✅ `vnp_TxnRef` = transaction reference
- ✅ `vnp_OrderInfo` = thông tin đơn hàng
- ✅ `vnp_OrderType` = "other"
- ✅ `vnp_Locale` = "vn"
- ✅ `vnp_ReturnUrl` = URL callback
- ✅ `vnp_IpAddr` = IP address
- ✅ `vnp_CreateDate` = thời gian (format: yyyyMMddHHmmss)
- ✅ `vnp_SecureHash` = signature

#### 3.3. Kiểm Tra TMN Code và Hash Secret

Đảm bảo:
- TMN Code: `OXPI7X5A` (đúng, không có khoảng trắng)
- Hash Secret: `JHFEEOVQ8MLPL1054W0O0IUZDE8P2LIN` (đúng, không có khoảng trắng)

### Bước 4: Test Lại

Sau khi đã cấu hình xong:

1. **Restart Tomcat** (quan trọng!)
2. **Đảm bảo ngrok đang chạy**
3. **Kiểm tra ngrok URL trong config.properties đã đúng chưa**
4. **Kiểm tra VNPay Dashboard đã cấu hình Return URL chưa**
5. Test thanh toán lại

## 🐛 Troubleshooting

### Lỗi: "Không thể tạo Payment URL"

**Nguyên nhân:**
- TMN Code hoặc Hash Secret chưa được cấu hình
- File config.properties không được load

**Giải pháp:**
1. Kiểm tra file `src/java/config.properties` có tồn tại không
2. Kiểm tra TMN Code và Hash Secret có đúng không
3. Restart Tomcat
4. Kiểm tra log trong Tomcat console

### Lỗi: Vẫn Bị Code 70 Sau Khi Cấu Hình

**Nguyên nhân:**
- Return URL trong VNPay Dashboard chưa đúng
- Ngrok URL đã thay đổi nhưng chưa cập nhật trong VNPay Dashboard
- Signature vẫn không đúng

**Giải pháp:**
1. **Kiểm tra lại Return URL trong VNPay Dashboard:**
   - Phải là URL ngrok + `/WebEcommerce/vnpay-return`
   - Không có trailing slash
   - Phải là HTTPS

2. **Kiểm tra ngrok URL:**
   - Mở ngrok dashboard: `http://localhost:4040`
   - Xem URL hiện tại
   - So sánh với URL trong VNPay Dashboard

3. **Kiểm tra signature:**
   - Mở Debug Servlet
   - Copy Payment URL
   - So sánh với URL mẫu từ VNPay

### Lỗi: Ngrok URL Thay Đổi Mỗi Lần Chạy

**Giải pháp:**
1. **Dùng ngrok account (miễn phí):**
   - Đăng ký tại: https://dashboard.ngrok.com/signup
   - Lấy authtoken
   - Cấu hình: `ngrok config add-authtoken YOUR_TOKEN`
   - Dùng domain cố định: `ngrok http 9999 --domain=your-domain.ngrok-free.app`

2. **Hoặc cập nhật lại VNPay Dashboard mỗi lần chạy ngrok mới**

## 📋 Checklist

Trước khi test, đảm bảo:

- [ ] File `src/java/config.properties` có đầy đủ thông tin VNPay
- [ ] TMN Code: `OXPI7X5A`
- [ ] Hash Secret: `JHFEEOVQ8MLPL1054W0O0IUZDE8P2LIN`
- [ ] Ngrok đang chạy và URL đã được cập nhật vào `config.properties`
- [ ] **Đã cấu hình Return URL trong VNPay Dashboard**
- [ ] **Đã cấu hình IPN URL trong VNPay Dashboard (nếu có)**
- [ ] Return URL trong VNPay Dashboard khớp với ngrok URL hiện tại
- [ ] Đã restart Tomcat sau khi cập nhật config
- [ ] Đã test bằng Debug Servlet và không có lỗi

## 🔗 Liên Kết Hữu Ích

- **VNPay Sandbox Dashboard**: https://sandbox.vnpayment.vn/merchantv2/
- **VNPay API Docs**: https://sandbox.vnpayment.vn/apis/docs/gioi-thieu/
- **Ngrok Dashboard** (khi ngrok đang chạy): http://localhost:4040
- **Debug Servlet**: http://localhost:9999/WebEcommerce/debug/vnpay

---

**Lưu ý quan trọng:** Nếu vẫn gặp lỗi sau khi đã làm đủ các bước trên, hãy:
1. Kiểm tra log trong Tomcat console
2. Kiểm tra ngrok dashboard để xem có request từ VNPay không
3. Liên hệ VNPay support nếu cần


