# 🔧 Fix Lỗi Deploy Tomcat

## ❌ Vấn Đề 1: "Application already exists at path [/]"

Khi Clean and Build trong NetBeans, bạn gặp lỗi:
```
FAIL - Application already exists at path [/]
The module has not been deployed.
```

### 🔍 Nguyên Nhân

**Có ứng dụng khác đã được deploy ở root path `/` trên Tomcat!**

Thư mục `ROOT` trong `webapps` của Tomcat là ứng dụng mặc định chạy ở root path. Khi NetBeans cố deploy WebEcommerce vào root path, nó bị conflict với ứng dụng ROOT.

### ✅ Giải Pháp Nhanh

**Chạy script fix tự động:**
```powershell
.\fix-deployment-conflict.ps1
```

Script sẽ:
- Tìm thư mục webapps của Tomcat
- Backup và xóa/đổi tên thư mục ROOT
- Hướng dẫn các bước tiếp theo

**Hoặc làm thủ công:**

1. **Stop Tomcat hoàn toàn** (trong NetBeans hoặc Task Manager)

2. **Tìm thư mục webapps của Tomcat:**
   - Thường ở: `C:\Program Files\Apache Software Foundation\Tomcat 10.1_Tomcat10.1\webapps`

3. **Backup và xóa thư mục ROOT:**
   ```powershell
   # Backup
   Rename-Item "C:\Program Files\Apache Software Foundation\Tomcat 10.1_Tomcat10.1\webapps\ROOT" "ROOT_backup"
   
   # Hoặc xóa hoàn toàn (nếu không cần)
   Remove-Item "C:\Program Files\Apache Software Foundation\Tomcat 10.1_Tomcat10.1\webapps\ROOT" -Recurse -Force
   ```

4. **Mở lại NetBeans và deploy:**
   - Clean and Build (Shift+F11)
   - Start Tomcat (F6)

---

## ❌ Vấn Đề 2: "Yêu Cầu Cài Đặt Tomcat" Khi Truy Cập Qua Ngrok

Khi chạy `start-ngrok.ps1` và truy cập URL ngrok trên trình duyệt, bạn thấy:
- Trang yêu cầu cài đặt Tomcat
- Hoặc trang mặc định của Tomcat (Apache Tomcat/10.1.17)
- **KHÔNG thấy ứng dụng WebEcommerce**

### 🔍 Nguyên Nhân

**Ứng dụng chưa được deploy đúng cách lên Tomcat!**

Khi bạn truy cập `http://localhost:9999/` hoặc ngrok URL, Tomcat trả về trang mặc định của nó thay vì ứng dụng WebEcommerce.

## ✅ Giải Pháp

### Bước 1: Kiểm Tra Ứng Dụng Có Đang Chạy Không

Mở trình duyệt và truy cập:
```
http://localhost:9999/
```

**Nếu thấy:**
- ✅ Trang chủ WebEcommerce → Ứng dụng đã chạy, có thể tiếp tục với ngrok
- ❌ Trang mặc định Tomcat (Apache Tomcat/10.1.17) → Ứng dụng chưa được deploy

### Bước 2: Deploy Ứng Dụng Trong NetBeans

Nếu ứng dụng chưa chạy, làm theo các bước sau:

1. **Mở NetBeans**
   - Đảm bảo project WebEcommerce đang mở

2. **Stop Tomcat** (nếu đang chạy)
   - Click nút **Stop** trên toolbar
   - Hoặc nhấn **Shift+F5**

3. **Clean and Build Project**
   - Click chuột phải vào project → **Clean and Build**
   - Hoặc nhấn **Shift+F11**
   - Đợi đến khi build xong (thấy "BUILD SUCCESSFUL")

4. **Start Tomcat**
   - Click nút **Run** trên toolbar
   - Hoặc nhấn **F6**
   - Đợi đến khi Tomcat khởi động xong (thấy "Server startup in XXXX ms")

5. **Kiểm Tra Lại**
   - Mở trình duyệt: `http://localhost:9999/`
   - Phải thấy trang chủ WebEcommerce (không phải trang Tomcat mặc định)

### Bước 3: Chạy Lại Ngrok

Sau khi ứng dụng đã chạy đúng:

1. **Chạy script ngrok:**
   ```powershell
   .\start-ngrok.ps1
   ```

2. **Script sẽ tự động:**
   - ✅ Kiểm tra Tomcat có chạy không
   - ✅ Kiểm tra ứng dụng có được deploy không
   - ✅ Khởi động ngrok
   - ✅ Hiển thị URL ngrok

3. **Truy cập URL ngrok:**
   ```
   https://YOUR-NGROK-URL.ngrok-free.app/
   ```

   **Lưu ý:** Lần đầu sẽ có trang cảnh báo của ngrok → Click **"Visit Site"** để tiếp tục.

## 🔍 Kiểm Tra Nhanh

### Test Localhost
```powershell
Invoke-WebRequest -Uri "http://localhost:9999/" -UseBasicParsing
```
→ Phải trả về Status 200 và content có chứa "WebEcommerce" hoặc "Home"

### Test Ngrok
```powershell
Invoke-WebRequest -Uri "https://YOUR-NGROK-URL.ngrok-free.app/" -UseBasicParsing -Headers @{"ngrok-skip-browser-warning"="true"}
```
→ Phải trả về Status 200 và content có chứa "WebEcommerce" hoặc "Home"

## ⚠️ Lưu Ý Quan Trọng

1. **Luôn deploy ứng dụng trước khi chạy ngrok**
   - Ngrok chỉ forward request đến Tomcat
   - Nếu ứng dụng chưa deploy, ngrok sẽ trả về trang mặc định của Tomcat

2. **Restart Tomcat sau khi sửa code**
   - Nếu bạn sửa code, phải Clean and Build lại
   - Sau đó restart Tomcat để áp dụng thay đổi

3. **Kiểm tra context path**
   - Ứng dụng hiện tại chạy ở root context (`/`)
   - Không cần thêm `/WebEcommerce` vào URL

## 🐛 Troubleshooting

### Nếu Vẫn Gặp Lỗi "Application already exists at path [/]"

1. **Đảm bảo Tomcat đã được stop hoàn toàn:**
   ```powershell
   # Kiểm tra process Tomcat
   Get-Process | Where-Object {$_.ProcessName -like "*tomcat*" -or $_.ProcessName -like "*java*"} | Select-Object ProcessName, Id
   
   # Nếu có, kill process
   Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
   ```

2. **Xóa thư mục build trong project:**
   - Xóa thư mục `build` trong project root
   - Xóa thư mục `dist` trong project root

3. **Xóa cache NetBeans:**
   - Đóng NetBeans
   - Xóa thư mục: `C:\Users\YOUR_USER\AppData\Local\NetBeans\Cache`
   - Mở lại NetBeans

4. **Deploy lại từ đầu:**
   - Clean and Build (Shift+F11)
   - Stop Tomcat (nếu đang chạy)
   - Start Tomcat (F6)

### Nếu Vẫn Thấy Trang Tomcat Mặc Định

1. **Kiểm tra Tomcat có đang chạy không:**
   ```powershell
   netstat -ano | findstr ":9999"
   ```
   → Phải thấy port 9999 đang LISTENING

2. **Kiểm tra NetBeans có deploy không:**
   - Xem tab "Services" trong NetBeans
   - Mở rộng "Servers" → "Tomcat"
   - Xem có project WebEcommerce không

3. **Kiểm tra log Tomcat:**
   - Trong NetBeans: Tab "Output" → "Tomcat"
   - Tìm lỗi khi deploy (nếu có)

4. **Kiểm tra thư mục webapps:**
   - Đảm bảo không có thư mục `ROOT` (trừ khi đã backup)
   - Kiểm tra có thư mục nào khác đang chiếm root path không

### Nếu Gặp Lỗi 404

- Đảm bảo `context.xml` có `path="/"`
- Đảm bảo `web.xml` có đúng servlet mappings
- Kiểm tra log Tomcat để xem có lỗi gì không

### Nếu Ngrok Không Hoạt Động

1. **Kiểm tra ngrok có chạy không:**
   ```powershell
   Get-Process -Name ngrok
   ```

2. **Kiểm tra ngrok dashboard:**
   - Mở: http://localhost:4040
   - Xem phần "Forwarding" → Phải là `https://xxx.ngrok-free.app -> http://localhost:9999`

3. **Kiểm tra firewall:**
   - Đảm bảo port 9999 không bị chặn
   - Đảm bảo ngrok có thể truy cập internet

---

**Sau khi fix xong, bạn có thể test thanh toán VNPay qua ngrok! 🎉**

