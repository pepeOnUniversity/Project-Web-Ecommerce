# 🚀 Quick Start: Chạy Ngrok Local

## Cách Nhanh Nhất (3 Bước)

### 1️⃣ Đảm Bảo Tomcat Đang Chạy
- Chạy ứng dụng trên NetBeans hoặc start Tomcat
- Ứng dụng phải chạy trên port **9999**

### 2️⃣ Chạy Script Ngrok

**Cách 1: Dùng file .bat (Khuyến nghị - Không cần cài gì thêm)**
```cmd
start-ngrok.bat
```
Hoặc double-click vào file `start-ngrok.bat`

**Cách 2: Dùng PowerShell trực tiếp**
```powershell
powershell -ExecutionPolicy Bypass -File .\start-ngrok.ps1
```

### 3️⃣ Xong! URL Đã Được Tự Động Cập Nhật
- Script sẽ tự động:
  - ✅ Khởi động ngrok
  - ✅ Lấy URL ngrok
  - ✅ Cập nhật vào `config.properties`
- Bạn chỉ cần **giữ cửa sổ PowerShell mở** khi test

## 📋 Checklist

- [ ] Tomcat đang chạy trên port 9999
- [ ] Đã chạy script `start-ngrok.ps1`
- [ ] Đã thấy message "[OK] NGROK DA KHOI DONG THANH CONG!"
- [ ] Đã thấy message "[OK] Da cap nhat ngrok.url=... vao config.properties"
- [ ] Giữ cửa sổ PowerShell mở

## 🔍 Kiểm Tra

### Xem URL Ngrok
- Mở: http://localhost:4040 (ngrok dashboard)
- Hoặc xem trong output của script

### Test URL
- Mở trình duyệt: `https://YOUR-NGROK-URL.ngrok-free.app/WebEcommerce`
- **Lần đầu sẽ có trang cảnh báo của ngrok** → Click **"Visit Site"** hoặc **"Continue"**
- **Đây KHÔNG PHẢI là lỗi!** Đây là trang cảnh báo bình thường của ngrok free tier
- VNPay sẽ tự động bypass trang này khi gửi callback

## ⚠️ Lưu Ý

1. **URL thay đổi mỗi lần restart ngrok** → Script tự động cập nhật
2. **Phải giữ cửa sổ PowerShell mở** → Nếu đóng, ngrok sẽ dừng
3. **Máy phải bật** → Tắt máy = ngrok dừng

## 🛑 Dừng Ngrok

- Nhấn `Ctrl+C` trong cửa sổ PowerShell/CMD
- Hoặc đóng cửa sổ

## ⚠️ Troubleshooting

### Nếu Gặp Lỗi "Yêu Cầu Cài Tomcat" Trên Trình Duyệt

**Vấn đề:** Khi truy cập URL ngrok trên trình duyệt, bạn thấy trang yêu cầu cài Tomcat.

**Nguyên nhân có thể:**
1. **Ngrok Warning Page (Phổ biến nhất)**
   - Ngrok free tier hiển thị trang cảnh báo lần đầu truy cập
   - Trang này có thể trông giống như yêu cầu cài đặt
   - **Giải pháp:** Click nút **"Visit Site"** hoặc **"Continue"** để tiếp tục
   - VNPay sẽ tự động bypass trang này khi gửi callback

2. **Tomcat Chưa Chạy**
   - Nếu Tomcat không chạy, ngrok không thể forward request
   - **Giải pháp:** 
     - Khởi động Tomcat trong NetBeans
     - Hoặc start Tomcat service
     - Kiểm tra: `http://localhost:9999/WebEcommerce` (hoặc port của bạn)

3. **Port Không Đúng**
   - Script mặc định expose port 9999
   - Nếu Tomcat chạy port khác (ví dụ: 8080), cần sửa script
   - **Giải pháp:** Sửa dòng 107 trong `start-ngrok.ps1`: `$port = 8080`

### Nếu PowerShell Yêu Cầu Cài Đặt

**Giải pháp:**
1. **Dùng file `.bat` thay vì `.ps1`** (khuyến nghị):
   ```cmd
   start-ngrok.bat
   ```

2. **Hoặc chạy PowerShell với Bypass:**
   ```powershell
   powershell -ExecutionPolicy Bypass -File .\start-ngrok.ps1
   ```

3. **Hoặc set Execution Policy một lần (nếu cần):**
   ```powershell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```

## 📚 Xem Thêm

- Chi tiết đầy đủ: `HUONG_DAN_CHAY_NGROK_LOCAL.md`
- Về ngrok vĩnh viễn: `NGROK_PERMANENT_SETUP.md`

