# Script tu dong fix loi "Application already exists at path [/]"
# Script nay se tu dong backup va xoa ROOT ma khong can input

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Fix Deployment Conflict (Auto)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Tim Tomcat installation
$tomcatPaths = @(
    "C:\Program Files\Apache Software Foundation\Tomcat 10.1_Tomcat10.1",
    "C:\Program Files\Apache Software Foundation\Tomcat 10.1",
    "C:\Program Files\Apache Software Foundation\Tomcat*\webapps"
)

$tomcatWebapps = $null
foreach ($path in $tomcatPaths) {
    $fullPath = Join-Path $path "webapps"
    if (Test-Path $fullPath) {
        $tomcatWebapps = $fullPath
        break
    }
}

if (-not $tomcatWebapps) {
    Write-Host "[ERROR] Khong tim thay thư muc webapps cua Tomcat!" -ForegroundColor Red
    Write-Host "Vui long nhap duong dan den thư muc webapps cua Tomcat:" -ForegroundColor Yellow
    $tomcatWebapps = Read-Host "Duong dan"
    
    if (-not (Test-Path $tomcatWebapps)) {
        Write-Host "[ERROR] Duong dan khong hop le!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "[INFO] Tim thay Tomcat webapps: $tomcatWebapps" -ForegroundColor Green
Write-Host ""

# Kiem tra ROOT folder
$rootPath = Join-Path $tomcatWebapps "ROOT"
if (Test-Path $rootPath) {
    Write-Host "[WARNING] Tim thay thư muc ROOT (ung dung mac dinh cua Tomcat)" -ForegroundColor Yellow
    Write-Host "         Day la nguyen nhan gay ra loi 'Application already exists at path [/]'" -ForegroundColor Yellow
    Write-Host ""
    
    # Tu dong backup va xoa
    $backupPath = Join-Path $tomcatWebapps "ROOT_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')"
    Write-Host "Dang backup ROOT den: $backupPath" -ForegroundColor Yellow
    try {
        Copy-Item -Path $rootPath -Destination $backupPath -Recurse -ErrorAction Stop
        Write-Host "[OK] Backup thanh cong" -ForegroundColor Green
        
        Write-Host "Dang xoa ROOT..." -ForegroundColor Yellow
        Remove-Item -Path $rootPath -Recurse -Force -ErrorAction Stop
        Write-Host "[OK] Da xoa ROOT" -ForegroundColor Green
        Write-Host ""
        Write-Host "[SUCCESS] Da fix xong! Ban co the deploy ung dung trong NetBeans." -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Khong the backup/xoa ROOT: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        Write-Host "Co the Tomcat dang chay hoac file dang duoc su dung." -ForegroundColor Yellow
        Write-Host "Vui long:" -ForegroundColor Yellow
        Write-Host "  1. DONG NetBeans va Tomcat hoan toan" -ForegroundColor White
        Write-Host "  2. Kill process Java (neu can):" -ForegroundColor White
        Write-Host "     Get-Process java | Stop-Process -Force" -ForegroundColor Gray
        Write-Host "  3. Chay lai script nay" -ForegroundColor White
        exit 1
    }
} else {
    Write-Host "[OK] Khong tim thay thư muc ROOT - Khong co conflict!" -ForegroundColor Green
    Write-Host "     Ban co the deploy ung dung binh thuong trong NetBeans." -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Buoc tiep theo" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. DONG NetBeans va Tomcat (neu dang chay)" -ForegroundColor Yellow
Write-Host "2. Mo lai NetBeans" -ForegroundColor Yellow
Write-Host "3. Clean and Build project (Shift+F11)" -ForegroundColor Yellow
Write-Host "4. Start Tomcat (F6)" -ForegroundColor Yellow
Write-Host "5. Kiem tra: http://localhost:9999/" -ForegroundColor Yellow
Write-Host ""
Write-Host "Neu van gap loi, vui long:" -ForegroundColor Cyan
Write-Host "  - Stop Tomcat hoan toan (kill process Java)" -ForegroundColor White
Write-Host "  - Xoa thu muc build trong project" -ForegroundColor White
Write-Host "  - Clean and Build lai" -ForegroundColor White
Write-Host "  - Start Tomcat" -ForegroundColor White
Write-Host ""


