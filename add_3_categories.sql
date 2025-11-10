-- =============================================
-- Thêm 3 categories mới để có tổng 8 categories
-- Hiển thị đều và đẹp trên trang home
-- =============================================

-- Hiện tại có 5 categories:
-- 1. Điện thoại (category_id = 1)
-- 2. Laptop (category_id = 2)
-- 3. Tablet (category_id = 3)
-- 4. Đồng hồ (category_id = 4)
-- 5. Tai nghe (category_id = 5)

-- Cập nhật category_id của 3 categories:
-- Máy ảnh: 1002 -> 6
-- Loa: 1003 -> 7
-- Phụ kiện: 1004 -> 8

-- Bước 1: Cập nhật foreign key trong bảng products (nếu có)
UPDATE products 
SET category_id = 6 
WHERE category_id = 1002;

UPDATE products 
SET category_id = 7 
WHERE category_id = 1003;

UPDATE products 
SET category_id = 8 
WHERE category_id = 1004;

GO

-- Bước 2: Xóa các categories cũ
DELETE FROM categories WHERE category_id = 1002;
DELETE FROM categories WHERE category_id = 1003;
DELETE FROM categories WHERE category_id = 1004;

GO

-- Bước 3: Bật IDENTITY_INSERT và INSERT lại với ID mới
SET IDENTITY_INSERT categories ON;

-- Category 6: Máy ảnh
INSERT INTO categories (category_id, category_name, description, image_url) 
VALUES 
(6, N'Máy ảnh', N'Máy ảnh DSLR, Mirrorless và máy ảnh kỹ thuật số', '/images/categories/camera.jpg');

-- Category 7: Loa
INSERT INTO categories (category_id, category_name, description, image_url) 
VALUES 
(7, N'Loa', N'Loa Bluetooth, loa không dây và loa thông minh', '/images/categories/speaker.jpg');

-- Category 8: Phụ kiện
INSERT INTO categories (category_id, category_name, description, image_url) 
VALUES 
(8, N'Phụ kiện', N'Ốp lưng, sạc dự phòng, cáp sạc và phụ kiện điện tử', '/images/categories/accessories.jpg');

-- Tắt IDENTITY_INSERT sau khi chèn xong
SET IDENTITY_INSERT categories OFF;

GO

-- Kiểm tra số lượng categories
SELECT COUNT(*) AS total_categories 
FROM categories;

-- Xem danh sách tất cả categories
SELECT 
    category_id, 
    category_name, 
    description,
    image_url
FROM categories 
ORDER BY category_id;

