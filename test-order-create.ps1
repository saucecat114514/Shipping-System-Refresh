# 测试订单创建功能
$baseUrl = "http://localhost:8080/api"

Write-Host "正在测试订单创建功能..." -ForegroundColor Green

try {
    # 1. 先登录一个客户用户
    Write-Host "1. 登录客户用户..." -ForegroundColor Yellow
    $loginData = @{
        username = "iambb"
        password = "123456"
    } | ConvertTo-Json

    $loginHeaders = @{
        "Content-Type" = "application/json"
    }

    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -Headers $loginHeaders
    Write-Host "登录响应: $($loginResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    if ($loginResponse.code -eq 200) {
        $token = $loginResponse.data.token
        Write-Host "获取到Token: $token" -ForegroundColor Green

        # 2. 获取当前用户信息
        Write-Host "2. 获取当前用户信息..." -ForegroundColor Yellow
        $authHeaders = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }

        $userResponse = Invoke-RestMethod -Uri "$baseUrl/auth/current-user" -Method GET -Headers $authHeaders
        Write-Host "用户信息响应: $($userResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

        # 3. 创建订单测试
        Write-Host "3. 创建订单..." -ForegroundColor Yellow
        $orderData = @{
            orderNumber = "ORD$(Get-Date -Format 'yyyyMMddHHmmss')"
            customerId = $userResponse.data.id
            cargoName = "测试货物"
            cargoType = "普通货物"
            cargoWeight = 25.5
            cargoVolume = 50.0
            isUrgent = $false
            notes = "测试订单创建功能"
        } | ConvertTo-Json

        Write-Host "订单数据: $orderData" -ForegroundColor Cyan

        $orderResponse = Invoke-RestMethod -Uri "$baseUrl/orders" -Method POST -Body $orderData -Headers $authHeaders
        Write-Host "订单创建响应: $($orderResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

        if ($orderResponse.code -eq 200) {
            Write-Host "✅ 订单创建成功！" -ForegroundColor Green
            Write-Host "客户ID已自动设置为: $($orderResponse.data.customerId)" -ForegroundColor Green
            Write-Host "客户真实姓名: $($userResponse.data.realName)" -ForegroundColor Green
        } else {
            Write-Host "❌ 订单创建失败: $($orderResponse.msg)" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ 登录失败: $($loginResponse.msg)" -ForegroundColor Red
    }

} catch {
    Write-Host "❌ 测试过程中发生错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "测试完成!" -ForegroundColor Green 