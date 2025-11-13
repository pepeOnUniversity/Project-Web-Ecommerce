# 🔍 Hướng Dẫn Về Ngrok Warning Page

## ❓ Trang Cảnh Báo Ngrok Là Gì?

Khi bạn truy cập URL ngrok lần đầu tiên trên trình duyệt, bạn sẽ thấy một trang cảnh báo của ngrok. **Đây KHÔNG PHẢI là lỗi!**

## 🎯 Tại Sao Có Trang Này?

- Ngrok **free tier** hiển thị trang cảnh báo để:
  - Bảo vệ người dùng khỏi các trang web không an toàn
  - Giới thiệu dịch vụ ngrok
  - Yêu cầu người dùng xác nhận trước khi truy cập

## 📸 Trang Cảnh Báo Trông Như Thế Nào?

Trang cảnh báo thường có:
- Tiêu đề: **"You are about to visit..."** hoặc **"ngrok - Visit Site"**
- URL của trang bạn muốn truy cập
- Nút **"Visit Site"** hoặc **"Continue"**
- Logo và thông tin về ngrok

**⚠️ LƯU Ý:** Trang này có thể trông giống như yêu cầu cài đặt phần mềm, nhưng **KHÔNG PHẢI**!

## ✅ Cách Xử Lý

### Khi Test Thủ Công (Truy Cập Trên Trình Duyệt)

1. Mở URL ngrok: `https://YOUR-URL.ngrok-free.app/WebEcommerce`
2. Thấy trang cảnh báo → **Click nút "Visit Site"** hoặc **"Continue"**
3. Trang web của bạn sẽ hiển thị bình thường

### Khi VNPay Gửi Callback

**KHÔNG CẦN LÀM GÌ!** VNPay sẽ tự động bypass trang cảnh báo này khi gửi callback về server của bạn.

## 🔄 Làm Sao Để Bỏ Qua Trang Cảnh Báo?

### Cách 1: Nâng Cấp Ngrok Pro (Có Phí)
- Ngrok Pro plan không có warning page
- Giá: $8/tháng
- Có fixed domain (URL không đổi)

### Cách 2: Chấp Nhận (Miễn Phí)
- Click "Visit Site" mỗi lần test
- VNPay tự động bypass khi gửi callback
- **→ Không ảnh hưởng đến chức năng thanh toán!**

## ⚠️ Lưu Ý Quan Trọng

1. **Trang cảnh báo KHÔNG phải là lỗi**
   - Đây là tính năng bình thường của ngrok free tier
   - Không ảnh hưởng đến chức năng thanh toán

2. **VNPay tự động bypass**
   - Khi VNPay gửi callback, nó sẽ tự động bypass warning page
   - Bạn không cần làm gì thêm

3. **Chỉ xuất hiện lần đầu**
   - Mỗi URL ngrok mới sẽ có warning page lần đầu
   - Sau khi click "Visit Site", có thể không thấy nữa (tùy browser)

## 🐛 Nếu Gặp Vấn Đề

### Vấn Đề: Không Thấy Nút "Visit Site"

**Giải pháp:**
- Scroll xuống để tìm nút
- Hoặc thử refresh trang (F5)
- Hoặc thử trình duyệt khác

### Vấn Đề: Click "Visit Site" Nhưng Vẫn Không Vào Được

**Nguyên nhân có thể:**
1. **Tomcat chưa chạy**
   - Kiểm tra: `http://localhost:9999/WebEcommerce`
   - Nếu không vào được → Tomcat chưa chạy

2. **Port không đúng**
   - Script expose port 9999
   - Nếu Tomcat chạy port khác → Sửa script

3. **Ngrok đã dừng**
   - Kiểm tra: `http://localhost:4040`
   - Nếu không vào được → Ngrok đã dừng

**Giải pháp:**
- Khởi động lại Tomcat
- Khởi động lại ngrok
- Kiểm tra port trong script

## 📝 Checklist

Khi test URL ngrok:
- [ ] Đã click "Visit Site" trên warning page
- [ ] Trang web hiển thị bình thường
- [ ] Có thể truy cập các trang trong ứng dụng
- [ ] VNPay callback hoạt động (không cần click gì)

## 🔗 Xem Thêm

- Hướng dẫn đầy đủ: `HUONG_DAN_CHAY_NGROK_LOCAL.md`
- Quick start: `QUICK_START_NGROK.md`
- Ngrok documentation: https://ngrok.com/docs



