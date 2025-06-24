# 测试航线创建功能的脚本
Write-Host "=== 测试航线管理新功能 ===" -ForegroundColor Green

# 后端API基础URL
$baseUrl = "http://localhost:8080"

Write-Host "1. 测试获取港口列表..." -ForegroundColor Yellow

try {
    # 获取港口列表
    $portsResponse = Invoke-RestMethod -Uri "$baseUrl/routes/ports" -Method Get
    Write-Host "✓ 港口列表获取成功，共 $($portsResponse.data.Count) 个港口" -ForegroundColor Green
    
    if ($portsResponse.data.Count -ge 2) {
        $port1 = $portsResponse.data[0]
        $port2 = $portsResponse.data[1]
        
        Write-Host "  起始港口: $($port1.nameCn) ($($port1.code))" -ForegroundColor Cyan
        Write-Host "  目标港口: $($port2.nameCn) ($($port2.code))" -ForegroundColor Cyan
        
        Write-Host "`n2. 测试自动生成航线编号和计算距离..." -ForegroundColor Yellow
        
        # 创建航线（不提供航线编号和距离，让系统自动生成和计算）
        $routeData = @{
            name = "测试航线-$(Get-Date -Format 'MMddHHmmss')"
            originPortId = $port1.id
            destinationPortId = $port2.id
            status = 1
            description = "这是一条测试航线，用于验证自动生成功能"
        }
        
        $createResponse = Invoke-RestMethod -Uri "$baseUrl/routes" -Method Post -Body ($routeData | ConvertTo-Json) -ContentType "application/json"
        
        if ($createResponse.code -eq 200) {
            Write-Host "✓ 航线创建成功！" -ForegroundColor Green
            Write-Host "  自动生成的航线编号: $($createResponse.data.routeNumber)" -ForegroundColor Cyan
            Write-Host "  自动计算的距离: $($createResponse.data.distance) 公里" -ForegroundColor Cyan
            Write-Host "  自动计算的海里距离: $($createResponse.data.distanceNm) 海里" -ForegroundColor Cyan
            Write-Host "  自动计算的预计时间: $($createResponse.data.estimatedDuration) 小时" -ForegroundColor Cyan
            
            Write-Host "`n3. 验证创建的航线..." -ForegroundColor Yellow
            $routeId = $createResponse.data.id
            $getResponse = Invoke-RestMethod -Uri "$baseUrl/routes/$routeId" -Method Get
            
            if ($getResponse.code -eq 200) {
                Write-Host "✓ 航线查询成功！" -ForegroundColor Green
                Write-Host "  航线ID: $($getResponse.data.id)" -ForegroundColor Cyan
                Write-Host "  航线名称: $($getResponse.data.name)" -ForegroundColor Cyan
                Write-Host "  起始港口ID: $($getResponse.data.originPortId)" -ForegroundColor Cyan
                Write-Host "  目标港口ID: $($getResponse.data.destinationPortId)" -ForegroundColor Cyan
            }
            
        } else {
            Write-Host "✗ 航线创建失败: $($createResponse.msg)" -ForegroundColor Red
        }
        
    } else {
        Write-Host "✗ 港口数量不足，无法创建测试航线" -ForegroundColor Red
    }
    
} catch {
    Write-Host "✗ 测试失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请确保后端服务正在运行在 $baseUrl" -ForegroundColor Yellow
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 