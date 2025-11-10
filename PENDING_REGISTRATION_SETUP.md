# Hướng Dẫn Thiết Lập Pending Registration

## Tổng Quan

Hệ thống đã được cập nhật để **chỉ tạo tài khoản trong database sau khi xác minh email thành công**. 

### Thay Đổi Logic

**Trước đây:**
- User được tạo ngay trong bảng `users` khi đăng ký
- Chỉ cần xác minh email để kích hoạt tài khoản

**Bây giờ:**
- Thông tin đăng ký được lưu tạm trong bảng `pending_registrations`
- Chỉ khi người dùng click link xác minh email, tài khoản mới được tạo trong bảng `users`
- Token hết hạn sau 24 giờ

## Cài Đặt

### Bước 1: Tạo Bảng pending_registrations

Chạy file SQL script để tạo bảng:

```sql
-- Chạy file: create_pending_registrations_table.sql
```

Hoặc chạy trực tiếp trong SQL Server Management Studio:

1. Mở SQL Server Management Studio
2. Kết nối đến database `EcommerceDB`
3. Mở file `create_pending_registrations_table.sql`
4. Execute script

### Bước 2: Kiểm Tra

Sau khi chạy script, kiểm tra bảng đã được tạo:

```sql
SELECT * FROM pending_registrations;
```

Bảng sẽ có cấu trúc:
- `registration_id` (INT, PRIMARY KEY, IDENTITY)
- `username` (NVARCHAR(50), UNIQUE)
- `email` (NVARCHAR(100), UNIQUE)
- `password_hash` (NVARCHAR(255))
- `full_name` (NVARCHAR(100))
- `phone` (NVARCHAR(20))
- `address` (NVARCHAR(500))
- `verification_token` (NVARCHAR(255), UNIQUE)
- `created_at` (DATETIME)
- `expires_at` (DATETIME)

## Flow Hoạt Động

### 1. Đăng Ký
```
User đăng ký → Lưu vào pending_registrations → Gửi email xác minh
```

### 2. Xác Minh Email
```
User click link → Tìm trong pending_registrations → Tạo user trong users → Xóa pending registration
```

### 3. Validation
- Khi kiểm tra username/email đã tồn tại, hệ thống sẽ kiểm tra cả:
  - Bảng `users` (tài khoản đã xác minh)
  - Bảng `pending_registrations` (đăng ký chưa xác minh)

## Lưu Ý

1. **Token hết hạn**: Token trong `pending_registrations` hết hạn sau 24 giờ. Có thể chạy cleanup định kỳ:
   ```sql
   DELETE FROM pending_registrations WHERE expires_at < GETDATE();
   ```

2. **Tương thích ngược**: Hệ thống vẫn hỗ trợ xác minh cho các user đã được tạo trước đó (trong bảng `users` với `email_verified = 0`).

3. **Xóa dữ liệu cũ**: Nếu muốn xóa các pending registration đã hết hạn, có thể chạy:
   ```sql
   DELETE FROM pending_registrations WHERE expires_at < GETDATE();
   ```

## Files Đã Thay Đổi

1. **create_pending_registrations_table.sql** - Script tạo bảng
2. **src/java/com/ecommerce/dao/PendingRegistrationDAO.java** - DAO mới để quản lý pending registrations
3. **src/java/com/ecommerce/dao/UserDAO.java** - Cập nhật validation để kiểm tra cả pending registrations
4. **src/java/com/ecommerce/controller/AuthServlet.java** - Lưu vào pending_registrations thay vì users
5. **src/java/com/ecommerce/controller/EmailVerificationServlet.java** - Tạo user khi xác minh email thành công

## Testing

Sau khi cài đặt, test flow:

1. Đăng ký tài khoản mới
2. Kiểm tra trong `pending_registrations` - phải có bản ghi mới
3. Kiểm tra trong `users` - KHÔNG có user mới
4. Click link xác minh email
5. Kiểm tra trong `users` - phải có user mới với `email_verified = 1`
6. Kiểm tra trong `pending_registrations` - bản ghi đã bị xóa

