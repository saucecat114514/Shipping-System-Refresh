# 简化的订单测试
$baseUrl = "http://localhost:8080/api"

Write-Host "测试基本接口..." -ForegroundColor Green

try {
    # 测试登录
    $loginData = @{
        username = "admin"
        password = "123456"
    } | ConvertTo-Json

    Write-Host "尝试登录，数据: $loginData" -ForegroundColor Cyan

    $headers = @{
        "Content-Type" = "application/json"
    }

    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -Headers $headers
    Write-Host "登录响应: $($loginResponse | ConvertTo-Json -Depth 2)" -ForegroundColor Cyan
    
    if ($loginResponse.code -eq 200) {
        $token = $loginResponse.data.token
        Write-Host "✅ 登录成功，Token: $token" -ForegroundColor Green
        
        $authHeaders = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }

        # 测试获取订单列表
        try {
            Write-Host "测试获取订单列表..." -ForegroundColor Yellow
            $ordersResponse = Invoke-RestMethod -Uri "$baseUrl/orders" -Method GET -Headers $authHeaders
            Write-Host "✅ 获取订单列表成功: $($ordersResponse.data.total) 条记录" -ForegroundColor Green
        } catch {
            Write-Host "❌ 获取订单列表失败: $($_.Exception.Message)" -ForegroundColor Red
        }

        # 测试简单的订单创建
        try {
            Write-Host "测试订单创建..." -ForegroundColor Yellow
            $simpleOrderData = @{
                orderNumber = "ORD$(Get-Date -Format 'yyyyMMddHHmmss')TEST"
                customerId = 1
                cargoName = "测试货物"
                cargoType = "普通货物"
                cargoWeight = 25.5
                cargoVolume = 50.0
            } | ConvertTo-Json

            Write-Host "订单数据: $simpleOrderData" -ForegroundColor Cyan
            $createResponse = Invoke-RestMethod -Uri "$baseUrl/orders" -Method POST -Body $simpleOrderData -Headers $authHeaders
            Write-Host "✅ 订单创建成功: $($createResponse | ConvertTo-Json -Depth 2)" -ForegroundColor Green
        } catch {
            Write-Host "❌ 订单创建失败: $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ 登录失败: $($loginResponse.msg)" -ForegroundColor Red
    }

} catch {
    Write-Host "❌ 测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "测试完成!" -ForegroundColor Green 