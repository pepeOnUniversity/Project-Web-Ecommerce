# Hướng Dẫn: Ngrok Có Chạy Vĩnh Viễn Được Không?

## ⚠️ Câu Trả Lời Ngắn Gọn

**KHÔNG**, link ngrok hiện tại **KHÔNG thể chạy vĩnh viễn** với plan miễn phí. Và **CÓ**, bạn **PHẢI** để máy local bật và các phần mềm sau phải chạy:

## 📋 Những Gì Cần Bật Để Ngrok Hoạt Động

### 1. **Máy tính của bạn PHẢI BẬT**
   - Ngrok chỉ là tunnel, nó không thể hoạt động nếu máy local tắt
   - Khi máy tắt → ngrok dừng → URL không hoạt động

### 2. **Tomcat Server PHẢI CHẠY**
   - Ứng dụng web của bạn phải chạy trên port 9999
   - Nếu Tomcat tắt → ngrok không có gì để forward → lỗi 502

### 3. **Ngrok Process PHẢI CHẠY**
   - File `ngrok.exe` phải đang chạy
   - Nếu đóng ngrok → URL ngrok không hoạt động

## 🔄 Giới Hạn Của Ngrok Free Plan

### 1. **URL Thay Đổi Mỗi Lần Restart**
   - Mỗi lần khởi động lại ngrok → URL mới
   - Ví dụ: 
     - Lần 1: `https://770e95811769.ngrok-free.app`
     - Lần 2: `https://abc123xyz.ngrok-free.app` (khác hoàn toàn)
   - **→ Phải cập nhật lại URL trong VNPay mỗi lần**

### 2. **Giới Hạn Thời Gian**
   - Ngrok free có thể tự động dừng sau một thời gian không hoạt động
   - Hoặc có giới hạn thời gian chạy liên tục

### 3. **Giới Hạn Bandwidth**
   - Free plan có giới hạn số lượng request/bandwidth
   - Vượt quá → bị chặn tạm thời

### 4. **Trang Cảnh Báo**
   - Mỗi lần truy cập lần đầu → hiển thị trang cảnh báo
   - Phải click "Visit Site" mới vào được

## ✅ Giải Pháp Để Chạy "Vĩnh Viễn"

### Option 1: Ngrok Pro Plan (Có Phí)

**Ưu điểm:**
- ✅ **Fixed Domain**: URL không đổi (ví dụ: `https://yourname.ngrok.io`)
- ✅ Không giới hạn thời gian
- ✅ Không có trang cảnh báo
- ✅ Bandwidth cao hơn

**Nhược điểm:**
- ❌ Phải trả phí (~$8/tháng)
- ❌ Vẫn cần máy local bật

**Cách setup:**
1. Đăng ký ngrok Pro tại: https://ngrok.com/pricing
2. Cấu hình fixed domain trong dashboard
3. Sử dụng lệnh:
   ```powershell
   ngrok http 9999 --domain=yourname.ngrok.io
   ```

### Option 2: Deploy Lên Cloud Server (Khuyến Nghị)

**Ưu điểm:**
- ✅ Chạy 24/7 không cần máy local
- ✅ URL cố định (ví dụ: `https://yourapp.herokuapp.com`)
- ✅ Không cần ngrok
- ✅ Nhiều nền tảng miễn phí

**Các nền tảng miễn phí:**
1. **Heroku** (Free tier có giới hạn)
   - Deploy Java web app
   - URL: `https://yourapp.herokuapp.com`

2. **Railway** (Free tier)
   - Hỗ trợ Java/Tomcat
   - URL: `https://yourapp.railway.app`

3. **Render** (Free tier)
   - Hỗ trợ Java web services
   - URL: `https://yourapp.onrender.com`

4. **AWS/Google Cloud/Azure** (Có free tier)
   - Cần setup phức tạp hơn
   - Mạnh mẽ và ổn định

**Cách deploy:**
- Export WAR file từ NetBeans
- Upload lên cloud platform
- Cấu hình database (dùng cloud database)
- Cập nhật VNPay callback URL

### Option 3: VPS (Virtual Private Server)

**Ưu điểm:**
- ✅ Chạy 24/7
- ✅ Toàn quyền kiểm soát
- ✅ URL cố định (dùng domain riêng)

**Nhược điểm:**
- ❌ Phải trả phí (~$5-10/tháng)
- ❌ Cần kiến thức quản trị server

**Các nhà cung cấp VPS rẻ:**
- DigitalOcean ($5/tháng)
- Vultr ($5/tháng)
- Linode ($5/tháng)
- AWS Lightsail ($3.5/tháng)

## 🎯 Khuyến Nghị Cho Project Của Bạn

### Nếu Chỉ Test/Demo:
- ✅ Dùng ngrok free (như hiện tại)
- ✅ Chỉ bật khi cần test
- ✅ Chấp nhận URL thay đổi

### Nếu Cần Chạy Lâu Dài:
- ✅ **Deploy lên Heroku/Railway** (miễn phí)
- ✅ Hoặc mua VPS rẻ ($5/tháng)
- ✅ Hoặc nâng cấp ngrok Pro ($8/tháng)

## 📝 Checklist Để Ngrok Hoạt Động

Khi muốn test VNPay, đảm bảo:

- [ ] Máy tính của bạn đang bật
- [ ] Tomcat đang chạy trên port 9999
- [ ] Ngrok đang chạy (chạy script `start-ngrok.ps1`)
- [ ] Đã copy URL ngrok mới và cập nhật vào:
  - System property: `-Dvnpay.ngrok.url=https://xxx.ngrok-free.app`
  - Hoặc file `config.properties`: `ngrok.url=https://xxx.ngrok-free.app`
- [ ] Đã cập nhật callback URL trong VNPay dashboard (nếu cần)

## 🔍 Kiểm Tra Ngrok Có Đang Chạy

### Cách 1: Kiểm tra Process
```powershell
Get-Process -Name ngrok -ErrorAction SilentlyContinue
```

### Cách 2: Kiểm tra Dashboard
Mở trình duyệt: `http://localhost:4040`

### Cách 3: Test URL
```powershell
curl https://your-ngrok-url.ngrok-free.app/WebEcommerce
```

## ⚡ Script Tự Động Chạy Ngrok Khi Khởi Động Máy

Nếu muốn ngrok tự động chạy khi bật máy, bạn có thể:

1. Tạo Windows Task Scheduler
2. Hoặc thêm vào Startup folder
3. Hoặc tạo Windows Service

**Lưu ý:** Vẫn cần máy bật và Tomcat chạy!

## 📚 Tài Liệu Tham Khảo

- Ngrok Documentation: https://ngrok.com/docs
- Ngrok Pricing: https://ngrok.com/pricing
- Heroku Java Guide: https://devcenter.heroku.com/articles/getting-started-with-java
- Railway Docs: https://docs.railway.app/



