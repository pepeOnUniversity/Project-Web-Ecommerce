# 🔧 Khắc Phục Lỗi VNPay

## ✅ Đã Sửa

### 1. Debug Servlet Không Hoạt Động

**Vấn đề:** Không truy cập được `http://localhost:9999/WebEcommerce/debug/vnpay`

**Nguyên nhân:** Servlet chưa được đăng ký trong `web.xml`

**Đã sửa:**
- ✅ Đăng ký `DebugVNPayServlet` trong `web/WEB-INF/web.xml`
- ✅ Bây giờ có thể truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`

### 2. Lỗi Code 03 - Dữ Liệu Gửi Không Đúng Định Dạng

**Vấn đề:** Khi test trực tiếp trên VNPay sandbox, bị lỗi code 03

**Nguyên nhân:** Logic tạo hash data có lỗi - khi kiểm tra `itr.hasNext()`, nó không tính đến các field bị bỏ qua (null hoặc rỗng), dẫn đến việc thêm `&` không đúng chỗ.

**Đã sửa:**
- ✅ Sửa logic tạo hash data trong `VNPayUtil.java`
- ✅ Lọc các field hợp lệ trước, rồi mới tạo hash data
- ✅ Đảm bảo không có `&` thừa ở cuối hash data

### 3. Config Properties Không Được Load

**Vấn đề:** File `config.properties` nằm ở `src/java/config.properties` nhưng không được copy vào `WEB-INF/classes/` khi build

**Đã sửa:**
- ✅ Copy file `config.properties` vào `web/WEB-INF/classes/config.properties`
- ✅ Bây giờ VNPayConfig có thể load được config từ classpath

## 📋 Các Bước Tiếp Theo

### 1. Restart Tomcat

**QUAN TRỌNG:** Sau khi sửa code, bạn **PHẢI restart Tomcat** để:
- Load lại servlet mới
- Load lại config.properties
- Áp dụng các thay đổi code

### 2. Kiểm Tra Debug Servlet

1. Restart Tomcat
2. Truy cập: `http://localhost:9999/WebEcommerce/debug/vnpay`
3. Kiểm tra:
   - ✅ TMN Code có đúng không (`OXPI7X5A`)
   - ✅ Hash Secret có đúng không (độ dài: 32 ký tự)
   - ✅ Ngrok URL có được cấu hình không
   - ✅ Payment URL được tạo ra như thế nào

### 3. Test Payment URL

1. Trong Debug Servlet, click link "Mở URL trong tab mới"
2. Kiểm tra xem có bị lỗi code 03 không
3. Nếu vẫn bị lỗi, kiểm tra:
   - TMN Code và Hash Secret có đúng không
   - Các tham số trong URL có đầy đủ không
   - Hash signature có đúng không

### 4. Cấu Hình VNPay Dashboard (Nếu Vẫn Bị Lỗi Code 70)

Nếu sau khi sửa vẫn bị lỗi code 70, bạn cần:

1. **Đăng nhập VNPay Dashboard:**
   - URL: https://sandbox.vnpayment.vn/merchantv2/
   - Username: `contact.me.dothehung@gmail.com`
   - Password: `0586255568@Qa`

2. **Cấu hình Return URL:**
   - Vào "Thông tin website" hoặc "Cấu hình"
   - Tìm "Return URL" hoặc "URL trả về"
   - Nhập: `https://YOUR-NGROK-URL.ngrok-free.app/WebEcommerce/vnpay-return`
   - (Thay YOUR-NGROK-URL bằng URL ngrok thực tế)

3. **Lưu cấu hình**

## 🔍 Kiểm Tra Log

Sau khi restart Tomcat, kiểm tra log trong Tomcat console để xem:
- Có load được config.properties không
- Có lỗi gì khi tạo payment URL không
- Hash data được tạo ra như thế nào

## 🐛 Troubleshooting

### Vẫn Không Vào Được Debug Servlet

1. **Kiểm tra Tomcat có đang chạy không:**
   - Port 9999 có đang được sử dụng không
   - Ứng dụng có được deploy không

2. **Kiểm tra web.xml:**
   - Đảm bảo servlet đã được đăng ký đúng
   - Không có lỗi syntax trong web.xml

3. **Kiểm tra log Tomcat:**
   - Xem có lỗi gì khi deploy không

### Vẫn Bị Lỗi Code 03

1. **Kiểm tra Debug Servlet:**
   - Xem Payment URL được tạo ra như thế nào
   - Kiểm tra các tham số có đầy đủ không

2. **Kiểm tra TMN Code và Hash Secret:**
   - Đảm bảo đúng với thông tin từ VNPay
   - Không có khoảng trắng thừa

3. **Kiểm tra Hash Data:**
   - Xem log trong Tomcat console
   - So sánh với tài liệu VNPay

### Config Không Được Load

1. **Kiểm tra file có tồn tại không:**
   - `web/WEB-INF/classes/config.properties`

2. **Kiểm tra nội dung file:**
   - Đảm bảo có `vnpay.tmn.code` và `vnpay.hash.secret`
   - Không có lỗi syntax

3. **Restart Tomcat:**
   - Đảm bảo restart để load lại config

## 📝 Lưu Ý

- **File config.properties chứa thông tin nhạy cảm:** Không commit vào Git
- **Mỗi lần build mới:** Có thể cần copy lại file config.properties vào WEB-INF/classes/
- **Hoặc:** Cấu hình build script để tự động copy file này

## ✅ Checklist

Sau khi sửa, đảm bảo:

- [ ] Đã restart Tomcat
- [ ] Có thể truy cập Debug Servlet: `http://localhost:9999/WebEcommerce/debug/vnpay`
- [ ] Debug Servlet hiển thị đúng TMN Code và Hash Secret
- [ ] Payment URL được tạo thành công
- [ ] Test payment URL không bị lỗi code 03
- [ ] File `web/WEB-INF/classes/config.properties` tồn tại và có nội dung đúng

---

**Sau khi hoàn thành các bước trên, test lại thanh toán VNPay!**


