# Ecommerce Web Application

Dự án web ecommerce hoàn chỉnh được xây dựng với Java Servlet, JSP, SQL Server 2019.

## 📋 Mô tả

Ứng dụng web ecommerce với đầy đủ các chức năng:
- Xem và tìm kiếm sản phẩm
- Quản lý giỏ hàng
- Đăng ký/Đăng nhập
- Đặt hàng và quản lý đơn hàng
- Admin panel để quản lý sản phẩm và đơn hàng

## 🛠️ Công nghệ sử dụng

- **Backend**: Java Servlet 4.0, JSP
- **Database**: SQL Server 2019
- **Frontend**: Bootstrap 5, Font Awesome, AOS Animation
- **Build Tool**: Apache Ant (NetBeans)
- **Server**: Apache Tomcat

## 📦 Cấu trúc dự án

```
WebEcommerce/
├── src/java/com/ecommerce/
│   ├── controller/          # Servlets xử lý request
│   │   ├── HomeServlet.java
│   │   ├── ProductServlet.java
│   │   ├── CartServlet.java
│   │   ├── AuthServlet.java
│   │   ├── OrderServlet.java
│   │   └── admin/
│   │       └── AdminServlet.java
│   ├── dao/                 # Data Access Object
│   │   ├── ProductDAO.java
│   │   ├── UserDAO.java
│   │   ├── CategoryDAO.java
│   │   ├── CartDAO.java
│   │   └── OrderDAO.java
│   ├── model/            # Model classes
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── util/              # Utilities
│   │   ├── DBConnection.java
│   │   └── PasswordUtil.java
│   └── filter/            # Filters
│       ├── AuthFilter.java
│       └── AdminFilter.java
├── web/
│   ├── views/             # JSP pages
│   │   ├── common/        # Common components
│   │   ├── customer/      # Customer pages
│   │   ├── admin/         # Admin pages
│   │   └── auth/          # Authentication pages
│   ├── assets/
│   │   ├── css/           # Custom CSS
│   │   └── js/            # JavaScript files
│   └── WEB-INF/
│       └── web.xml        # Web configuration
└── schema.sql             # Database schema

```

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống

- Java JDK 17+
- NetBeans IDE
- Apache Tomcat 10.x
- SQL Server 2019
- SQL Server JDBC Driver

### Bước 1: Cài đặt Database

1. Mở SQL Server Management Studio
2. Tạo database mới hoặc sử dụng database có sẵn
3. Chạy file `schema.sql` để tạo tables và insert sample data

### Bước 2: Cấu hình Database Connection

**Cho Development (Local):**
- Code đã có default values cho localhost
- Có thể chạy ngay mà không cần cấu hình thêm

**Cho Production:**
- Sử dụng Environment Variables hoặc System Properties
- KHÔNG hardcode credentials trong code
- Cấu hình database connection qua environment variables: `DB_URL`, `DB_USER`, `DB_PASSWORD`

### Bước 3: Thêm Dependencies vào NetBeans

Đảm bảo các file JAR sau đã được thêm vào project:
- `mssql-jdbc-12.8.1.jre11.jar` (hoặc phiên bản mới hơn)
- `jakarta.servlet.jsp.jstl-2.0.0.jar`
- `jakarta.servlet.jsp.jstl-api-2.0.0.jar`
- `jbcrypt-0.4.jar`

### Bước 4: Deploy và chạy

1. Mở project trong NetBeans
2. Clean and Build project (Shift+F11)
3. Deploy project lên Tomcat
4. Truy cập: `http://localhost:9999/WebEcommerce`

## 🔐 Tài khoản mặc định

### Admin
- Username: `admin`
- Password: `admin123`

### Customer
- Username: `customer1`
- Password: `customer123`

> **Lưu ý**: Các password hash đã được tạo bằng BCrypt. Để tạo password mới, sử dụng `PasswordUtil.hashPassword()`.

## 📝 Chức năng chính

### Customer
- ✅ Xem danh sách sản phẩm và chi tiết
- ✅ Tìm kiếm và lọc sản phẩm
- ✅ Thêm sản phẩm vào giỏ hàng (AJAX)
- ✅ Quản lý giỏ hàng (thêm/xóa/cập nhật)
- ✅ Đăng ký/Đăng nhập
- ✅ Đặt hàng và xem lịch sử đơn hàng

### Admin
- ✅ Dashboard với thống kê
- ✅ Quản lý sản phẩm
- ✅ Quản lý đơn hàng (cập nhật trạng thái)
- ✅ Xem sản phẩm sắp hết hàng

## 🔧 Cấu hình bổ sung

### Session Timeout

Trong `web.xml`, session timeout được set là 30 phút. Bạn có thể thay đổi:

```xml
<session-config>
    <session-timeout>30</session-timeout>
</session-config>
```

### Filter Configuration

- `AuthFilter`: Bảo vệ các trang `/cart`, `/checkout`, `/orders`
- `AdminFilter`: Bảo vệ các trang `/admin/*`

## 🚀 Deploy lên Production

**Dự án đã sẵn sàng để deploy lên production!**

### Các thay đổi đã thực hiện:
- ✅ Database connection đọc từ environment variables
- ✅ Email service đọc từ environment variables  
- ✅ Image storage hỗ trợ external path
- ✅ Không hardcode credentials trong code

### Hướng dẫn deploy:
- Build WAR file: Sử dụng script `build-war.bat` (Windows) hoặc `build-war.sh` (Linux/Mac)
- Cấu hình environment variables: Xem file `env.example` để biết các biến cần thiết
- Deploy lên Tomcat: Copy WAR file vào thư mục `webapps` của Tomcat
- Deploy lên Cloud Platforms: AWS Elastic Beanstalk, Azure App Service, Railway.app, Render.com
- Cấu hình bảo mật: Sử dụng HTTPS, cấu hình firewall, bảo vệ credentials

### ⚠️ Lưu ý về Platform:
- **Vercel KHÔNG hỗ trợ Java backend!** Vercel chỉ hỗ trợ serverless functions (Node.js, Python, etc.)
- Để deploy Java application, sử dụng các platform hỗ trợ Java như Railway.app, Render.com, AWS, Azure

### Platform hỗ trợ Java (Khuyến nghị):
- **Railway.app** ⭐ - Dễ deploy, free tier $5/tháng
- **Render.com** - Free tier, deploy từ GitHub
- **AWS Elastic Beanstalk** - Free tier 12 tháng
- **Azure App Service** - Free tier, tích hợp Azure SQL
- **VPS (iNET.vn)** - Full control, giá rẻ

### File cấu hình mẫu:
- `config.example.properties` - Mẫu cấu hình properties
- `env.example` - Mẫu environment variables

### Scripts hỗ trợ:
- `build-war.bat` - Build WAR file trên Windows
- `build-war.sh` - Build WAR file trên Linux/Mac

## 📝 Ghi chú

- File `schema.sql` chứa cấu trúc database và sample data
- Password được hash bằng BCrypt với salt rounds = 12
- Hình ảnh sản phẩm lưu local trong thư mục `web/images/products/`
- Code đã hỗ trợ external image storage qua environment variables

## 🐛 Troubleshooting

### Lỗi kết nối database
- Kiểm tra SQL Server đã được start
- Kiểm tra username/password trong DBConnection.java
- Kiểm tra port và database name

### Lỗi 404
- Kiểm tra URL mapping trong `web.xml`
- Kiểm tra servlet annotations (@WebServlet)

### Lỗi JSP
- Kiểm tra JSTL libraries đã được thêm
- Kiểm tra encoding (UTF-8)

## 📄 License

Dự án này được tạo cho mục đích học tập và nghiên cứu.

## 👨‍💻 Tác giả

Created for FPT University Final Project - Semester 4








