# Script de fix loi "Application already exists at path [/]"
# Loi nay xay ra khi co ung dung khac da duoc deploy o root path

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Fix Deployment Conflict" -ForegroundColor Cyan
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
    Write-Host "Cac lua chon:" -ForegroundColor Cyan
    Write-Host "  1. Backup va xoa ROOT (KHUYEN NGHI)" -ForegroundColor White
    Write-Host "  2. Doi ten ROOT thanh ROOT_backup" -ForegroundColor White
    Write-Host "  3. Bo qua (khong lam gi)" -ForegroundColor White
    Write-Host ""
    
    $choice = Read-Host "Chon (1/2/3)"
    
    switch ($choice) {
        "1" {
            $backupPath = Join-Path $tomcatWebapps "ROOT_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')"
            Write-Host "Dang backup ROOT den: $backupPath" -ForegroundColor Yellow
            try {
                Copy-Item -Path $rootPath -Destination $backupPath -Recurse -ErrorAction Stop
                Write-Host "[OK] Backup thanh cong" -ForegroundColor Green
                
                Write-Host "Dang xoa ROOT..." -ForegroundColor Yellow
                Remove-Item -Path $rootPath -Recurse -Force -ErrorAction Stop
                Write-Host "[OK] Da xoa ROOT" -ForegroundColor Green
            } catch {
                Write-Host "[ERROR] Khong the backup/xoa ROOT: $($_.Exception.Message)" -ForegroundColor Red
                Write-Host "Vui long dong NetBeans va Tomcat, sau do chay lai script nay" -ForegroundColor Yellow
                exit 1
            }
        }
        "2" {
            $backupPath = Join-Path $tomcatWebapps "ROOT_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')"
            Write-Host "Dang doi ten ROOT thanh: ROOT_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')" -ForegroundColor Yellow
            try {
                Rename-Item -Path $rootPath -NewName "ROOT_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')" -ErrorAction Stop
                Write-Host "[OK] Da doi ten ROOT" -ForegroundColor Green
            } catch {
                Write-Host "[ERROR] Khong the doi ten ROOT: $($_.Exception.Message)" -ForegroundColor Red
                Write-Host "Vui long dong NetBeans va Tomcat, sau do chay lai script nay" -ForegroundColor Yellow
                exit 1
            }
        }
        "3" {
            Write-Host "[INFO] Bo qua, khong thay doi gi" -ForegroundColor Yellow
        }
        default {
            Write-Host "[ERROR] Lua chon khong hop le!" -ForegroundColor Red
            exit 1
        }
    }
} else {
    Write-Host "[OK] Khong tim thay thư muc ROOT" -ForegroundColor Green
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
Write-Host "  - Stop Tomcat hoan toan" -ForegroundColor White
Write-Host "  - Xoa thu muc build trong project" -ForegroundColor White
Write-Host "  - Clean and Build lai" -ForegroundColor White
Write-Host "  - Start Tomcat" -ForegroundColor White
Write-Host ""


