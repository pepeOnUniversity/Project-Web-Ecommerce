# Hướng Dẫn Deploy WebEcommerce lên iNET.vn

## 📋 Tổng Quan

iNET.vn là nhà cung cấp dịch vụ hosting và VPS tại Việt Nam. Bạn có thể deploy ứng dụng Java Web (JSP/Servlet) lên VPS của iNET.vn.

## 🎯 Yêu Cầu

### 1. Đăng ký dịch vụ iNET.vn
- **VPS/Cloud Server**: Khuyến nghị gói VPS có ít nhất:
  - RAM: 2GB trở lên
  - CPU: 2 cores trở lên
  - Storage: 20GB trở lên
  - OS: Ubuntu 20.04/22.04 LTS hoặc CentOS 7/8 (khuyến nghị Ubuntu)

### 2. Thông tin cần chuẩn bị
- IP address của VPS
- Username và password SSH (hoặc SSH key)
- Thông tin database (nếu iNET cung cấp SQL Server, hoặc bạn tự cài đặt)

## 🚀 Các Bước Deploy

### Bước 1: Chuẩn Bị WAR File

#### 1.1. Build WAR file từ NetBeans:
1. Mở project trong NetBeans
2. Click chuột phải vào project → **Clean and Build**
3. WAR file sẽ được tạo tại: `dist/WebEcommerce.war`

#### 1.2. Hoặc build bằng Ant (nếu có):
```bash
ant dist
```

#### 1.3. Kiểm tra WAR file:
- Đảm bảo file `WebEcommerce.war` đã được tạo trong thư mục `dist/`
- Kích thước file thường từ 5-20MB

### Bước 2: Kết Nối VPS qua SSH

#### 2.1. Windows (dùng PowerShell hoặc PuTTY):
```powershell
# Dùng PowerShell
ssh username@your-vps-ip

# Hoặc dùng PuTTY
# Download PuTTY từ: https://www.putty.org/
# Nhập IP address và port 22
```

#### 2.2. Linux/Mac:
```bash
ssh username@your-vps-ip
```

### Bước 3: Cài Đặt Java và Tomcat trên VPS

#### 3.1. Cập nhật hệ thống (Ubuntu):
```bash
sudo apt update
sudo apt upgrade -y
```

#### 3.2. Cài đặt Java 17:
```bash
# Cài đặt OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Kiểm tra phiên bản Java
java -version
javac -version
```

#### 3.3. Cài đặt Apache Tomcat 10:
```bash
# Tạo user tomcat
sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat

# Tải Tomcat 10
cd /tmp
wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.20/bin/apache-tomcat-10.1.20.tar.gz

# Giải nén
sudo tar xzf apache-tomcat-10.1.20.tar.gz -C /opt/tomcat --strip-components=1

# Phân quyền
sudo chown -R tomcat:tomcat /opt/tomcat
sudo chmod -R u+x /opt/tomcat/bin

# Tạo systemd service
sudo nano /etc/systemd/system/tomcat.service
```

#### 3.4. Nội dung file `/etc/systemd/system/tomcat.service`:
```ini
[Unit]
Description=Apache Tomcat 10
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="CATALINA_PID=/opt/tomcat/temp/tomcat.pid"
Environment="CATALINA_HOME=/opt/tomcat"
Environment="CATALINA_BASE=/opt/tomcat"
Environment="CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC"
Environment="JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom"

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

RestartSec=10
Restart=always

[Install]
WantedBy=multi-user.target
```

#### 3.5. Khởi động Tomcat:
```bash
# Reload systemd
sudo systemctl daemon-reload

# Khởi động Tomcat
sudo systemctl start tomcat
sudo systemctl enable tomcat

# Kiểm tra trạng thái
sudo systemctl status tomcat
```

#### 3.6. Mở firewall (nếu có):
```bash
# Ubuntu (UFW)
sudo ufw allow 8080/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw reload

# Kiểm tra Tomcat đã chạy
curl http://localhost:8080
```

### Bước 4: Cài Đặt SQL Server (Nếu cần)

#### 4.1. Nếu iNET cung cấp SQL Server:
- Sử dụng thông tin connection string từ iNET
- Bỏ qua bước này

#### 4.2. Nếu tự cài đặt SQL Server trên VPS:

**Option A: SQL Server Express (Miễn phí)**
```bash
# Ubuntu 22.04
curl -fsSL https://packages.microsoft.com/keys/microsoft.asc | sudo gpg --dearmor -o /usr/share/keyrings/microsoft-prod.gpg
sudo add-apt-repository "$(curl -fsSL https://packages.microsoft.com/config/ubuntu/22.04/mssql-server-2022.list)"
sudo apt-get update
sudo apt-get install -y mssql-server
sudo /opt/mssql/bin/mssql-conf setup
```

**Option B: Dùng Azure SQL Database (Khuyến nghị)**
- Đăng ký Azure SQL Database
- Lấy connection string
- Không cần cài đặt trên VPS

#### 4.3. Tạo Database:
```bash
# Kết nối SQL Server
sqlcmd -S localhost -U sa -P 'YourPassword123!'

# Tạo database
CREATE DATABASE EcommerceDB;
GO

# Import schema (từ máy local)
# Upload file schema.sql lên VPS trước
sqlcmd -S localhost -U sa -P 'YourPassword123!' -d EcommerceDB -i schema.sql
```

### Bước 5: Upload WAR File lên VPS

#### 5.1. Sử dụng SCP (Windows PowerShell):
```powershell
# Từ máy Windows
scp dist\WebEcommerce.war username@your-vps-ip:/tmp/
```

#### 5.2. Sử dụng SCP (Linux/Mac):
```bash
scp dist/WebEcommerce.war username@your-vps-ip:/tmp/
```

#### 5.3. Hoặc dùng FileZilla/WinSCP:
- Download FileZilla: https://filezilla-project.org/
- Kết nối qua SFTP
- Upload file `WebEcommerce.war` lên `/tmp/`

### Bước 6: Deploy WAR File lên Tomcat

#### 6.1. Copy WAR file vào webapps:
```bash
# Trên VPS
sudo cp /tmp/WebEcommerce.war /opt/tomcat/webapps/

# Phân quyền
sudo chown tomcat:tomcat /opt/tomcat/webapps/WebEcommerce.war
sudo chmod 644 /opt/tomcat/webapps/WebEcommerce.war

# Tomcat sẽ tự động deploy
# Kiểm tra log
sudo tail -f /opt/tomcat/logs/catalina.out
```

#### 6.2. Hoặc dùng Tomcat Manager:
```bash
# Cấu hình Tomcat Manager
sudo nano /opt/tomcat/conf/tomcat-users.xml
```

Thêm vào cuối file (trước `</tomcat-users>`):
```xml
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<user username="admin" password="StrongPassword123!" roles="manager-gui,manager-script"/>
```

Restart Tomcat:
```bash
sudo systemctl restart tomcat
```

Truy cập: `http://your-vps-ip:8080/manager/html`

### Bước 7: Cấu Hình Environment Variables

#### 7.1. Tạo file setenv.sh:
```bash
sudo nano /opt/tomcat/bin/setenv.sh
```

#### 7.2. Nội dung file setenv.sh:
```bash
#!/bin/sh

# Java Options
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:MaxPermSize=256m"

# Database Configuration
export JAVA_OPTS="$JAVA_OPTS -Ddb.url=jdbc:sqlserver://your-db-server:1433;databaseName=EcommerceDB;encrypt=true;trustServerCertificate=false;"
export JAVA_OPTS="$JAVA_OPTS -Ddb.user=sa"
export JAVA_OPTS="$JAVA_OPTS -Ddb.password=YourStrongPassword123!"

# Email Configuration (SMTP)
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.host=smtp.gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.port=587"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.user=your-email@gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Dsmtp.password=your-app-password"
export JAVA_OPTS="$JAVA_OPTS -Demail.from=your-email@gmail.com"
export JAVA_OPTS="$JAVA_OPTS -Demail.from.name=Ecommerce Store"

# Image Storage (Optional)
# export JAVA_OPTS="$JAVA_OPTS -Decommerce.images.path=/var/www/ecommerce/images"
# export JAVA_OPTS="$JAVA_OPTS -Decommerce.images.url=https://yourdomain.com/images"
```

#### 7.3. Phân quyền và restart:
```bash
sudo chmod +x /opt/tomcat/bin/setenv.sh
sudo chown tomcat:tomcat /opt/tomcat/bin/setenv.sh
sudo systemctl restart tomcat
```

### Bước 8: Cấu Hình Nginx Reverse Proxy (Khuyến nghị)

#### 8.1. Cài đặt Nginx:
```bash
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### 8.2. Tạo file cấu hình:
```bash
sudo nano /etc/nginx/sites-available/ecommerce
```

#### 8.3. Nội dung file:
```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;

    # Redirect HTTP to HTTPS (sau khi có SSL)
    # return 301 https://$server_name$request_uri;

    location / {
        proxy_pass http://localhost:8080/WebEcommerce;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeout settings
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Static files caching
    location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
        proxy_pass http://localhost:8080/WebEcommerce;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

#### 8.4. Enable site và restart:
```bash
sudo ln -s /etc/nginx/sites-available/ecommerce /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### Bước 9: Cấu Hình SSL/HTTPS (Let's Encrypt)

#### 9.1. Cài đặt Certbot:
```bash
sudo apt install certbot python3-certbot-nginx -y
```

#### 9.2. Lấy SSL certificate:
```bash
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com
```

#### 9.3. Auto-renewal:
```bash
# Certbot tự động cấu hình cron job
sudo certbot renew --dry-run
```

### Bước 10: Cấu Hình Firewall

```bash
# Kiểm tra firewall status
sudo ufw status

# Mở các port cần thiết
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 8080/tcp  # Tomcat (nếu cần truy cập trực tiếp)

# Enable firewall
sudo ufw enable
```

### Bước 11: Kiểm Tra Deploy

#### 11.1. Kiểm tra log:
```bash
# Tomcat logs
sudo tail -f /opt/tomcat/logs/catalina.out

# Nginx logs
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/nginx/access.log
```

#### 11.2. Truy cập ứng dụng:
- **Qua Nginx**: `http://yourdomain.com` hoặc `https://yourdomain.com`
- **Trực tiếp Tomcat**: `http://your-vps-ip:8080/WebEcommerce`

#### 11.3. Test các chức năng:
- ✅ Trang chủ hiển thị
- ✅ Đăng ký tài khoản mới
- ✅ Đăng nhập
- ✅ Xem sản phẩm
- ✅ Thêm vào giỏ hàng
- ✅ Đặt hàng
- ✅ Admin panel

## 🔧 Cấu Hình Bổ Sung

### 1. Tối Ưu Tomcat Performance

Chỉnh sửa `setenv.sh`:
```bash
export JAVA_OPTS="-Xms1024m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### 2. Backup Database

Tạo script backup:
```bash
sudo nano /opt/backup-db.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
sqlcmd -S localhost -U sa -P 'YourPassword' -Q "BACKUP DATABASE EcommerceDB TO DISK = '$BACKUP_DIR/ecommerce_$DATE.bak'"
```

Thêm vào crontab:
```bash
sudo crontab -e
# Backup mỗi ngày lúc 2h sáng
0 2 * * * /opt/backup-db.sh
```

### 3. Monitor Application

Cài đặt monitoring tools:
```bash
# Htop để monitor resources
sudo apt install htop -y

# Netdata (optional)
bash <(curl -Ss https://my-netdata.io/kickstart.sh)
```

## 🐛 Troubleshooting

### ⚠️ Lỗi: Shared Hosting không có Java/Tomcat

**Triệu chứng:**
- Không tìm thấy Java: `which java` → `no java`
- Không tìm thấy thư mục `webapps/`
- Không có quyền `sudo`
- Lệnh `find ~ -name "webapps" -type d` không trả về kết quả

**Nguyên nhân:**
- Bạn đang ở trên **Shared Hosting** (cPanel), không phải VPS
- Shared Hosting thường **KHÔNG hỗ trợ Java/Tomcat** trừ khi hosting provider cung cấp Application Manager
- Không có quyền cài đặt Java/Tomcat trực tiếp

**Giải pháp:**

#### Cách 1: Kiểm tra Application Manager trong cPanel (Khuyến nghị)

1. **Đăng nhập vào cPanel:**
   - Truy cập: `http://your-server-ip:2083` (cPanel) hoặc `https://your-server-ip:8443` (Plesk)
   - Đăng nhập với thông tin từ email iNET.vn

2. **Tìm Application Manager:**
   - Trong cPanel, tìm các mục sau:
     - **Java Applications**
     - **Application Manager**
     - **Tomcat**
     - **Java Support**
   - Nếu **CÓ** các mục trên → Hosting **HỖ TRỢ Java** → Xem hướng dẫn trong `DEPLOY_SHARED_HOSTING_INET.md`
   - Nếu **KHÔNG CÓ** → Xem Cách 2 hoặc Cách 3

#### Cách 2: Liên hệ iNET.vn để kích hoạt Java/Tomcat

**Gửi email hoặc ticket cho iNET.vn:**

```
Xin chào,

Tôi có Shared Hosting với IP [your-server-ip].
Tôi muốn deploy ứng dụng Java Web (JSP/Servlet) lên hosting này.

Vui lòng cho tôi biết:
1. Shared Hosting của tôi có hỗ trợ Java/Tomcat không?
2. Nếu có, cách kích hoạt và deploy như thế nào?
3. Nếu không, tôi cần upgrade lên VPS không? VPS có giá bao nhiêu?

Thông tin hosting:
- IP: [your-server-ip]
- Username: [your-username]
- Control Panel: cPanel/Plesk

Cảm ơn!
```

#### Cách 3: Upgrade lên VPS (Nếu Shared Hosting không hỗ trợ Java)

**Nếu Shared Hosting không hỗ trợ Java/Tomcat**, bạn cần:

1. **Upgrade lên VPS** từ iNET.vn:
   - VPS có ít nhất: 2GB RAM, 2 CPU cores, 20GB storage
   - OS: Ubuntu 20.04/22.04 LTS (khuyến nghị)

2. **Sau khi có VPS**, làm theo hướng dẫn từ **Bước 3** trong file này để:
   - Cài đặt Java 17
   - Cài đặt Apache Tomcat 10
   - Deploy ứng dụng

**Lưu ý:**
- Shared Hosting thường chỉ hỗ trợ PHP, không hỗ trợ Java
- VPS cho phép bạn cài đặt bất kỳ phần mềm nào (Java, Tomcat, etc.)
- VPS có giá cao hơn Shared Hosting nhưng linh hoạt hơn

**Xem thêm:**
- `DEPLOY_SHARED_HOSTING_INET.md` - Hướng dẫn deploy lên Shared Hosting có Application Manager
- Phần **Bước 3** trong file này - Cài đặt Java và Tomcat trên VPS

---

### Lỗi: Cannot connect to database
```bash
# Kiểm tra SQL Server đang chạy
sudo systemctl status mssql-server

# Test connection
sqlcmd -S localhost -U sa -P 'YourPassword' -Q "SELECT @@VERSION"

# Kiểm tra firewall
sudo ufw status
```

### Lỗi: Tomcat không khởi động
```bash
# Kiểm tra log
sudo tail -f /opt/tomcat/logs/catalina.out

# Kiểm tra Java
java -version

# Kiểm tra port 8080 đã được sử dụng
sudo netstat -tulpn | grep 8080
```

### Lỗi: Ảnh không hiển thị
```bash
# Kiểm tra quyền thư mục images
sudo ls -la /opt/tomcat/webapps/WebEcommerce/images/

# Phân quyền
sudo chown -R tomcat:tomcat /opt/tomcat/webapps/WebEcommerce/images/
sudo chmod -R 755 /opt/tomcat/webapps/WebEcommerce/images/
```

### Lỗi: Out of Memory
```bash
# Tăng heap size trong setenv.sh
export JAVA_OPTS="-Xms1024m -Xmx2048m ..."
sudo systemctl restart tomcat
```

## 📞 Hỗ Trợ iNET.vn

Nếu gặp vấn đề với VPS của iNET:
- **Hotline**: Xem trên website inet.vn
- **Ticket**: Đăng nhập vào control panel của iNET
- **Email**: support@inet.vn (hoặc email hỗ trợ từ iNET)

## ✅ Checklist Deploy

- [ ] Đăng ký VPS trên iNET.vn
- [ ] Kết nối SSH thành công
- [ ] Cài đặt Java 17
- [ ] Cài đặt Apache Tomcat 10
- [ ] Cài đặt SQL Server (hoặc dùng Azure SQL)
- [ ] Tạo database và import schema
- [ ] Build WAR file từ NetBeans
- [ ] Upload WAR file lên VPS
- [ ] Deploy WAR file lên Tomcat
- [ ] Cấu hình environment variables
- [ ] Cài đặt và cấu hình Nginx
- [ ] Cấu hình SSL/HTTPS
- [ ] Cấu hình firewall
- [ ] Test tất cả chức năng
- [ ] Setup backup database
- [ ] Monitor application

## 🎉 Hoàn Thành!

Sau khi hoàn thành tất cả các bước, ứng dụng của bạn sẽ chạy trên:
- **URL**: `https://yourdomain.com` (nếu có domain)
- **Hoặc**: `http://your-vps-ip:8080/WebEcommerce`

**Lưu ý quan trọng:**
- Đảm bảo thay đổi tất cả password mặc định
- Không commit credentials vào git
- Backup database thường xuyên
- Monitor server resources
- Cập nhật hệ thống và Java thường xuyên

---

**Chúc bạn deploy thành công! 🚀**

