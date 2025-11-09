# Hướng Dẫn Deploy WebEcommerce lên Production

## Tổng Quan

Dự án WebEcommerce là một ứng dụng Java Web (JSP/Servlet) có thể deploy lên các server production như:
- **Tomcat Server** (Apache Tomcat 9.x/10.x)
- **Cloud Platforms**: AWS, Azure, Google Cloud Platform
- **VPS/Cloud Server**: DigitalOcean, Linode, Vultr, etc.

## Yêu Cầu Hệ Thống

### 1. Java Runtime Environment
- **Java 17** hoặc cao hơn
- JDK/JRE phải được cài đặt trên server

### 2. Application Server
- **Apache Tomcat 9.x** hoặc **10.x** (khuyến nghị)
- Hoặc bất kỳ servlet container nào hỗ trợ Jakarta EE 9+

### 3. Database
- **Microsoft SQL Server 2019+** hoặc **Azure SQL Database**
- Database phải được tạo và schema đã được import

### 4. Network
- Port 80/443 (HTTP/HTTPS) mở cho web traffic
- Port 1433 (SQL Server) hoặc port tùy chỉnh cho database connection

## Các Bước Deploy

### Bước 1: Chuẩn Bị Database

1. **Tạo database trên SQL Server:**
```sql
CREATE DATABASE EcommerceDB;
```

2. **Import schema:**
```bash
sqlcmd -S your-server -d EcommerceDB -i schema.sql
```

3. **Tạo user và cấp quyền** (khuyến nghị cho production):
```sql
CREATE LOGIN ecommerce_user WITH PASSWORD = 'StrongPassword123!';
USE EcommerceDB;
CREATE USER ecommerce_user FOR LOGIN ecommerce_user;
ALTER ROLE db_owner ADD MEMBER ecommerce_user;
```

### Bước 2: Build WAR File

1. **Build project:**
```bash
# Nếu dùng Ant
ant dist

# Hoặc build từ IDE (NetBeans/IntelliJ/Eclipse)
# File sẽ được tạo tại: dist/WebEcommerce.war
```

2. **Kiểm tra WAR file:**
- Đảm bảo file `WebEcommerce.war` đã được tạo trong thư mục `dist/`

### Bước 3: Cấu Hình Environment Variables

**QUAN TRỌNG:** Trước khi deploy, bạn PHẢI cấu hình các biến môi trường sau:

#### Database Configuration
```bash
# Windows (setenv.bat trong Tomcat/bin)
set DB_URL=jdbc:sqlserver://your-db-server:1433;databaseName=EcommerceDB;encrypt=true;trustServerCertificate=false;
set DB_USER=ecommerce_user
set DB_PASSWORD=StrongPassword123!

# Linux/Mac (setenv.sh trong Tomcat/bin)
export DB_URL="jdbc:sqlserver://your-db-server:1433;databaseName=EcommerceDB;encrypt=true;trustServerCertificate=false;"
export DB_USER="ecommerce_user"
export DB_PASSWORD="StrongPassword123!"
```

#### Email Configuration (SMTP)
```bash
# Windows
set SMTP_HOST=smtp.gmail.com
set SMTP_PORT=587
set SMTP_USER=your-email@gmail.com
set SMTP_PASSWORD=your-app-password
set EMAIL_FROM=your-email@gmail.com
set EMAIL_FROM_NAME=Ecommerce Store

# Linux/Mac
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"
export SMTP_USER="your-email@gmail.com"
export SMTP_PASSWORD="your-app-password"
export EMAIL_FROM="your-email@gmail.com"
export EMAIL_FROM_NAME="Ecommerce Store"
```

#### Image Storage (Optional)
```bash
# Nếu muốn lưu ảnh ở thư mục khác (không phải trong webapp)
export ECOMMERCE_IMAGES_PATH="/var/www/ecommerce/images"
export ECOMMERCE_IMAGES_URL="https://yourdomain.com/images"
```

### Bước 4: Deploy lên Tomcat

#### Cách 1: Deploy qua Tomcat Manager (Khuyến nghị)

1. **Truy cập Tomcat Manager:**
   - URL: `http://your-server:8080/manager/html`
   - Đăng nhập với user có quyền manager

2. **Upload WAR file:**
   - Chọn file `WebEcommerce.war`
   - Click "Deploy"

#### Cách 2: Copy WAR file trực tiếp

1. **Copy WAR file vào webapps:**
```bash
# Windows
copy dist\WebEcommerce.war C:\Program Files\Apache Software Foundation\Tomcat 10.0\webapps\

# Linux/Mac
cp dist/WebEcommerce.war /opt/tomcat/webapps/
```

2. **Tomcat sẽ tự động deploy** khi phát hiện WAR file mới

#### Cách 3: Deploy qua Context File

1. **Tạo file context.xml:**
```xml
<!-- Tạo file: $CATALINA_BASE/conf/Catalina/localhost/WebEcommerce.xml -->
<Context path="/WebEcommerce" docBase="/path/to/WebEcommerce.war">
    <Environment name="DB_URL" value="jdbc:sqlserver://..." type="java.lang.String"/>
    <Environment name="DB_USER" value="ecommerce_user" type="java.lang.String"/>
    <Environment name="DB_PASSWORD" value="..." type="java.lang.String"/>
</Context>
```

### Bước 5: Cấu Hình Tomcat

#### 1. Tạo file setenv.bat/setenv.sh

**Windows (setenv.bat):**
```batch
@echo off
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MaxPermSize=256m
set JAVA_OPTS=%JAVA_OPTS% -Ddb.url=jdbc:sqlserver://your-db-server:1433;databaseName=EcommerceDB;encrypt=true;trustServerCertificate=false;
set JAVA_OPTS=%JAVA_OPTS% -Ddb.user=ecommerce_user
set JAVA_OPTS=%JAVA_OPTS% -Ddb.password=StrongPassword123!
set JAVA_OPTS=%JAVA_OPTS% -Dsmtp.host=smtp.gmail.com
set JAVA_OPTS=%JAVA_OPTS% -Dsmtp.port=587
set JAVA_OPTS=%JAVA_OPTS% -Dsmtp.user=your-email@gmail.com
set JAVA_OPTS=%JAVA_OPTS% -Dsmtp.password=your-app-password
set JAVA_OPTS=%JAVA_OPTS% -Demail.from=your-email@gmail.com
set JAVA_OPTS=%JAVA_OPTS% -Demail.from.name=Ecommerce Store
```

**Linux/Mac (setenv.sh):**
```bash
#!/bin/sh
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:MaxPermSize=256m"
export JAVA_OPTS="$JAVA_OPTS -Ddb.url=jdbc:sqlserver://your-db-server:1433;databaseName=EcommerceDB;encrypt=true;trustServerCertificate=false;"
export JAVA_OPTS="$JAVA_OPTS -Ddb.user=ecommerce_user"
export JAVA_OPTS="$JAVA_OPTS -Ddb.password=StrongPassword123!"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.host=smtp.gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.port=587"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.user=your-email@gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.password=your-app-password"
export JAVA_OPTS="$JAVA_OPTS -Demail.from=your-email@gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Demail.from.name=Ecommerce Store"
```

**Lưu file vào:** `$CATALINA_HOME/bin/setenv.bat` hoặc `setenv.sh`

#### 2. Cấu hình server.xml (Optional)

Nếu muốn chạy trên port 80 hoặc cấu hình HTTPS:

```xml
<!-- $CATALINA_HOME/conf/server.xml -->
<Connector port="80" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

### Bước 6: Cấu Hình Firewall

```bash
# Linux (UFW)
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp  # Nếu dùng port 8080

# Windows Firewall
# Mở port 80, 443, 8080 qua Windows Firewall Settings
```

### Bước 7: Cấu Hình Reverse Proxy (Nginx/Apache) - Optional

Nếu muốn dùng Nginx làm reverse proxy:

```nginx
# /etc/nginx/sites-available/ecommerce
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        proxy_pass http://localhost:8080/WebEcommerce;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Bước 8: Kiểm Tra Deploy

1. **Khởi động Tomcat:**
```bash
# Windows
C:\Program Files\Apache Software Foundation\Tomcat 10.0\bin\startup.bat

# Linux/Mac
/opt/tomcat/bin/startup.sh
```

2. **Kiểm tra log:**
```bash
# Xem log để đảm bảo không có lỗi
tail -f $CATALINA_HOME/logs/catalina.out
```

3. **Truy cập ứng dụng:**
   - URL: `http://your-server:8080/WebEcommerce`
   - Hoặc: `http://yourdomain.com` (nếu đã cấu hình domain)

4. **Test các chức năng:**
   - Đăng ký tài khoản mới
   - Đăng nhập
   - Xem sản phẩm
   - Thêm vào giỏ hàng
   - Đặt hàng

## Deploy lên Cloud Platforms

### AWS Elastic Beanstalk

1. **Tạo WAR file**
2. **Upload lên Elastic Beanstalk:**
   - Tạo môi trường Java
   - Upload WAR file
   - Cấu hình environment variables trong Console

### Azure App Service

1. **Tạo App Service Plan**
2. **Deploy WAR file:**
```bash
az webapp deploy --resource-group myResourceGroup --name myAppName --src-path WebEcommerce.war
```

3. **Cấu hình Application Settings** (Environment Variables)

### Google Cloud Platform

1. **Deploy lên App Engine:**
```yaml
# app.yaml
runtime: java17
service: ecommerce
```

2. **Deploy:**
```bash
gcloud app deploy WebEcommerce.war
```

## Bảo Mật Production

### 1. Database Security
- ✅ Sử dụng user riêng (không dùng sa)
- ✅ Mật khẩu mạnh
- ✅ Enable SSL/TLS cho database connection
- ✅ Restrict database access từ firewall

### 2. Application Security
- ✅ Không hardcode credentials trong code
- ✅ Sử dụng HTTPS (SSL/TLS certificate)
- ✅ Cấu hình CORS nếu cần
- ✅ Enable Tomcat security manager

### 3. Server Security
- ✅ Cập nhật hệ điều hành và Java thường xuyên
- ✅ Cấu hình firewall
- ✅ Sử dụng SSH key thay vì password
- ✅ Disable Tomcat Manager nếu không cần

## Troubleshooting

### Lỗi: Cannot connect to database
- Kiểm tra database server đang chạy
- Kiểm tra firewall rules
- Kiểm tra connection string và credentials
- Xem log: `$CATALINA_HOME/logs/catalina.out`

### Lỗi: Email không gửi được
- Kiểm tra SMTP credentials
- Kiểm tra firewall (port 587)
- Với Gmail: Đảm bảo đã tạo App Password
- Xem log để biết chi tiết lỗi

### Lỗi: Ảnh không hiển thị
- Kiểm tra quyền ghi vào thư mục images
- Kiểm tra cấu hình ImageServlet
- Kiểm tra đường dẫn ảnh trong database

### Lỗi: Out of Memory
- Tăng heap size trong setenv: `-Xmx2048m`
- Kiểm tra memory leaks

## Monitoring & Maintenance

### Log Files
- Application logs: `$CATALINA_HOME/logs/catalina.out`
- Access logs: `$CATALINA_HOME/logs/localhost_access_log.*`

### Backup
- Backup database thường xuyên
- Backup WAR file và cấu hình
- Backup thư mục images

### Performance
- Monitor database performance
- Monitor Tomcat memory usage
- Sử dụng connection pooling (đã có trong code)

## Liên Hệ & Hỗ Trợ

Nếu gặp vấn đề khi deploy, vui lòng:
1. Kiểm tra log files
2. Xem lại các bước cấu hình
3. Đảm bảo tất cả environment variables đã được set

---

**Lưu ý:** File này cung cấp hướng dẫn tổng quát. Tùy vào môi trường cụ thể, bạn có thể cần điều chỉnh một số bước.


