# 测试航线显示和计算功能的脚本
Write-Host "=== 测试航线显示和计算功能 ===" -ForegroundColor Green

# 后端API基础URL
$baseUrl = "http://localhost:8080"

Write-Host "1. 测试获取包含港口信息的航线列表..." -ForegroundColor Yellow

try {
    # 测试包含港口信息的航线列表接口
    $routesResponse = Invoke-RestMethod -Uri "$baseUrl/routes/with-ports" -Method Get
    Write-Host "✓ 航线列表获取成功，共 $($routesResponse.data.records.Count) 条航线" -ForegroundColor Green
    
    if ($routesResponse.data.records.Count -gt 0) {
        $route = $routesResponse.data.records[0]
        Write-Host "  示例航线:" -ForegroundColor Cyan
        Write-Host "    航线编号: $($route.routeNumber)" -ForegroundColor Cyan
        Write-Host "    航线名称: $($route.name)" -ForegroundColor Cyan
        
        if ($route.originPort -and $route.destinationPort) {
            Write-Host "    起始港口: $($route.originPort.nameCn) ($($route.originPort.code))" -ForegroundColor Green
            Write-Host "    目标港口: $($route.destinationPort.nameCn) ($($route.destinationPort.code))" -ForegroundColor Green
        } else {
            Write-Host "    港口信息: 未包含港口详情" -ForegroundColor Red
        }
        
        Write-Host "    航程距离: $($route.distance) 公里" -ForegroundColor Cyan
        Write-Host "    航程海里: $($route.distanceNm) 海里" -ForegroundColor Cyan
        Write-Host "    预计时间: $($route.estimatedDuration) 小时" -ForegroundColor Cyan
    }
    
    Write-Host "`n2. 测试港口列表获取..." -ForegroundColor Yellow
    
    # 获取港口列表
    $portsResponse = Invoke-RestMethod -Uri "$baseUrl/ports/all" -Method Get
    Write-Host "✓ 港口列表获取成功，共 $($portsResponse.data.Count) 个港口" -ForegroundColor Green
    
    if ($portsResponse.data.Count -ge 2) {
        $port1 = $portsResponse.data[0]
        $port2 = $portsResponse.data[1]
        
        Write-Host "  示例港口 1:" -ForegroundColor Cyan
        Write-Host "    ID: $($port1.id), 名称: $($port1.nameCn), 代码: $($port1.code)" -ForegroundColor Cyan
        Write-Host "    坐标: ($($port1.latitude), $($port1.longitude))" -ForegroundColor Cyan
        
        Write-Host "  示例港口 2:" -ForegroundColor Cyan
        Write-Host "    ID: $($port2.id), 名称: $($port2.nameCn), 代码: $($port2.code)" -ForegroundColor Cyan
        Write-Host "    坐标: ($($port2.latitude), $($port2.longitude))" -ForegroundColor Cyan
        
        # 测试距离计算（模拟前端计算）
        Write-Host "`n3. 测试距离计算..." -ForegroundColor Yellow
        
        $lat1 = [double]$port1.latitude
        $lon1 = [double]$port1.longitude
        $lat2 = [double]$port2.latitude
        $lon2 = [double]$port2.longitude
        
        # Haversine公式计算距离
        $R = 6371  # 地球半径，公里
        $dLat = ($lat2 - $lat1) * [Math]::PI / 180
        $dLon = ($lon2 - $lon1) * [Math]::PI / 180
        $a = [Math]::Sin($dLat/2) * [Math]::Sin($dLat/2) + [Math]::Cos($lat1 * [Math]::PI / 180) * [Math]::Cos($lat2 * [Math]::PI / 180) * [Math]::Sin($dLon/2) * [Math]::Sin($dLon/2)
        $c = 2 * [Math]::Atan2([Math]::Sqrt($a), [Math]::Sqrt(1-$a))
        $distance = $R * $c
        
        # 转换为海里
        $distanceNm = $distance * 0.539957
        
        # 计算航行时间（假设12节速度）
        $estimatedHours = $distanceNm / 12
        
        Write-Host "✓ 距离计算完成:" -ForegroundColor Green
        Write-Host "    两港口间距离: $([Math]::Round($distance, 2)) 公里" -ForegroundColor Green
        Write-Host "    两港口间距离: $([Math]::Round($distanceNm, 2)) 海里" -ForegroundColor Green
        Write-Host "    预计航行时间: $([Math]::Round($estimatedHours, 2)) 小时" -ForegroundColor Green
    }
    
} catch {
    Write-Host "✗ 测试失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请确保后端服务正在运行在 http://localhost:8080" -ForegroundColor Yellow
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 