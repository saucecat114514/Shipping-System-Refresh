#!/usr/bin/env pwsh

Write-Host "等待后端启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "测试API连接..." -ForegroundColor Green

# 测试健康检查
try {
    Write-Host "测试 GET /api/test/health" -ForegroundColor Cyan
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/test/health" -Method GET -TimeoutSec 5
    Write-Host "✅ 健康检查成功!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 3) -ForegroundColor Gray
} catch {
    Write-Host "❌ 健康检查失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试端口状态
Write-Host "`n检查端口状态:" -ForegroundColor Cyan
$portCheck = netstat -an | Select-String "8080"
if ($portCheck) {
    Write-Host "✅ 端口8080正在监听" -ForegroundColor Green
    Write-Host $portCheck -ForegroundColor Gray
} else {
    Write-Host "❌ 端口8080未在监听" -ForegroundColor Red
}

# 测试进程状态
Write-Host "`n检查Java进程:" -ForegroundColor Cyan
$javaProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcess) {
    Write-Host "✅ Java进程运行中" -ForegroundColor Green
    Write-Host "PID: $($javaProcess.Id), 内存: $([math]::Round($javaProcess.WorkingSet64/1MB))MB" -ForegroundColor Gray
} else {
    Write-Host "❌ 未找到Java进程" -ForegroundColor Red
}

Write-Host "`n测试完成!" -ForegroundColor Yellow 