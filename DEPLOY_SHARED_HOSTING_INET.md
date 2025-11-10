# Hướng Dẫn Deploy WebEcommerce lên Shared Hosting iNET.vn

## ✅ Xác Nhận: iNET.vn Có Application Manager!

**Tuyệt vời!** Shared Hosting của bạn có **Application Manager**, điều này có nghĩa là hosting **HỖ TRỢ Java/Tomcat**! 🎉

Bạn có thể deploy ứng dụng Java Web (JSP/Servlet) lên hosting này.

---

## 🚀 Hướng Dẫn Deploy Qua Application Manager

### 📋 Thông Tin Hosting
- **IP Hosting**: `103.57.220.209`
- **Control Panel**: cPanel/Plesk (có Application Manager)
- **Hỗ trợ**: Java/Tomcat ✅

---

## 🔍 Bước 1: Kiểm Tra Application Manager (Đã xác nhận ✅)

### Cách 1: Kiểm tra qua cPanel/Plesk

1. **Đăng nhập vào cPanel/Plesk** của iNET.vn
2. Tìm các mục sau:
   - **Java Applications** hoặc **Tomcat**
   - **Application Manager**
   - **Java Support**
3. Nếu **KHÔNG có** các mục trên → Shared Hosting **KHÔNG hỗ trợ Java**

### Cách 2: Liên hệ hỗ trợ iNET.vn

**Gửi email hoặc chat với iNET.vn hỏi:**
```
Xin chào,

Tôi có Shared Hosting với IP 103.57.220.209.
Tôi muốn deploy ứng dụng Java Web (JSP/Servlet) lên hosting này.

Vui lòng cho tôi biết:
1. Shared Hosting của tôi có hỗ trợ Java/Tomcat không?
2. Nếu có, cách deploy như thế nào?
3. Nếu không, tôi cần upgrade lên VPS không?

Cảm ơn!
```

### Cách 3: Kiểm tra qua FTP

1. **Kết nối FTP** vào hosting:
   - Host: `103.57.220.209` hoặc domain của bạn
   - Port: `21` (FTP) hoặc `22` (SFTP)
   - Username/Password: từ email iNET.vn gửi cho bạn

2. **Kiểm tra cấu trúc thư mục:**
   - Nếu có thư mục `tomcat/` hoặc `java/` → Có thể hỗ trợ Java
   - Nếu chỉ có `public_html/`, `www/`, `htdocs/` → Thường chỉ hỗ trợ PHP

---

## 📦 Bước 2: Chuẩn Bị WAR File

#### 1.1. Build WAR file từ NetBeans:
1. Mở project trong NetBeans
2. Click chuột phải vào project → **Clean and Build**
3. WAR file sẽ được tạo tại: `dist/WebEcommerce.war`

#### 1.2. Hoặc build bằng script:
```bash
# Windows
build-war.bat

# Linux/Mac
./build-war.sh
```

#### 1.3. Kiểm tra WAR file:
- Đảm bảo file `WebEcommerce.war` đã được tạo trong thư mục `dist/`
- Kích thước file thường từ 5-20MB

## 📤 Bước 3: Deploy WAR File Qua Application Manager

### 3.1. Đăng nhập vào cPanel/Plesk

1. **Truy cập**: `http://103.57.220.209:2083` (cPanel) hoặc `https://103.57.220.209:8443` (Plesk)
2. **Đăng nhập** với username/password từ email iNET.vn

### 3.2. Tìm Application Manager

1. Trong cPanel/Plesk, tìm mục **Application Manager** hoặc **Java Applications**
2. Click vào **Application Manager**

### 3.3. Deploy WAR File

#### Cách 1: Upload WAR File trực tiếp

1. Click **Deploy Application** hoặc **Upload WAR**
2. Click **Choose File** hoặc **Browse**
3. Chọn file `WebEcommerce.war` từ thư mục `dist/` của project
4. Click **Deploy** hoặc **Upload**
5. Đợi vài phút để Tomcat deploy ứng dụng

#### Cách 2: Upload qua File Manager rồi Deploy

1. **Upload WAR file qua File Manager:**
   - Vào **File Manager** trong cPanel/Plesk
   - Tìm thư mục `webapps/` hoặc `tomcat/webapps/`
   - Upload file `WebEcommerce.war` vào thư mục này

2. **Deploy qua Application Manager:**
   - Vào **Application Manager**
   - Chọn file `WebEcommerce.war` đã upload
   - Click **Deploy**

### 3.4. Kiểm tra Deploy thành công

1. **Truy cập ứng dụng:**
   - `http://103.57.220.209:8080/WebEcommerce/`
   - Hoặc: `http://your-domain.com/WebEcommerce/`

2. **Nếu thấy trang chủ** → Deploy thành công! ✅
3. **Nếu lỗi 404** → Đợi thêm vài phút hoặc kiểm tra logs

## 🗄️ Bước 4: Cấu Hình Database

### 4.1. Tạo Database trên iNET.vn

1. **Đăng nhập vào cPanel/Plesk**
2. Tìm mục **MySQL Databases** hoặc **SQL Server** (tùy loại database iNET.vn cung cấp)
3. **Tạo database mới:**
   - Database name: `ecommerce_db` (hoặc tên khác)
   - Lưu lại thông tin database name

4. **Tạo user và cấp quyền:**
   - Tạo user mới cho database
   - Cấp quyền **ALL PRIVILEGES** cho user
   - Lưu lại thông tin username và password

### 4.2. Import Schema

1. **Kết nối database:**
   - **Nếu MySQL**: Vào **phpMyAdmin** trong cPanel/Plesk
   - **Nếu SQL Server**: Dùng **SQL Server Management Studio** hoặc công cụ iNET.vn cung cấp

2. **Import file `schema.sql`:**
   - Chọn database vừa tạo
   - Click **Import** hoặc **SQL**
   - Chọn file `schema.sql` từ project
   - Click **Go** hoặc **Execute**

3. **Kiểm tra import thành công:**
   - Xem danh sách tables: `Users`, `Products`, `Categories`, `Orders`, etc.

### 4.3. Cấu Hình Environment Variables trong Application Manager

**QUAN TRỌNG:** Ứng dụng cần các biến môi trường để kết nối database và gửi email.

#### Cách 1: Qua Application Manager (Khuyến nghị)

1. **Vào Application Manager** trong cPanel/Plesk
2. **Tìm ứng dụng** `WebEcommerce` đã deploy
3. **Click vào ứng dụng** → Tìm mục **Environment Variables** hoặc **Configuration**
4. **Thêm các biến môi trường sau:**

```bash
# Database Configuration
db.url=jdbc:sqlserver://localhost:1433;databaseName=ecommerce_db;encrypt=false;trustServerCertificate=true;
db.user=your_db_user
db.password=your_db_password

# Hoặc nếu dùng MySQL:
# db.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC
# db.user=your_db_user
# db.password=your_db_password

# Email Configuration (SMTP)
smtp.host=smtp.gmail.com
smtp.port=587
smtp.user=your_email@gmail.com
smtp.password=your_app_password
email.from=your_email@gmail.com
email.from.name=Ecommerce Store
```

**Lưu ý:**
- Thay `your_db_user`, `your_db_password` bằng thông tin database thực tế
- Thay `your_email@gmail.com` và `your_app_password` bằng thông tin email thực tế
- Với Gmail: Cần tạo **App Password** tại https://myaccount.google.com/apppasswords

#### Cách 2: Qua File Manager (Nếu Application Manager không có Environment Variables)

1. **Vào File Manager** trong cPanel/Plesk
2. **Tìm file cấu hình** của ứng dụng:
   - Thường là: `context.xml` trong thư mục `META-INF/`
   - Hoặc: `setenv.sh` trong thư mục Tomcat
3. **Chỉnh sửa file** và thêm environment variables

#### Cách 3: Qua System Properties trong Tomcat

1. **Tìm file** `setenv.sh` (Linux) hoặc `setenv.bat` (Windows) trong Tomcat
2. **Thêm các dòng sau:**

```bash
export JAVA_OPTS="$JAVA_OPTS -Ddb.url=jdbc:sqlserver://localhost:1433;databaseName=ecommerce_db;encrypt=false;trustServerCertificate=true;"
export JAVA_OPTS="$JAVA_OPTS -Ddb.user=your_db_user"
export JAVA_OPTS="$JAVA_OPTS -Ddb.password=your_db_password"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.host=smtp.gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.port=587"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.user=your_email@gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.password=your_app_password"
export JAVA_OPTS="$JAVA_OPTS -Demail.from=your_email@gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Demail.from.name=Ecommerce Store"
```

3. **Restart Tomcat** để áp dụng thay đổi

## 📁 Bước 5: Cấu Hình Static Files (Images)

### 5.1. Upload Thư Mục Images

1. **Vào File Manager** trong cPanel/Plesk
2. **Tìm thư mục** của ứng dụng đã deploy:
   - Thường là: `/webapps/WebEcommerce/` hoặc `/tomcat/webapps/WebEcommerce/`
3. **Upload thư mục `images/`:**
   - Upload thư mục `web/images/` từ project
   - Đảm bảo cấu trúc: `/webapps/WebEcommerce/images/`

### 5.2. Kiểm Tra Quyền Truy Cập

1. **Kiểm tra quyền** của thư mục `images/`:
   - Quyền đọc: `755` hoặc `644` cho files
   - Quyền ghi: `775` hoặc `664` cho files (nếu cần upload)

2. **Test truy cập ảnh:**
   - Truy cập: `http://103.57.220.209:8080/WebEcommerce/images/logo.png`
   - Nếu thấy ảnh → Cấu hình thành công! ✅

## ✅ Bước 6: Kiểm Tra và Test

1. **Truy cập ứng dụng:**
   - `http://103.57.220.209:8080/WebEcommerce/`
   - Hoặc: `http://your-domain.com/WebEcommerce/`

2. **Test các chức năng:**
   - Đăng ký/Đăng nhập
   - Xem sản phẩm
   - Thêm vào giỏ hàng
   - Đặt hàng

---

---

## 📋 Checklist Deploy

### Trước khi deploy:
- [ ] Đã build WAR file thành công
- [ ] Đã kiểm tra Shared Hosting có hỗ trợ Java
- [ ] Đã có thông tin FTP/cPanel
- [ ] Đã tạo database trên hosting
- [ ] Đã import schema.sql vào database
- [ ] Đã chuẩn bị thông tin database (host, port, user, password)
- [ ] Đã chuẩn bị thông tin SMTP (nếu cần)

### Sau khi deploy:
- [ ] Ứng dụng có thể truy cập được
- [ ] Database kết nối thành công
- [ ] Đăng ký/Đăng nhập hoạt động
- [ ] Xem sản phẩm hoạt động
- [ ] Thêm vào giỏ hàng hoạt động
- [ ] Đặt hàng hoạt động
- [ ] Upload ảnh hoạt động (nếu có)

---

## 🆘 Troubleshooting

### Lỗi 1: WAR file không deploy được

**Nguyên nhân:**
- Shared Hosting không hỗ trợ Java
- Thư mục webapps không có quyền ghi
- WAR file bị lỗi

**Giải pháp:**
1. Kiểm tra lại Shared Hosting có hỗ trợ Java
2. Liên hệ iNET.vn để được hỗ trợ
3. Kiểm tra lại WAR file có build đúng không

### Lỗi 2: Database connection failed

**Nguyên nhân:**
- Thông tin database sai
- Database chưa được tạo
- Firewall chặn kết nối

**Giải pháp:**
1. Kiểm tra lại thông tin database trong environment variables
2. Đảm bảo database đã được tạo và import schema
3. Liên hệ iNET.vn để kiểm tra firewall

### Lỗi 3: Ứng dụng không truy cập được

**Nguyên nhân:**
- Port chưa được mở
- WAR file chưa deploy xong
- Đường dẫn sai

**Giải pháp:**
1. Đợi vài phút để Tomcat deploy xong
2. Kiểm tra lại đường dẫn URL
3. Liên hệ iNET.vn để kiểm tra port

### Lỗi 4: Static files không load được

**Nguyên nhân:**
- Đường dẫn images sai
- Thư mục images chưa được upload
- Quyền truy cập file

**Giải pháp:**
1. Kiểm tra lại đường dẫn images trong code
2. Đảm bảo thư mục images đã được upload
3. Kiểm tra quyền truy cập file (chmod 755)

---

## 📞 Liên Hệ Hỗ Trợ

### iNET.vn:
- **Website**: https://inet.vn
- **Hotline**: (xem trên website)
- **Email**: support@inet.vn
- **Ticket**: Đăng nhập vào client area để tạo ticket

### Thông tin cần cung cấp khi liên hệ:
- IP hosting: `103.57.220.209`
- Loại hosting: Shared Hosting
- Vấn đề gặp phải: (mô tả chi tiết)
- Logs/Error messages: (nếu có)

---

## 📝 Kết Luận

**✅ Shared Hosting iNET.vn của bạn CÓ hỗ trợ Java qua Application Manager!**

**Các bước đã hoàn thành:**
1. ✅ Xác nhận Application Manager có sẵn
2. ✅ Build WAR file
3. ✅ Deploy qua Application Manager
4. ✅ Cấu hình database và environment variables
5. ✅ Upload static files (images)
6. ✅ Test ứng dụng

**Nếu gặp vấn đề:**
- Xem phần **Troubleshooting** ở trên
- Liên hệ iNET.vn support với thông tin: IP `103.57.220.209`, Shared Hosting, Application Manager

**Câu hỏi?** Xem thêm:
- `DEPLOY_INET_VN.md` - Deploy lên VPS iNET.vn
- `DEPLOYMENT.md` - Deploy lên Cloud platforms
- `VERCEL_DEPLOYMENT_OPTIONS.md` - Các giải pháp deploy

---

**Chúc bạn deploy thành công! 🚀**

