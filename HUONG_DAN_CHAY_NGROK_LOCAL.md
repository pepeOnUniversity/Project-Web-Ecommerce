# Hướng Dẫn Chạy Ngrok Local

## 📋 Tổng Quan

Ngrok giúp expose localhost của bạn ra internet, cho phép VNPay gửi callback về server local khi test thanh toán.

## 🚀 Cách 1: Sử Dụng Script Tự Động (Khuyến Nghị)

### Bước 1: Đảm Bảo Đã Có Ngrok

1. File `ngrok.exe` đã có trong thư mục project (hoặc đã cài đặt và thêm vào PATH)
2. Authtoken đã được cấu hình trong `src/java/config.properties`

### Bước 2: Đảm Bảo Tomcat Đang Chạy

- Tomcat phải chạy trên port **9999** (hoặc port mà bạn đã cấu hình)
- Kiểm tra: Mở trình duyệt và truy cập `http://localhost:9999/WebEcommerce`

### Bước 3: Chạy Script

Mở PowerShell trong thư mục project và chạy:

```powershell
.\start-ngrok.ps1
```

**Hoặc nếu ngrok đang chạy và muốn restart:**

```powershell
Stop-Process -Name ngrok -Force -ErrorAction SilentlyContinue; Start-Sleep -Seconds 2; .\start-ngrok.ps1
```

### Bước 4: Copy URL và Cấu Hình

Sau khi script chạy, bạn sẽ thấy output như sau:

```
=============================================
[OK] NGROK DA KHOI DONG THANH CONG!
=============================================

Public URL: https://770e95811769.ngrok-free.app

Cac URL callback cho VNPay:
  Return URL: https://770e95811769.ngrok-free.app/WebEcommerce/vnpay-return
  IPN URL:    https://770e95811769.ngrok-free.app/WebEcommerce/vnpay-ipn
```

**Copy URL này và cấu hình theo một trong các cách sau:**

#### Cách A: Cập Nhật config.properties (Dễ Nhất)

Mở file `src/java/config.properties` và cập nhật:

```properties
ngrok.url=https://770e95811769.ngrok-free.app
```

#### Cách B: Set System Property (Khi Chạy Tomcat)

Trong NetBeans:
1. Right-click project → **Properties**
2. Chọn **Run** → **VM Options**
3. Thêm: `-Dvnpay.ngrok.url=https://770e95811769.ngrok-free.app`

Hoặc khi chạy Tomcat từ command line:
```bash
-Dvnpay.ngrok.url=https://770e95811769.ngrok-free.app
```

### Bước 5: Giữ Script Chạy

**QUAN TRỌNG:** Để ngrok hoạt động, bạn **PHẢI** giữ cửa sổ PowerShell mở. Nếu đóng cửa sổ, ngrok sẽ dừng.

## 🔧 Cách 2: Chạy Thủ Công

### Bước 1: Mở PowerShell hoặc Command Prompt

### Bước 2: Di Chuyển Đến Thư Mục Chứa ngrok.exe

```powershell
cd D:\FPT_University\semester4\Final_Project\WebEcommerce
```

### Bước 3: Config Authtoken (Chỉ Cần Làm 1 Lần)

```powershell
.\ngrok.exe config add-authtoken 30CX95zenYVZutb0DfKN0C6Hh4T_5HWchnbHVNr91TU8SUMwf
```

### Bước 4: Chạy Ngrok

```powershell
.\ngrok.exe http 9999
```

**Lưu ý:** Thay `9999` bằng port mà Tomcat của bạn đang chạy.

### Bước 5: Copy URL

Bạn sẽ thấy output như sau:

```
ngrok                                                                        

Session Status                online
Account                       Your Name (Plan: Free)
Version                       3.x.x
Region                        United States (us)
Latency                       45ms
Web Interface                 http://127.0.0.1:4040
Forwarding                    https://770e95811769.ngrok-free.app -> http://localhost:9999

Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
```

Copy URL: `https://770e95811769.ngrok-free.app`

### Bước 6: Cấu Hình URL

Làm theo **Bước 4** ở trên để cấu hình URL vào ứng dụng.

## ✅ Kiểm Tra Ngrok Có Hoạt Động

### Cách 1: Kiểm Tra Dashboard

Mở trình duyệt và truy cập: **http://localhost:4040**

Bạn sẽ thấy:
- Tất cả requests đi qua ngrok
- Request/Response details
- URL hiện tại

### Cách 2: Test URL Trực Tiếp

Mở trình duyệt và truy cập URL ngrok:

```
https://770e95811769.ngrok-free.app/WebEcommerce
```

**Lưu ý:** Lần đầu tiên sẽ có trang cảnh báo của ngrok, click **"Visit Site"** để tiếp tục.

### Cách 3: Kiểm Tra Process

```powershell
Get-Process -Name ngrok -ErrorAction SilentlyContinue
```

Nếu có output → ngrok đang chạy.

### Cách 4: Lấy URL Từ API

```powershell
curl http://localhost:4040/api/tunnels
```

Hoặc trong PowerShell:

```powershell
Invoke-RestMethod -Uri "http://localhost:4040/api/tunnels" | ConvertTo-Json
```

## 🔄 Quy Trình Test VNPay Với Ngrok

### 1. Khởi Động Tomcat
- Chạy ứng dụng trên NetBeans hoặc start Tomcat
- Đảm bảo ứng dụng chạy trên port 9999

### 2. Khởi Động Ngrok
```powershell
.\start-ngrok.ps1
```

### 3. Copy URL Ngrok
- Copy URL từ output (ví dụ: `https://770e95811769.ngrok-free.app`)

### 4. Cấu Hình URL
- Cập nhật `config.properties`: `ngrok.url=https://770e95811769.ngrok-free.app`
- Hoặc set system property: `-Dvnpay.ngrok.url=https://770e95811769.ngrok-free.app`

### 5. Restart Tomcat (Nếu Cần)
- Nếu đã set system property, cần restart Tomcat để áp dụng

### 6. Test Thanh Toán
- Vào trang checkout
- Chọn thanh toán VNPay
- Thực hiện thanh toán test

### 7. Kiểm Tra Callback
- Mở ngrok dashboard: http://localhost:4040
- Xem requests từ VNPay đến callback URL

## ⚠️ Lưu Ý Quan Trọng

### 1. URL Thay Đổi Mỗi Lần Restart
- Mỗi lần khởi động lại ngrok → URL mới
- **→ Phải cập nhật lại URL trong config mỗi lần**

### 2. Phải Giữ Ngrok Chạy
- Đóng cửa sổ PowerShell → ngrok dừng
- **→ Phải giữ cửa sổ mở khi test**

### 3. Máy Phải Bật
- Ngrok chỉ hoạt động khi máy tính bật
- Tắt máy → ngrok dừng

### 4. Port Phải Đúng
- Script mặc định expose port **9999**
- Nếu Tomcat chạy port khác, sửa trong script dòng 105:
  ```powershell
  $port = 9999  # Thay bằng port của bạn
  ```

### 5. Trang Cảnh Báo Ngrok
- Lần đầu truy cập URL ngrok → có trang cảnh báo
- Click **"Visit Site"** để tiếp tục
- VNPay sẽ tự động bypass trang này

## 🛠️ Troubleshooting

### Ngrok Không Chạy Được

**Lỗi: "ngrok.exe not found"**
- Đảm bảo file `ngrok.exe` trong thư mục project
- Hoặc đã cài đặt và thêm vào PATH

**Lỗi: "authtoken invalid"**
- Kiểm tra authtoken trong `config.properties`
- Hoặc config lại: `ngrok config add-authtoken YOUR_TOKEN`

**Lỗi: "port already in use"**
- Kiểm tra port 9999 có đang được sử dụng không
- Hoặc đổi port trong script

### Không Nhận Được Callback Từ VNPay

**Kiểm tra:**
1. Ngrok có đang chạy không? → Mở http://localhost:4040
2. URL trong config có đúng không?
3. Tomcat có đang chạy trên port đúng không?
4. Xem ngrok dashboard có request từ VNPay không?

**Giải pháp:**
- Restart ngrok và cập nhật lại URL
- Kiểm tra log trong ngrok dashboard
- Kiểm tra log trong Tomcat console

### URL Ngrok Thay Đổi

**Vấn đề:** Mỗi lần restart ngrok, URL thay đổi

**Giải pháp:**
1. **Tạm thời:** Cập nhật lại URL mỗi lần
2. **Lâu dài:** 
   - Nâng cấp ngrok Pro (có fixed domain)
   - Hoặc deploy lên cloud server

### Ngrok Tự Động Dừng

**Nguyên nhân:**
- Free plan có giới hạn thời gian
- Hoặc không có traffic trong thời gian dài

**Giải pháp:**
- Restart ngrok khi cần
- Hoặc nâng cấp lên Pro plan

## 📝 Checklist Trước Khi Test VNPay

- [ ] Tomcat đang chạy trên port 9999
- [ ] Ngrok đang chạy (kiểm tra: http://localhost:4040)
- [ ] Đã copy URL ngrok mới
- [ ] Đã cập nhật URL vào `config.properties` hoặc system property
- [ ] Đã restart Tomcat (nếu cần)
- [ ] Đã test URL ngrok trong trình duyệt
- [ ] Đã mở ngrok dashboard để theo dõi requests

## 🎯 Tóm Tắt Nhanh

```powershell
# 1. Chạy script
.\start-ngrok.ps1

# 2. Copy URL (ví dụ: https://770e95811769.ngrok-free.app)

# 3. Cập nhật config.properties
ngrok.url=https://770e95811769.ngrok-free.app

# 4. Restart Tomcat (nếu cần)

# 5. Test thanh toán VNPay

# 6. Xem requests tại: http://localhost:4040
```

## 📚 Tài Liệu Tham Khảo

- Ngrok Documentation: https://ngrok.com/docs
- Ngrok Dashboard: https://dashboard.ngrok.com/
- VNPay Sandbox: https://sandbox.vnpayment.vn/



