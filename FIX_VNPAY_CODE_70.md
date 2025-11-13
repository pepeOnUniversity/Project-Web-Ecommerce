# 🔧 Khắc Phục Lỗi VNPay Code 70 - "Sai chữ ký"

## ✅ Đã Sửa

### 1. Sửa Cách Tạo Hash Data trong `VNPayUtil.java`

**Vấn đề:** Khi tạo hash data để tính signature, code đang **encode giá trị**, nhưng theo tài liệu VNPay, hash data phải là giá trị **RAW (không encode)**.

**Đã sửa:**
- **File:** `src/java/com/ecommerce/util/VNPayUtil.java`
- **Dòng 82-86:** Thay đổi từ encode giá trị sang dùng giá trị RAW
  ```java
  // TRƯỚC (SAI):
  hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
  
  // SAU (ĐÚNG):
  hashData.append(fieldValue); // Dùng giá trị RAW, không encode
  ```

### 2. Sửa Cách Verify Payment trong `VNPayUtil.java`

**Vấn đề:** Khi verify callback từ VNPay, các giá trị đã được URL encode, nhưng khi tạo hash data để verify, cần decode về giá trị RAW trước.

**Đã sửa:**
- **File:** `src/java/com/ecommerce/util/VNPayUtil.java`
- **Dòng 143-168:** Thêm decode giá trị trước khi tạo hash data
  ```java
  // Decode giá trị từ URL encoded về RAW
  fieldValue = java.net.URLDecoder.decode(fieldValue, StandardCharsets.UTF_8.toString());
  hashData.append(fieldValue); // Dùng giá trị RAW
  ```

---

## 📋 Các Bước Tiếp Theo

### Bước 1: Rebuild Project

1. **Clean và Build lại project:**
   - Trong IDE (Eclipse/IntelliJ): Right-click project → Clean → Build
   - Hoặc dùng Maven: `mvn clean compile`

### Bước 2: Restart Tomcat

**QUAN TRỌNG:** Sau khi sửa code, **PHẢI restart Tomcat** để load code mới.

1. Dừng Tomcat
2. Khởi động lại Tomcat
3. Đợi Tomcat khởi động hoàn tất

### Bước 3: Kiểm Tra Debug Servlet

1. Truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`
2. Kiểm tra:
   - ✅ TMN Code: `OXPI7X5A`
   - ✅ Hash Secret: `Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7` (32 ký tự)
   - ✅ Ngrok URL: `https://34e28004cc12.ngrok-free.app`
   - ✅ Return URL: `https://34e28004cc12.ngrok-free.app/WebEcommerce/vnpay-return`

### Bước 4: Cấu Hình Return URL trong VNPay Dashboard

**QUAN TRỌNG NHẤT:** VNPay Sandbox **BẮT BUỘC** phải có Return URL được cấu hình trong dashboard.

1. **Đăng nhập VNPay Dashboard:**
   - URL: https://sandbox.vnpayment.vn/merchantv2/
   - Username: `contact.me.dothehung@gmail.com`
   - Password: `0586255568@Qa`

2. **Tìm phần cấu hình Return URL:**
   - Vào "Thông tin website" hoặc "Cấu hình"
   - Tìm "Return URL" hoặc "URL trả về"

3. **Nhập Return URL:**
   ```
   https://34e28004cc12.ngrok-free.app/WebEcommerce/vnpay-return
   ```
   ⚠️ **Lưu ý:** URL này phải **KHỚP CHÍNH XÁC** với ngrok URL hiện tại của bạn!

4. **Lưu cấu hình**
   - Đợi 1-2 phút để VNPay cập nhật

### Bước 5: Test Thanh Toán

1. Tạo một đơn hàng test
2. Chọn thanh toán VNPay
3. Kiểm tra:
   - ✅ Payment URL được tạo thành công
   - ✅ Không bị lỗi code 70
   - ✅ Có thể redirect đến VNPay
   - ✅ Sau khi thanh toán, callback về đúng URL

---

## 🔍 Kiểm Tra Logs

Sau khi test, kiểm tra logs trong Tomcat để xem:
- Hash Data được tạo như thế nào
- Payment URL có đúng không
- Có lỗi gì không

**Xem logs:**
- Trong IDE: Console tab
- Hoặc file: `tomcat/logs/catalina.out`

---

## ⚠️ Lưu Ý Quan Trọng

### 1. Hash Data Phải Là Giá Trị RAW

- ❌ **SAI:** `vnp_ReturnUrl=https%3A%2F%2F34e28004cc12.ngrok-free.app%2FWebEcommerce%2Fvnpay-return`
- ✅ **ĐÚNG:** `vnp_ReturnUrl=https://34e28004cc12.ngrok-free.app/WebEcommerce/vnpay-return`

### 2. Query String Phải Encode

- Query string trong URL phải encode: `vnp_ReturnUrl=https%3A%2F%2F...`
- Nhưng hash data phải là RAW: `vnp_ReturnUrl=https://...`

### 3. Return URL Phải Khớp

- Return URL trong VNPay Dashboard phải **KHỚP CHÍNH XÁC** với ngrok URL hiện tại
- Nếu ngrok URL thay đổi, phải cập nhật lại trong VNPay Dashboard

### 4. Hash Secret Phải Đúng

- Đảm bảo Hash Secret mới đã được cập nhật: `Y0NSWV2BYEBD1F2TSCIJHP8PBM7MSIO7`
- Đã restart Tomcat sau khi cập nhật

---

## 🐛 Nếu Vẫn Bị Lỗi Code 70

### Kiểm Tra Lại:

1. **Hash Secret:**
   - Truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`
   - Kiểm tra Hash Secret có đúng không

2. **Return URL:**
   - Kiểm tra Return URL trong VNPay Dashboard
   - So sánh với ngrok URL hiện tại

3. **Code đã được rebuild:**
   - Đảm bảo đã clean và build lại project
   - Đã restart Tomcat

4. **Logs:**
   - Xem logs để kiểm tra hash data được tạo như thế nào
   - So sánh với tài liệu VNPay

5. **Liên Hệ VNPay Support:**
   - Nếu vẫn không được, liên hệ VNPay support
   - Cung cấp: TMN Code, Hash Secret (masked), Return URL, và logs

---

## ✅ Checklist

Sau khi sửa, đảm bảo:

- [x] Code đã được sửa (hash data không encode)
- [ ] Đã rebuild project
- [ ] Đã restart Tomcat
- [ ] Debug Servlet hiển thị đúng thông tin
- [ ] **Đã cấu hình Return URL trong VNPay Dashboard**
- [ ] Return URL trong VNPay Dashboard khớp với ngrok URL hiện tại
- [ ] Test thanh toán thành công

---

## 📚 Tài Liệu Tham Khảo

- **VNPay Sandbox Dashboard:** https://sandbox.vnpayment.vn/merchantv2/
- **VNPay API Docs:** https://sandbox.vnpayment.vn/apis/docs/gioi-thieu/
- **Debug Servlet:** http://localhost:9999/WebEcommerce/debug/vnpay

---

**Sau khi hoàn thành các bước trên, test lại thanh toán VNPay!**

