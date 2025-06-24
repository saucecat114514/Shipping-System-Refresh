#!/usr/bin/env pwsh

Write-Host "开始测试API连接..." -ForegroundColor Green

# 等待应用启动
Write-Host "等待应用启动(15秒)..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# 检查端口
Write-Host "`n检查端口状态:" -ForegroundColor Cyan
$ports = @(8080, 8081)
foreach ($port in $ports) {
    $portCheck = netstat -an | Select-String ":$port"
    if ($portCheck) {
        Write-Host "✅ 端口$port正在监听" -ForegroundColor Green
        
        # 测试对应端口的API
        try {
            Write-Host "测试 http://localhost:$port/api/test/health" -ForegroundColor Cyan
            $response = Invoke-RestMethod -Uri "http://localhost:$port/api/test/health" -Method GET -TimeoutSec 3
            Write-Host "✅ API响应成功!" -ForegroundColor Green
            Write-Host ($response | ConvertTo-Json -Depth 2) -ForegroundColor Gray
            break
        } catch {
            Write-Host "❌ API调用失败: $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ 端口$port未在监听" -ForegroundColor Red
    }
}

Write-Host "`n测试完成!" -ForegroundColor Yellow 