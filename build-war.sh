#!/bin/bash

# =============================================
# Build WAR File for WebEcommerce
# =============================================
# 
# Script này sẽ build WAR file từ NetBeans project
# Sử dụng Apache Ant để build project
#
# =============================================

echo "============================================="
echo "Building WebEcommerce WAR File"
echo "============================================="
echo ""

# Kiểm tra Ant có được cài đặt không
if ! command -v ant &> /dev/null; then
    echo "[ERROR] Apache Ant chưa được cài đặt!"
    echo "Vui lòng cài đặt Apache Ant hoặc sử dụng NetBeans để build."
    echo ""
    echo "Ubuntu/Debian: sudo apt install ant"
    echo "Mac: brew install ant"
    echo "Download: https://ant.apache.org/"
    exit 1
fi

echo "[INFO] Đang build project..."
echo ""

# Build project
ant dist

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Build thất bại!"
    exit 1
fi

echo ""
echo "============================================="
echo "Build thành công!"
echo "============================================="
echo ""
echo "WAR file đã được tạo tại: dist/WebEcommerce.war"
echo ""
echo "Bạn có thể upload file này lên VPS của inet.vn"
echo ""


