# Hướng dẫn tạo bản sao Database

Tài liệu này hướng dẫn cách tạo bản sao database để chuyển sang máy khác.

## 📋 Yêu cầu

- SQL Server Management Studio (SSMS)
- Database EcommerceDB đang chạy với dữ liệu

## 🚀 Các bước thực hiện

### Bước 1: Tạo file schema (Cấu trúc bảng)

File `database_schema.sql` đã được tạo sẵn, chứa các lệnh CREATE TABLE để tạo tất cả các bảng.

**Không cần làm gì thêm** - file này đã sẵn sàng để sử dụng.

### Bước 2: Export dữ liệu từ database hiện tại

Có 2 cách để export dữ liệu:

#### Cách 1: Sử dụng script generate_insert_statements.sql (Khuyến nghị)

1. Mở SQL Server Management Studio
2. Kết nối đến database `EcommerceDB`
3. Mở file `generate_insert_statements.sql`
4. Chạy script (F5)
5. Trong tab **Results**, bạn sẽ thấy tất cả các câu INSERT
6. Click chuột phải vào kết quả → **Select All** (Ctrl+A)
7. Copy (Ctrl+C)
8. Tạo file mới `database_data.sql` và paste vào
9. Lưu file

#### Cách 2: Sử dụng script export_database_data.sql

1. Mở SQL Server Management Studio
2. Kết nối đến database `EcommerceDB`
3. Mở file `export_database_data.sql`
4. Chạy script (F5)
5. Xem kết quả trong tab **Messages**
6. Copy toàn bộ output từ Messages tab
7. Tạo file mới `database_data.sql` và paste vào
8. Lưu file

### Bước 3: Kiểm tra file database_data.sql

Đảm bảo file `database_data.sql` có:
- Các câu `SET IDENTITY_INSERT ... ON;` và `SET IDENTITY_INSERT ... OFF;`
- Các câu `INSERT INTO ...` cho tất cả các bảng
- Các câu `GO` để phân tách batch

## 📦 Sử dụng trên máy khác

### Trên máy đích (máy nhận database):

1. **Tạo database mới** (nếu chưa có):
   ```sql
   CREATE DATABASE EcommerceDB;
   GO
   ```

2. **Chạy file schema**:
   - Mở SQL Server Management Studio
   - Kết nối đến SQL Server
   - Mở file `database_schema.sql`
   - Chạy script (F5)

3. **Chạy file data**:
   - Mở file `database_data.sql`
   - Chạy script (F5)

4. **Kiểm tra**:
   ```sql
   USE EcommerceDB;
   SELECT COUNT(*) FROM users;
   SELECT COUNT(*) FROM products;
   SELECT COUNT(*) FROM orders;
   ```

## 📁 Cấu trúc file

```
WebEcommerce/
├── database_schema.sql          # Tạo các bảng (chạy trước)
├── database_data.sql            # Insert dữ liệu (tạo từ export script)
├── generate_insert_statements.sql  # Script để generate INSERT statements
├── export_database_data.sql     # Script export dữ liệu (alternative)
└── BACKUP_DATABASE_README.md    # File hướng dẫn này
```

## ⚠️ Lưu ý

1. **Thứ tự chạy**: Luôn chạy `database_schema.sql` trước `database_data.sql`

2. **Identity Insert**: Script data sử dụng `SET IDENTITY_INSERT ... ON` để giữ nguyên ID gốc. Điều này đảm bảo dữ liệu giống hệt database gốc.

3. **Foreign Keys**: Các bảng được insert theo thứ tự đúng để đảm bảo foreign key constraints:
   - categories → users → products → cart_items → orders → order_items

4. **Encoding**: File SQL sử dụng UTF-8 để hỗ trợ tiếng Việt (NVARCHAR)

5. **Backup trước khi restore**: Nếu database đích đã có dữ liệu, hãy backup trước khi chạy script!

## 🔍 Troubleshooting

### Lỗi: "Cannot insert duplicate key"
- **Nguyên nhân**: Database đích đã có dữ liệu
- **Giải pháp**: Xóa dữ liệu cũ hoặc drop và tạo lại database

### Lỗi: "Foreign key constraint"
- **Nguyên nhân**: Thứ tự insert không đúng
- **Giải pháp**: Đảm bảo chạy `database_schema.sql` trước, sau đó mới chạy `database_data.sql`

### Lỗi: "Invalid column name"
- **Nguyên nhân**: Schema không khớp (thiếu cột)
- **Giải pháp**: Kiểm tra lại file `database_schema.sql` có đầy đủ các cột không

## ✅ Checklist

Trước khi gửi file cho người khác:

- [ ] Đã chạy `generate_insert_statements.sql` hoặc `export_database_data.sql`
- [ ] Đã tạo file `database_data.sql` với đầy đủ INSERT statements
- [ ] Đã test chạy cả 2 file trên database mới
- [ ] Đã kiểm tra số lượng records khớp với database gốc
- [ ] Đã đảm bảo file không chứa thông tin nhạy cảm (nếu cần)

## 📞 Hỗ trợ

Nếu gặp vấn đề, kiểm tra:
1. SQL Server version (cần SQL Server 2019+)
2. Database name phải là `EcommerceDB`
3. Permissions của user SQL Server



