# Hướng Dẫn Sử Dụng Ngrok cho VNPay

## Tổng Quan

Ngrok được sử dụng để expose localhost ra internet, cho phép VNPay gửi callback về server local của bạn khi test thanh toán.

## Cài Đặt Ngrok

### Bước 1: Tải Ngrok

1. Truy cập: https://ngrok.com/download
2. Tải file `ngrok.exe` cho Windows
3. Giải nén và đặt vào một thư mục (ví dụ: `C:\ngrok\`)
4. Thêm thư mục vào PATH hoặc đặt file `ngrok.exe` vào thư mục project

### Bước 2: Config Authtoken

Authtoken đã được lưu trong file `config.properties`:
```
ngrok.authtoken=30CX95zenYVZutb0DfKN0C6Hh4T_5HWchnbHVNr91TU8SUMwf
```

Script `start-ngrok.ps1` sẽ tự động config authtoken này khi chạy.

Hoặc bạn có thể config thủ công:
```powershell
ngrok config add-authtoken 30CX95zenYVZutb0DfKN0C6Hh4T_5HWchnbHVNr91TU8SUMwf
```

## Sử Dụng

### Cách 1: Sử dụng Script (Khuyến nghị)

Chạy script PowerShell:
```powershell
.\start-ngrok.ps1
```

Script sẽ:
- Tự động đọc authtoken từ `config.properties`
- Config ngrok với authtoken
- Khởi động ngrok để expose port 8080
- Hiển thị URL công khai để bạn copy

### Cách 2: Chạy Thủ Công

1. Mở PowerShell hoặc Command Prompt
2. Chạy lệnh:
   ```powershell
   ngrok http 8080
   ```
3. Copy URL từ output (ví dụ: `https://abc123.ngrok-free.app`)

## Cấu Hình VNPay với Ngrok URL

Sau khi có URL từ ngrok, bạn có 3 cách để cấu hình:

### Cách 1: Set System Property (Khuyến nghị)

Khi chạy Tomcat, thêm system property:
```
-Dvnpay.ngrok.url=https://abc123.ngrok-free.app
```

Hoặc trong NetBeans:
1. Right-click project → Properties
2. Run → VM Options
3. Thêm: `-Dvnpay.ngrok.url=https://abc123.ngrok-free.app`

### Cách 2: Cập nhật config.properties

Mở file `src/java/config.properties` và cập nhật:
```properties
ngrok.url=https://abc123.ngrok-free.app
```

Sau đó cập nhật `VNPayConfig.java` để đọc từ config file.

### Cách 3: Cập nhật trực tiếp trong VNPayConfig.java

Mở file `src/java/com/ecommerce/config/VNPayConfig.java` và sửa:
```java
public static final String VNPAY_RETURN_URL = "https://abc123.ngrok-free.app/WebEcommerce/vnpay-return";
public static final String VNPAY_IPN_URL = "https://abc123.ngrok-free.app/WebEcommerce/vnpay-ipn";
```

**Lưu ý**: URL ngrok sẽ thay đổi mỗi lần khởi động lại (trừ khi dùng ngrok Pro).

## Kiểm Tra Ngrok

1. **Dashboard**: Mở trình duyệt và truy cập http://localhost:4040
2. **API**: Gọi API để lấy URL:
   ```powershell
   curl http://localhost:4040/api/tunnels
   ```

## Lưu Ý Quan Trọng

1. **URL thay đổi**: Mỗi lần khởi động lại ngrok, URL sẽ thay đổi (trừ ngrok Pro có fixed domain)
2. **Cần chạy trước**: Ngrok phải chạy trước khi test thanh toán VNPay
3. **Port**: Mặc định script expose port 8080. Nếu Tomcat chạy port khác, sửa trong script
4. **Bảo mật**: Không commit file `config.properties` vào git (đã có trong .gitignore)

## Troubleshooting

### Ngrok không chạy được

- Kiểm tra authtoken đã đúng chưa
- Kiểm tra port 8080 có đang được sử dụng bởi ứng dụng khác không
- Thử chạy `ngrok http 8080` trực tiếp để xem lỗi

### Không nhận được callback từ VNPay

- Kiểm tra URL trong VNPayConfig có đúng không
- Kiểm tra ngrok dashboard tại http://localhost:4040 xem có request đến không
- Đảm bảo VNPay đã được cấu hình với đúng callback URL

### URL ngrok thay đổi

- Mỗi lần restart ngrok, URL sẽ thay đổi
- Cần cập nhật lại URL trong VNPayConfig hoặc system property
- Hoặc nâng cấp lên ngrok Pro để có fixed domain

## Tài Liệu Tham Khảo

- Ngrok Documentation: https://ngrok.com/docs
- Ngrok Dashboard: https://dashboard.ngrok.com/



