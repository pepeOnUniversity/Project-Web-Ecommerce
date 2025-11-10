# Hướng Dẫn Thiết Lập Tính Năng Quên Mật Khẩu

## Tổng Quan

Tính năng quên mật khẩu cho phép người dùng đặt lại mật khẩu thông qua email. Hệ thống sẽ:
1. Người dùng nhập email đã đăng ký
2. Hệ thống gửi link reset password qua email (token hết hạn sau 1 giờ)
3. Người dùng click link và đặt lại mật khẩu mới

## Cài Đặt

### Bước 1: Tạo Bảng password_reset_tokens

Chạy file SQL script để tạo bảng:

```sql
-- Chạy file: create_password_reset_tokens_table.sql
```

Hoặc chạy trực tiếp trong SQL Server Management Studio:

1. Mở SQL Server Management Studio
2. Kết nối đến database `EcommerceDB`
3. Mở file `create_password_reset_tokens_table.sql`
4. Execute script

### Bước 2: Kiểm Tra

Sau khi chạy script, kiểm tra bảng đã được tạo:

```sql
SELECT * FROM password_reset_tokens;
```

Bảng sẽ có cấu trúc:
- `token_id` (INT, PRIMARY KEY, IDENTITY)
- `user_id` (INT, FOREIGN KEY -> users.user_id)
- `token` (NVARCHAR(255), UNIQUE)
- `expires_at` (DATETIME)
- `created_at` (DATETIME)
- `used` (BIT) - Đánh dấu token đã được sử dụng

## Flow Hoạt Động

### 1. Yêu Cầu Reset Password
```
User vào /forgot-password → Nhập email → Hệ thống tạo token → Gửi email
```

### 2. Reset Password
```
User click link trong email → Validate token → Nhập mật khẩu mới → Cập nhật password → Đánh dấu token đã dùng
```

### 3. Bảo Mật
- Token hết hạn sau 1 giờ
- Mỗi token chỉ được sử dụng 1 lần
- Token cũ của user sẽ bị vô hiệu hóa khi tạo token mới
- Không tiết lộ email có tồn tại hay không (luôn hiển thị thông báo thành công)

## Các File Đã Tạo/Cập Nhật

### Backend
1. **PasswordResetDAO.java** - Quản lý password reset tokens trong database
2. **ForgotPasswordServlet.java** - Xử lý yêu cầu quên mật khẩu
3. **ResetPasswordServlet.java** - Xử lý đặt lại mật khẩu với token
4. **UserDAO.java** - Thêm method `updatePassword()`
5. **EmailService.java** - Thêm method `sendPasswordResetEmail()`
6. **AuthServlet.java** - Cập nhật để hiển thị thông báo reset password thành công

### Frontend
1. **forgot-password.jsp** - Trang nhập email để reset password
2. **reset-password.jsp** - Trang đặt lại mật khẩu với token
3. **login.jsp** - Thêm link "Quên mật khẩu?"

### Database
1. **create_password_reset_tokens_table.sql** - Script tạo bảng password_reset_tokens

## URL Endpoints

- `/forgot-password` - GET: Hiển thị form nhập email
- `/forgot-password` - POST: Xử lý yêu cầu reset password
- `/reset-password?token=xxx` - GET: Hiển thị form đặt lại mật khẩu (nếu token hợp lệ)
- `/reset-password` - POST: Xử lý đặt lại mật khẩu

## Cấu Hình Email

Tính năng này sử dụng cùng cấu hình SMTP như email verification:
- File `config.properties` hoặc Environment Variables
- Các biến cần thiết:
  - `smtp.host`
  - `smtp.port`
  - `smtp.user`
  - `smtp.password`
  - `email.from`
  - `email.from.name`

Xem thêm file `SMTP_CONFIGURATION.md` (nếu có) để biết cách cấu hình.

## Testing

### Test Case 1: Quên mật khẩu với email hợp lệ
1. Vào `/forgot-password`
2. Nhập email đã đăng ký
3. Kiểm tra email nhận được link reset password
4. Click link và đặt lại mật khẩu
5. Đăng nhập với mật khẩu mới

### Test Case 2: Quên mật khẩu với email không tồn tại
1. Vào `/forgot-password`
2. Nhập email không tồn tại
3. Vẫn hiển thị thông báo thành công (bảo mật)

### Test Case 3: Token hết hạn
1. Tạo token reset password
2. Đợi hơn 1 giờ
3. Thử sử dụng token → Phải báo lỗi "Token đã hết hạn"

### Test Case 4: Token đã được sử dụng
1. Tạo token và reset password thành công
2. Thử sử dụng lại token đó → Phải báo lỗi "Token đã được sử dụng"

## Cleanup

Có thể chạy cleanup job định kỳ để xóa các token đã hết hạn:

```java
PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
passwordResetDAO.cleanupExpiredTokens();
```

## Lưu Ý

1. **Bảo mật**: Token được tạo ngẫu nhiên an toàn (32 bytes, Base64 URL-safe)
2. **Thời gian hết hạn**: Token hết hạn sau 1 giờ (có thể thay đổi trong `PasswordResetDAO.TOKEN_EXPIRY_SECONDS`)
3. **Email**: Đảm bảo cấu hình SMTP đúng để email được gửi thành công
4. **Database**: Đảm bảo bảng `password_reset_tokens` đã được tạo trước khi sử dụng

## Troubleshooting

### Email không được gửi
- Kiểm tra cấu hình SMTP trong `config.properties` hoặc Environment Variables
- Kiểm tra logs để xem lỗi chi tiết
- Đảm bảo email server không chặn email từ ứng dụng

### Token không hợp lệ
- Kiểm tra token chưa hết hạn (1 giờ)
- Kiểm tra token chưa được sử dụng
- Kiểm tra token đúng format (Base64 URL-safe)

### Lỗi database
- Kiểm tra bảng `password_reset_tokens` đã được tạo
- Kiểm tra foreign key constraint với bảng `users`
- Kiểm tra connection string trong `DBConnection`

