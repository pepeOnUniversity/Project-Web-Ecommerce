# =============================================
# Script de chay ngrok cho VNPay Callback
# =============================================
# 
# Script nay se:
# 1. Doc ngrok authtoken tu config.properties
# 2. Config ngrok voi authtoken
# 3. Chay ngrok de expose localhost:9999
# 4. Hien thi URL ngrok de ban co the cap nhat vao VNPay
#
# =============================================
# 
# LUU Y: Script nay KHONG yeu cau cai dat Tomcat hay bat ky gi khac
# Chi can: ngrok.exe, PowerShell (co san trong Windows), va config.properties
# =============================================

# Set ErrorActionPreference de khong dung script neu co loi
$ErrorActionPreference = "Continue"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "NGROK SETUP FOR VNPAY CALLBACK" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

# Doc config.properties
$configPath = "src\java\config.properties"
if (-not (Test-Path $configPath)) {
    Write-Host "ERROR: Khong tim thay file config.properties tai: $configPath" -ForegroundColor Red
    exit 1
}

# Doc ngrok authtoken tu config
$configContent = Get-Content $configPath -Raw -Encoding UTF8
$ngrokToken = ""
if ($configContent -match "ngrok\.authtoken=(.+)") {
    $ngrokToken = $matches[1].Trim()
    # Loai bo comment neu co
    if ($ngrokToken -match "^(.+?)(\s*#|$)") {
        $ngrokToken = $matches[1].Trim()
    }
}

if ([string]::IsNullOrEmpty($ngrokToken)) {
    Write-Host "ERROR: Khong tim thay ngrok.authtoken trong config.properties" -ForegroundColor Red
    Write-Host "Vui long them ngrok.authtoken vao file config.properties" -ForegroundColor Yellow
    exit 1
}

Write-Host "[OK] Da doc ngrok authtoken tu config.properties" -ForegroundColor Green
Write-Host ""

# Tim ngrok.exe - uu tien tim trong thu muc hien tai truoc
$ngrokExe = $null
$currentDir = Get-Location

# Tim trong thu muc hien tai
$localNgrok = Join-Path $currentDir "ngrok.exe"
if (Test-Path $localNgrok) {
    $ngrokExe = $localNgrok
    Write-Host "[OK] Da tim thay ngrok.exe trong thu muc hien tai" -ForegroundColor Green
} else {
    # Tim trong PATH
    $ngrokPath = Get-Command ngrok -ErrorAction SilentlyContinue
    if ($ngrokPath) {
        $ngrokExe = $ngrokPath.Source
        Write-Host "[OK] Da tim thay ngrok trong PATH" -ForegroundColor Green
    }
}

if (-not $ngrokExe) {
    Write-Host "ERROR: Khong tim thay ngrok.exe" -ForegroundColor Red
    Write-Host ""
    Write-Host "Vui long dat file ngrok.exe vao thu muc: $currentDir" -ForegroundColor Yellow
    Write-Host "Hoac cai dat ngrok va them vao PATH" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""

# Config ngrok voi authtoken (chi can chay 1 lan)
Write-Host "Dang config ngrok voi authtoken..." -ForegroundColor Yellow
$configResult = & "$ngrokExe" config add-authtoken $ngrokToken 2>&1 | Out-String
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Da config ngrok authtoken thanh cong" -ForegroundColor Green
} else {
    # Co the da config roi, khong sao
    if ($configResult -match "already configured" -or $configResult -match "already exists") {
        Write-Host "[OK] Ngrok authtoken da duoc config truoc do" -ForegroundColor Green
    } else {
        Write-Host "WARNING: Co the co loi khi config ngrok: $configResult" -ForegroundColor Yellow
    }
}
Write-Host ""

# Kiem tra xem ngrok da chay chua
$ngrokProcess = Get-Process -Name ngrok -ErrorAction SilentlyContinue
if ($ngrokProcess) {
    Write-Host "WARNING: ngrok dang chay. Dang dung ngrok cu..." -ForegroundColor Yellow
    Stop-Process -Name ngrok -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    Write-Host "[OK] Da dung ngrok cu" -ForegroundColor Green
    Write-Host ""
}

# Port mac dinh (co the thay doi)
$port = 9999

# Kiem tra xem Tomcat co dang chay tren port nay khong
Write-Host "Dang kiem tra Tomcat tren port $port..." -ForegroundColor Yellow
$portInUse = $false
$appDeployed = $false
try {
    $connection = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue -InformationLevel Quiet -ErrorAction Stop
    if ($connection) {
        $portInUse = $true
        Write-Host "[OK] Port $port dang duoc su dung (Tomcat co the dang chay)" -ForegroundColor Green
        
        # Kiem tra xem ung dung co duoc deploy khong
        Write-Host "Dang kiem tra ung dung co duoc deploy khong..." -ForegroundColor Yellow
        try {
            $appResponse = Invoke-WebRequest -Uri "http://localhost:$port/" -UseBasicParsing -TimeoutSec 3 -ErrorAction Stop
            if ($appResponse.Content -match "WebEcommerce|Ecommerce|Home|Product" -or $appResponse.StatusCode -eq 200) {
                $appDeployed = $true
                Write-Host "[OK] Ung dung da duoc deploy va dang chay" -ForegroundColor Green
            } elseif ($appResponse.Content -match "Apache Tomcat") {
                Write-Host "[WARNING] Chi thay trang mac dinh cua Tomcat, ung dung chua duoc deploy!" -ForegroundColor Red
                Write-Host ""
                Write-Host "VUI LONG:" -ForegroundColor Yellow
                Write-Host "  1. Mo NetBeans" -ForegroundColor White
                Write-Host "  2. Stop Tomcat (nhan Stop hoac Shift+F5)" -ForegroundColor White
                Write-Host "  3. Clean and Build project (Shift+F11)" -ForegroundColor White
                Write-Host "  4. Start lai Tomcat (F6)" -ForegroundColor White
                Write-Host "  5. Cho den khi thay ung dung chay o http://localhost:$port/" -ForegroundColor White
                Write-Host "  6. Sau do chay lai script nay" -ForegroundColor White
                Write-Host ""
                $response = Read-Host "Ban co muon tiep tuc khong? (Y/N - Khuyen nghi: N)"
                if ($response -ne "Y" -and $response -ne "y") {
                    Write-Host "Da huy. Vui long deploy ung dung truoc khi chay ngrok." -ForegroundColor Yellow
                    exit 0
                }
            }
        } catch {
            Write-Host "[WARNING] Khong the kiem tra ung dung. Co the ung dung chua duoc deploy." -ForegroundColor Yellow
            Write-Host "         Vui long dam bao ung dung da duoc deploy trong NetBeans truoc khi tiep tuc." -ForegroundColor Yellow
            Write-Host ""
        }
    } else {
        Write-Host "[WARNING] Port $port khong co service nao dang chay!" -ForegroundColor Yellow
        Write-Host "         Tomcat co the chua chay hoac dang chay tren port khac" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Vui long kiem tra:" -ForegroundColor Yellow
        Write-Host "  1. Tomcat co dang chay khong? (Kiem tra trong NetBeans hoac Task Manager)" -ForegroundColor White
        Write-Host "  2. Tomcat dang chay tren port nao? (Mac dinh: 8080 hoac 9999)" -ForegroundColor White
        Write-Host "  3. Neu Tomcat chay tren port khac, vui long sua dong 107 trong script: `$port = YOUR_PORT" -ForegroundColor White
        Write-Host ""
        $response = Read-Host "Ban co muon tiep tuc khong? (Y/N)"
        if ($response -ne "Y" -and $response -ne "y") {
            Write-Host "Da huy. Vui long khoi dong Tomcat truoc khi chay ngrok." -ForegroundColor Yellow
            exit 0
        }
    }
} catch {
    Write-Host "[WARNING] Khong the kiem tra port $port. Co the port khong duoc su dung." -ForegroundColor Yellow
    Write-Host "         Vui long dam bao Tomcat dang chay truoc khi tiep tuc." -ForegroundColor Yellow
    Write-Host ""
}

Write-Host ""
Write-Host "Dang khoi dong ngrok de expose localhost:$port..." -ForegroundColor Yellow
Write-Host ""

# Chay ngrok trong background
Start-Process -FilePath "$ngrokExe" -ArgumentList "http", $port -WindowStyle Minimized

# Doi ngrok khoi dong
Start-Sleep -Seconds 3

# Lay URL tu ngrok API
Write-Host "Dang lay URL tu ngrok..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:4040/api/tunnels" -Method Get -ErrorAction Stop
    if ($response.tunnels -and $response.tunnels.Count -gt 0) {
        $publicUrl = $response.tunnels[0].public_url
        Write-Host ""
        Write-Host "=============================================" -ForegroundColor Green
        Write-Host "[OK] NGROK DA KHOI DONG THANH CONG!" -ForegroundColor Green
        Write-Host "=============================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Public URL: $publicUrl" -ForegroundColor Cyan
        Write-Host ""
        
        # Tu dong cap nhat URL vao config.properties
        Write-Host "Dang cap nhat URL vao config.properties..." -ForegroundColor Yellow
        $configFile = "src\java\config.properties"
        if (Test-Path $configFile) {
            $configContent = Get-Content $configFile -Raw -Encoding UTF8
            if ($configContent -match "(?m)^ngrok\.url=.*$") {
                # Thay the dong cu
                $configContent = $configContent -replace "(?m)^ngrok\.url=.*$", "ngrok.url=$publicUrl"
                Set-Content -Path $configFile -Value $configContent -Encoding UTF8 -NoNewline
                Write-Host "[OK] Da cap nhat ngrok.url=$publicUrl vao config.properties" -ForegroundColor Green
            } else {
                # Them dong moi neu chua co
                if (-not $configContent.EndsWith("`n") -and -not $configContent.EndsWith("`r`n")) {
                    $configContent += "`r`n"
                }
                $configContent += "ngrok.url=$publicUrl`r`n"
                Set-Content -Path $configFile -Value $configContent -Encoding UTF8 -NoNewline
                Write-Host "[OK] Da them ngrok.url=$publicUrl vao config.properties" -ForegroundColor Green
            }
        } else {
            Write-Host "WARNING: Khong tim thay file config.properties" -ForegroundColor Yellow
        }
        
        Write-Host ""
        Write-Host "Cac URL callback cho VNPay:" -ForegroundColor Yellow
        Write-Host "  Return URL: $publicUrl/vnpay-return" -ForegroundColor White
        Write-Host "  IPN URL:    $publicUrl/vnpay-ipn" -ForegroundColor White
        Write-Host ""
        Write-Host "LUU Y:" -ForegroundColor Yellow
        Write-Host "1. URL da duoc tu dong cap nhat vao config.properties" -ForegroundColor Green
        Write-Host "2. Neu muon dung system property: -Dvnpay.ngrok.url=$publicUrl" -ForegroundColor White
        Write-Host "3. Restart Tomcat de ap dung thay doi (neu can)" -ForegroundColor White
        Write-Host ""
        Write-Host "QUAN TRONG - NGROK WARNING PAGE:" -ForegroundColor Yellow
        Write-Host "Khi truy cap URL ngrok lan dau tien tren trinh duyet, ban se thay:" -ForegroundColor White
        Write-Host "  - Trang canh bao cua ngrok (free tier)" -ForegroundColor White
        Write-Host "  - Co the co text 'You are about to visit...' hoac 'ngrok - Visit Site'" -ForegroundColor White
        Write-Host "  - Day KHONG PHAI la loi, chi la trang canh bao binh thuong!" -ForegroundColor Green
        Write-Host "  - Click nut 'Visit Site' hoac 'Continue' de tiep tuc" -ForegroundColor Green
        Write-Host "  - VNPay se tu dong bypass trang nay khi gui callback" -ForegroundColor Green
        Write-Host ""
        Write-Host "KIEM TRA:" -ForegroundColor Yellow
        Write-Host "  - Tomcat dang chay: http://localhost:$port/" -ForegroundColor White
        Write-Host "  - Ngrok URL: $publicUrl/" -ForegroundColor White
        Write-Host "  - Ngrok dashboard: http://localhost:4040" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "De dung ngrok: Nhan Ctrl+C hoac dong cua so nay" -ForegroundColor Yellow
        Write-Host ""
    } else {
        Write-Host "WARNING: Khong lay duoc URL tu ngrok. Vui long kiem tra tai http://localhost:4040" -ForegroundColor Yellow
    }
} catch {
    Write-Host "WARNING: Khong the ket noi den ngrok API. Ngrok co the dang khoi dong..." -ForegroundColor Yellow
    Write-Host "Vui long kiem tra tai: http://localhost:4040" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Hoac chay lenh sau de xem URL:" -ForegroundColor Yellow
    Write-Host "  curl http://localhost:4040/api/tunnels" -ForegroundColor White
}

Write-Host ""
Write-Host "Ngrok dang chay. Nhan Ctrl+C de dung..." -ForegroundColor Cyan

# Giu script chay de ngrok khong bi dung
try {
    while ($true) {
        Start-Sleep -Seconds 10
        $ngrokProcess = Get-Process -Name ngrok -ErrorAction SilentlyContinue
        if (-not $ngrokProcess) {
            Write-Host "ngrok da dung." -ForegroundColor Yellow
            break
        }
    }
} catch {
    Write-Host "`nDang dung ngrok..." -ForegroundColor Yellow
    Stop-Process -Name ngrok -Force -ErrorAction SilentlyContinue
    Write-Host "[OK] Da dung ngrok" -ForegroundColor Green
}
