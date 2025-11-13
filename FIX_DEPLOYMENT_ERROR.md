# 🔧 Khắc Phục Lỗi Deploy "Context Failed to Start"

## ❌ Lỗi

```
FAIL - Deployed application at context path [/] but context failed to start
The module has not been deployed.
```

## 🔍 Nguyên Nhân

Có thể do một trong các nguyên nhân sau:

1. **Lỗi compile:** Các servlet mới không compile được
2. **Conflict giữa @WebServlet và web.xml:** DebugVNPayServlet vừa có @WebServlet vừa được đăng ký trong web.xml
3. **Thiếu dependencies:** Thiếu Jakarta EE dependencies trong classpath
4. **Lỗi trong servlet initialization:** Có exception khi khởi tạo servlet

## ✅ Giải Pháp

### Bước 1: Xóa Conflict trong web.xml

Đã xóa registration của DebugVNPayServlet trong web.xml vì nó đã dùng @WebServlet annotation.

### Bước 2: Clean và Build Lại Project

1. **Trong NetBeans:**
   - Right-click project → Clean and Build (Shift+F11)
   - Hoặc: Build → Clean and Build Project

2. **Kiểm tra lỗi compile:**
   - Xem tab "Output" → "Build"
   - Nếu có lỗi, sửa trước khi deploy

### Bước 3: Kiểm Tra Dependencies

Đảm bảo các file JAR sau có trong `web/WEB-INF/lib/`:
- `jakarta.servlet.jsp.jstl-2.0.0.jar`
- `jakarta.servlet.jsp.jstl-api-2.0.0.jar`
- `mssql-jdbc-12.8.1.jre11.jar`
- `jbcrypt-0.4.jar`
- `jakarta.activation-api-2.1.3.jar`
- `jakarta.mail-api-2.1.3.jar`
- `angus-activation-1.0.0.jar`
- `angus-mail-2.0.3.jar`

**Lưu ý:** Jakarta Servlet API được cung cấp bởi Tomcat, không cần thêm vào lib.

### Bước 4: Stop và Start Lại Tomcat

1. **Stop Tomcat hoàn toàn:**
   - Trong NetBeans: Services → Servers → Tomcat → Stop
   - Hoặc: Task Manager → Kill process Java

2. **Xóa thư mục deploy (nếu cần):**
   - Xóa thư mục `build` trong project
   - Xóa thư mục deploy trong Tomcat (nếu có)

3. **Start Tomcat:**
   - Trong NetBeans: Services → Servers → Tomcat → Start
   - Hoặc: Right-click project → Run

### Bước 5: Kiểm Tra Log Tomcat

Nếu vẫn lỗi, kiểm tra log Tomcat:

1. **Trong NetBeans:**
   - Tab "Output" → "Tomcat"
   - Tìm các dòng ERROR hoặc SEVERE

2. **Hoặc trong file:**
   - `C:\Program Files\Apache Software Foundation\Tomcat 10.1_Tomcat10.1\logs\catalina.out`
   - `C:\Program Files\Apache Software Foundation\Tomcat 10.1_Tomcat10.1\logs\localhost.log`

3. **Tìm các lỗi như:**
   - `ClassNotFoundException`
   - `NoClassDefFoundError`
   - `ServletException`
   - `IllegalStateException`

## 🐛 Troubleshooting

### Nếu Vẫn Gặp Lỗi "Context Failed to Start"

#### 1. Kiểm Tra Lỗi Compile

```powershell
# Trong NetBeans, xem tab "Output" → "Build"
# Hoặc compile thủ công:
cd "D:\FPT_University\semester4\Final_Project\WebEcommerce"
javac -cp "C:\Program Files\Apache Software Foundation\Tomcat 10.1_Tomcat10.1\lib\servlet-api.jar" src/java/com/ecommerce/controller/*.java
```

#### 2. Kiểm Tra Các Servlet Mới

Đảm bảo các file sau compile được:
- `PaymentServlet.java`
- `VNPayCallbackServlet.java`
- `DebugVNPayServlet.java`
- `VNPayUtil.java`
- `VNPayConfig.java`

#### 3. Kiểm Tra web.xml Syntax

Đảm bảo `web.xml` có cú pháp đúng:
- XML well-formed
- Không có duplicate servlet-name
- Không có conflict giữa @WebServlet và web.xml

#### 4. Kiểm Tra Database Connection

Nếu servlet cố kết nối database khi init, có thể gây lỗi:
- Kiểm tra database có đang chạy không
- Kiểm tra connection string trong `DBConnection.java`

#### 5. Xóa Cache NetBeans

1. Đóng NetBeans
2. Xóa thư mục: `C:\Users\YOUR_USER\AppData\Local\NetBeans\Cache`
3. Mở lại NetBeans

#### 6. Rebuild Từ Đầu

1. **Xóa thư mục build:**
   ```powershell
   Remove-Item -Recurse -Force "D:\FPT_University\semester4\Final_Project\WebEcommerce\build"
   ```

2. **Xóa thư mục dist:**
   ```powershell
   Remove-Item -Recurse -Force "D:\FPT_University\semester4\Final_Project\WebEcommerce\dist"
   ```

3. **Clean và Build lại trong NetBeans**

## ✅ Kiểm Tra Thành Công

Sau khi deploy thành công, bạn có thể:

1. **Truy cập trang chủ:**
   ```
   http://localhost:9999/
   ```

2. **Truy cập Debug Servlet:**
   ```
   http://localhost:9999/debug/vnpay
   ```

3. **Kiểm tra log:**
   - Không có ERROR trong log Tomcat
   - Context start thành công

## 📝 Lưu Ý

- **@WebServlet vs web.xml:** Chỉ dùng một trong hai cách:
  - Dùng @WebServlet annotation (không cần web.xml)
  - Hoặc đăng ký trong web.xml (không cần @WebServlet)
  - **KHÔNG dùng cả hai** vì sẽ gây conflict

- **Jakarta EE:** Project dùng Jakarta EE (Tomcat 10), không phải Java EE (javax.*)

- **Dependencies:** Jakarta Servlet API được cung cấp bởi Tomcat, không cần thêm vào lib

---

**Sau khi fix xong, hãy Clean and Build lại project và deploy!** 🚀

