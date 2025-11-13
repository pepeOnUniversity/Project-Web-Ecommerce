# 🔐 Thông Tin Cấu Hình VNPay

## ✅ Thông Tin Đã Được Cập Nhật

### Thông Tin Cấu Hình VNPay

**Terminal ID / Mã Website (vnp_TmnCode):**
```
OXPI7X5A
```

**Secret Key / Chuỗi bí mật tạo checksum (vnp_HashSecret):**
```
Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7
```

**URL Thanh Toán Môi Trường TEST (vnp_Url):**
```
https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
```

### Thông Tin Truy Cập Merchant Admin

**Địa chỉ:**
```
https://sandbox.vnpayment.vn/merchantv2/
```

**Tên đăng nhập:**
```
contact.me.dothehung@gmail.com
```

**Mật khẩu:**
```
0586255568@Qa
```

---

## 📝 Đã Cập Nhật

✅ **Hash Secret mới đã được cập nhật vào:**
- `src/java/config.properties`
- `web/WEB-INF/classes/config.properties`

✅ **TMN Code:** Đã đúng (`OXPI7X5A`)

---

## 🔄 Các Bước Tiếp Theo

### 1. Restart Tomcat (QUAN TRỌNG)

Sau khi cập nhật config, bạn **PHẢI restart Tomcat** để:
- Load lại config.properties với Hash Secret mới
- Áp dụng thay đổi

### 2. Kiểm Tra Debug Servlet

1. Restart Tomcat
2. Truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`
3. Kiểm tra:
   - ✅ TMN Code: `OXPI7X5A`
   - ✅ Hash Secret: `Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7` (32 ký tự)
   - ✅ Ngrok URL: Đã được cấu hình

### 3. Cấu Hình Return URL trong VNPay Dashboard

**QUAN TRỌNG:** Bạn cần cấu hình Return URL trong VNPay Dashboard:

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
   (Thay `YOUR-NGROK-URL` bằng URL ngrok thực tế của bạn)

4. **Lưu cấu hình**

### 4. Test Thanh Toán

1. Tạo một đơn hàng test
2. Chọn thanh toán VNPay
3. Kiểm tra:
   - Payment URL được tạo thành công
   - Không bị lỗi code 03
   - Có thể redirect đến VNPay
   - Sau khi thanh toán, callback về đúng URL

---

## 🔍 Kiểm Tra Cấu Hình

### Cách 1: Debug Servlet

Truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`

Sẽ hiển thị:
- TMN Code
- Hash Secret (masked)
- Ngrok URL
- Payment URL mẫu

### Cách 2: Kiểm Tra File Config

**File:** `src/java/config.properties` hoặc `web/WEB-INF/classes/config.properties`

Kiểm tra:
```properties
vnpay.tmn.code=OXPI7X5A
vnpay.hash.secret=Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7
```

---

## ⚠️ Lưu Ý

1. **Hash Secret mới:** Đảm bảo đã restart Tomcat để load Hash Secret mới
2. **Return URL:** Phải cấu hình trong VNPay Dashboard, nếu không sẽ bị lỗi code 70
3. **Ngrok URL:** Phải đảm bảo ngrok đang chạy và URL đúng
4. **Bảo mật:** Không commit file `config.properties` vào Git

---

## ✅ Checklist

Sau khi cập nhật, đảm bảo:

- [x] Hash Secret mới đã được cập nhật vào config.properties
- [ ] Đã restart Tomcat
- [ ] Debug Servlet hiển thị đúng Hash Secret mới
- [ ] Đã cấu hình Return URL trong VNPay Dashboard
- [ ] Ngrok đang chạy và URL đúng
- [ ] Test thanh toán thành công

---

**Sau khi hoàn thành các bước trên, test lại thanh toán VNPay với Hash Secret mới!**


