# 🔍 Kiểm Tra Application Path trên iNET Server

## 📋 Các Lệnh Cần Chạy

### Bước 1: Kiểm tra Home Directory hiện tại

```bash
pwd
```

**Kết quả mong đợi:**
```
/home/dtfiffwnhosting
```
hoặc
```
/home/dtfi
```

---

### Bước 2: Kiểm tra thư mục hiện tại

```bash
ls -la
```

**Giải thích:**
- Xem các thư mục và file trong home directory
- Tìm thư mục `webapps`, `tomcat`, `public_html`, hoặc `www`

---

### Bước 3: Tìm thư mục webapps

```bash
find ~ -name "webapps" -type d 2>/dev/null
```

**Giải thích:**
- Tìm tất cả thư mục tên `webapps` trong home directory
- `2>/dev/null` để ẩn các lỗi permission

**Kết quả có thể:**
```
/home/dtfiffwnhosting/webapps
/home/dtfiffwnhosting/tomcat/webapps
/home/dtfiffwnhosting/public_html/webapps
```

---

### Bước 4: Kiểm tra Tomcat (nếu có)

```bash
which tomcat
```

hoặc

```bash
ps aux | grep tomcat
```

hoặc

```bash
ls -la ~/tomcat
```

---

### Bước 5: Kiểm tra các thư mục phổ biến

```bash
# Kiểm tra webapps trực tiếp
ls -la ~/webapps

# Kiểm tra tomcat/webapps
ls -la ~/tomcat/webapps 2>/dev/null

# Kiểm tra public_html
ls -la ~/public_html

# Kiểm tra www
ls -la ~/www
```

---

### Bước 6: Xác định Application Path

**Sau khi chạy các lệnh trên, xác định:**

1. **Nếu home directory là:** `/home/dtfiffwnhosting`
2. **Và webapps ở:** `/home/dtfiffwnhosting/webapps`
3. **Thì Application Path là:** `/webapps`

**Hoặc:**

1. **Nếu home directory là:** `/home/dtfiffwnhosting`
2. **Và webapps ở:** `/home/dtfiffwnhosting/tomcat/webapps`
3. **Thì Application Path là:** `/tomcat/webapps`

---

## 🚀 Chạy Tất Cả Lệnh Một Lần

Copy và paste đoạn này vào terminal:

```bash
echo "=== Home Directory ==="
pwd
echo ""
echo "=== Thư mục hiện tại ==="
ls -la
echo ""
echo "=== Tìm webapps ==="
find ~ -name "webapps" -type d 2>/dev/null
echo ""
echo "=== Kiểm tra Tomcat ==="
which tomcat 2>/dev/null || echo "Tomcat không tìm thấy trong PATH"
ps aux | grep tomcat | grep -v grep || echo "Tomcat không chạy"
echo ""
echo "=== Kiểm tra các thư mục phổ biến ==="
[ -d ~/webapps ] && echo "✓ ~/webapps tồn tại" || echo "✗ ~/webapps không tồn tại"
[ -d ~/tomcat/webapps ] && echo "✓ ~/tomcat/webapps tồn tại" || echo "✗ ~/tomcat/webapps không tồn tại"
[ -d ~/public_html ] && echo "✓ ~/public_html tồn tại" || echo "✗ ~/public_html không tồn tại"
[ -d ~/www ] && echo "✓ ~/www tồn tại" || echo "✗ ~/www không tồn tại"
```

---

## 📝 Ví Dụ Kết Quả

### Trường hợp 1: webapps ở home directory

```bash
$ pwd
/home/dtfiffwnhosting

$ find ~ -name "webapps" -type d
/home/dtfiffwnhosting/webapps
```

**→ Application Path:** `/webapps`

---

### Trường hợp 2: webapps trong tomcat

```bash
$ pwd
/home/dtfiffwnhosting

$ find ~ -name "webapps" -type d
/home/dtfiffwnhosting/tomcat/webapps
```

**→ Application Path:** `/tomcat/webapps`

---

### Trường hợp 3: Không có webapps

```bash
$ find ~ -name "webapps" -type d
(không có kết quả)
```

**→ Cần tạo thư mục webapps:**
```bash
mkdir -p ~/webapps
```

**→ Application Path:** `/webapps`

---

## ⚠️ Lưu Ý

1. **Nếu không tìm thấy webapps:**
   - Có thể iNET dùng cấu trúc khác (ví dụ: `public_html`, `www`)
   - Liên hệ iNET support để hỏi đường dẫn chính xác

2. **Nếu có nhiều webapps:**
   - Chọn thư mục webapps của Tomcat (thường là `~/tomcat/webapps`)
   - Hoặc thư mục webapps chính (thường là `~/webapps`)

3. **Kiểm tra quyền truy cập:**
   ```bash
   ls -la ~/webapps
   ```
   - Đảm bảo bạn có quyền đọc/ghi vào thư mục này

---

## 🆘 Cần Hỗ Trợ?

Nếu vẫn không chắc, liên hệ iNET support:
- Email: support@inet.vn
- Ticket: Đăng nhập client area
- Hỏi: "Đường dẫn webapps trên server của tôi là gì? Home directory của tôi là gì?"


