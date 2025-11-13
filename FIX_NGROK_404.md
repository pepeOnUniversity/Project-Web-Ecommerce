# 🔧 Fix Lỗi 404 Khi Truy Cập Qua Ngrok

## ❌ Vấn Đề

Khi truy cập ứng dụng qua ngrok, bạn gặp lỗi:
```
HTTP Status 404 – Not Found
The requested resource [/WebEcommerce] is not available
```

## 🔍 Nguyên Nhân

Ứng dụng đang chạy ở **root context** (`/`) nhưng:
- File `context.xml` cấu hình context path là `/WebEcommerce`
- Các script và hướng dẫn đang dùng URL với prefix `/WebEcommerce`
- → Mismatch giữa cấu hình và thực tế

## ✅ Giải Pháp Đã Áp Dụng

### 1. Sửa `context.xml`

Đã cập nhật file `web/META-INF/context.xml`:
```xml
<!-- Trước -->
<Context path="/WebEcommerce">

<!-- Sau -->
<Context path="/">
```

### 2. Cập Nhật Script Ngrok

Đã cập nhật `start-ngrok.ps1` để hiển thị đúng URL callback:
- **Trước**: `https://xxx.ngrok-free.app/WebEcommerce/vnpay-return`
- **Sau**: `https://xxx.ngrok-free.app/vnpay-return`

## 🚀 Các Bước Tiếp Theo

### Bước 1: Restart Tomcat

**QUAN TRỌNG**: Sau khi sửa `context.xml`, bạn **PHẢI restart Tomcat** để áp dụng thay đổi:

1. Stop Tomcat trong NetBeans
2. Clean and Build project (Shift+F11)
3. Start lại Tomcat

### Bước 2: Kiểm Tra Localhost

Truy cập:
```
http://localhost:9999/
```

Phải thấy trang chủ của ứng dụng (không phải 404).

### Bước 3: Kiểm Tra Ngrok

1. Đảm bảo ngrok đang chạy:
   ```powershell
   Get-Process -Name ngrok -ErrorAction SilentlyContinue
   ```

2. Nếu chưa chạy, chạy lại:
   ```cmd
   start-ngrok.bat
   ```

3. Truy cập URL ngrok:
   ```
   https://YOUR-NGROK-URL.ngrok-free.app/
   ```

   **Lưu ý**: Lần đầu sẽ có trang cảnh báo của ngrok → Click **"Visit Site"** để tiếp tục.

### Bước 4: Test VNPay Callback

Sau khi restart Tomcat, các URL callback sẽ là:
- **Return URL**: `https://YOUR-NGROK-URL.ngrok-free.app/vnpay-return`
- **IPN URL**: `https://YOUR-NGROK-URL.ngrok-free.app/vnpay-ipn`

## ✅ Checklist

Sau khi fix, đảm bảo:

- [ ] Đã sửa `context.xml` → `path="/"`
- [ ] Đã restart Tomcat
- [ ] `http://localhost:9999/` hoạt động (không phải 404)
- [ ] Ngrok đang chạy
- [ ] `https://YOUR-NGROK-URL.ngrok-free.app/` hoạt động (sau khi click "Visit Site")
- [ ] Có thể test thanh toán VNPay

## 🔍 Kiểm Tra Nhanh

### Test Localhost
```powershell
Invoke-WebRequest -Uri "http://localhost:9999/" -UseBasicParsing
```
→ Phải trả về Status 200

### Test Ngrok
```powershell
Invoke-WebRequest -Uri "https://YOUR-NGROK-URL.ngrok-free.app/" -UseBasicParsing
```
→ Phải trả về Status 200 (hoặc redirect đến trang cảnh báo ngrok)

## 📝 Lưu Ý

1. **Context Path**: Ứng dụng hiện tại chạy ở root (`/`), không phải `/WebEcommerce`
2. **URL Callback**: Tất cả URL callback VNPay không có prefix `/WebEcommerce`
3. **Restart Tomcat**: Luôn restart Tomcat sau khi sửa `context.xml`

## 🐛 Nếu Vẫn Gặp Lỗi

1. **Kiểm tra Tomcat có đang chạy không:**
   ```powershell
   netstat -ano | findstr ":9999"
   ```

2. **Kiểm tra ngrok có đang chạy không:**
   ```powershell
   Get-Process -Name ngrok
   ```

3. **Kiểm tra ngrok expose đúng port không:**
   - Mở: http://localhost:4040
   - Xem phần "Forwarding" → Phải là `https://xxx.ngrok-free.app -> http://localhost:9999`

4. **Clear browser cache:**
   - Thử truy cập ở chế độ Incognito/Private
   - Hoặc clear cache và cookies

5. **Kiểm tra firewall:**
   - Đảm bảo port 9999 không bị chặn
   - Đảm bảo ngrok có thể truy cập internet

---

**Đã fix xong! Bây giờ bạn có thể test thanh toán VNPay qua ngrok. 🎉**


