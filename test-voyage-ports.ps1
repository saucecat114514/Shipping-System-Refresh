# 测试航次选择、港口信息获取和订单创建功能
$baseUrl = "http://localhost:8080/api"

Write-Host "========== 测试航次选择和港口信息功能 ==========" -ForegroundColor Green

# 1. 用户登录
Write-Host "`n1. 客户用户登录..." -ForegroundColor Yellow
$loginData = @{
    username = "iambb"
    password = "123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    Write-Host "✅ 登录成功" -ForegroundColor Green
    $token = $loginResponse.data.token
} catch {
    Write-Host "❌ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. 获取航次列表
Write-Host "`n2. 获取航次列表..." -ForegroundColor Yellow
try {
    $voyageResponse = Invoke-RestMethod -Uri "$baseUrl/voyages?page=1&size=5" -Method GET -Headers $headers
    if ($voyageResponse.code -eq 200 -and $voyageResponse.data.records) {
        Write-Host "✅ 获取航次列表成功" -ForegroundColor Green
        $voyages = $voyageResponse.data.records
        
        Write-Host "可用航次：" -ForegroundColor Cyan
        foreach ($voyage in $voyages) {
            Write-Host "  ID: $($voyage.id), 编号: $($voyage.voyageNumber), 航线ID: $($voyage.routeId)" -ForegroundColor Gray
        }
        
        # 选择第一个航次进行测试
        if ($voyages.Count -gt 0) {
            $testVoyage = $voyages[0]
            Write-Host "`n选择测试航次: $($testVoyage.voyageNumber)" -ForegroundColor Yellow
        } else {
            Write-Host "❌ 没有可用的航次" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "❌ 获取航次列表失败: $($voyageResponse.msg)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ 获取航次列表失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. 获取航次详细信息（包含港口信息）
Write-Host "`n3. 获取航次详细信息（包含港口信息）..." -ForegroundColor Yellow
try {
    $voyageDetailResponse = Invoke-RestMethod -Uri "$baseUrl/voyages/$($testVoyage.id)/details" -Method GET -Headers $headers
    if ($voyageDetailResponse.code -eq 200 -and $voyageDetailResponse.data) {
        Write-Host "✅ 获取航次详细信息成功" -ForegroundColor Green
        $voyageDetail = $voyageDetailResponse.data
        
        Write-Host "航次详细信息：" -ForegroundColor Cyan
        Write-Host "  航次编号: $($voyageDetail.voyageNumber)" -ForegroundColor Gray
        Write-Host "  航线ID: $($voyageDetail.routeId)" -ForegroundColor Gray
        
        if ($voyageDetail.route) {
            Write-Host "  航线名称: $($voyageDetail.route.name)" -ForegroundColor Gray
            Write-Host "  航线距离: $($voyageDetail.route.distanceNm) 海里" -ForegroundColor Gray
            
            if ($voyageDetail.route.originPort) {
                Write-Host "  出发港口: $($voyageDetail.route.originPort.name) ($($voyageDetail.route.originPort.code))" -ForegroundColor Gray
                Write-Host "    港口国家: $($voyageDetail.route.originPort.country)" -ForegroundColor Gray
                Write-Host "    港口城市: $($voyageDetail.route.originPort.city)" -ForegroundColor Gray
            }
            
            if ($voyageDetail.route.destinationPort) {
                Write-Host "  目的港口: $($voyageDetail.route.destinationPort.name) ($($voyageDetail.route.destinationPort.code))" -ForegroundColor Gray
                Write-Host "    港口国家: $($voyageDetail.route.destinationPort.country)" -ForegroundColor Gray
                Write-Host "    港口城市: $($voyageDetail.route.destinationPort.city)" -ForegroundColor Gray
            }
        }
    } else {
        Write-Host "❌ 获取航次详细信息失败: $($voyageDetailResponse.msg)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ 获取航次详细信息失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 4. 测试基于航次的价格计算
Write-Host "`n4. 测试基于航次的价格计算..." -ForegroundColor Yellow

$priceData = @{
    cargoWeight = 10.0
    cargoVolume = 20.0
    cargoType = "普通货物"
    selectedVoyageId = $testVoyage.id
    isUrgent = $false
} | ConvertTo-Json

Write-Host "价格计算请求数据: $priceData" -ForegroundColor Gray

try {
    $priceResponse = Invoke-RestMethod -Uri "$baseUrl/orders/calculate-price" -Method POST -Body $priceData -Headers $headers
    
    if ($priceResponse.code -eq 200) {
        Write-Host "✅ 价格计算成功" -ForegroundColor Green
        Write-Host "  基础运费: ¥$($priceResponse.data.basePrice)" -ForegroundColor Cyan
        Write-Host "  附加费用: ¥$($priceResponse.data.additionalFees)" -ForegroundColor Cyan
        Write-Host "  总费用: ¥$($priceResponse.data.totalPrice)" -ForegroundColor Cyan
    } else {
        Write-Host "❌ 价格计算失败: $($priceResponse.msg)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 价格计算请求失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "错误详情: $responseBody" -ForegroundColor Red
    }
}

# 5. 测试创建订单（包含航次信息）
Write-Host "`n5. 测试创建订单（包含航次信息）..." -ForegroundColor Yellow

$now = Get-Date
$orderNumber = "ORD" + $now.ToString("yyyyMMddHHmmss")

$orderData = @{
    orderNumber = $orderNumber
    cargoName = "测试货物-港口信息"
    cargoType = "普通货物"
    cargoWeight = 8.0
    cargoVolume = 16.0
    selectedVoyageId = $testVoyage.id
    isUrgent = $false
    notes = "测试订单 - 包含航次和港口信息"
} | ConvertTo-Json

Write-Host "订单数据: $orderData" -ForegroundColor Gray

try {
    $orderResponse = Invoke-RestMethod -Uri "$baseUrl/orders" -Method POST -Body $orderData -Headers $headers
    if ($orderResponse.code -eq 200) {
        Write-Host "✅ 订单创建成功" -ForegroundColor Green
        Write-Host "订单ID: $($orderResponse.data.id)" -ForegroundColor Cyan
        Write-Host "订单编号: $($orderResponse.data.orderNumber)" -ForegroundColor Cyan
        Write-Host "关联航次ID: $($orderResponse.data.voyageId)" -ForegroundColor Cyan
        Write-Host "基础价格: $($orderResponse.data.basePrice)" -ForegroundColor Cyan
        Write-Host "总价格: $($orderResponse.data.totalPrice)" -ForegroundColor Cyan
    } else {
        Write-Host "❌ 订单创建失败: $($orderResponse.msg)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 订单创建失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "错误详情: $responseBody" -ForegroundColor Red
    }
}

# 6. 验证订单中的航次信息
Write-Host "`n6. 验证订单中的航次信息..." -ForegroundColor Yellow
if ($orderResponse -and $orderResponse.code -eq 200) {
    try {
        $orderDetailResponse = Invoke-RestMethod -Uri "$baseUrl/orders/$($orderResponse.data.id)" -Method GET -Headers $headers
        if ($orderDetailResponse.code -eq 200) {
            Write-Host "✅ 获取订单详情成功" -ForegroundColor Green
            $orderDetail = $orderDetailResponse.data
            Write-Host "订单航次ID: $($orderDetail.voyageId)" -ForegroundColor Cyan
            Write-Host "是否正确关联航次: $(if ($orderDetail.voyageId -eq $testVoyage.id) { '✅ 是' } else { '❌ 否' })" -ForegroundColor Cyan
        }
    } catch {
        Write-Host "❌ 获取订单详情失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n========== 测试完成 ==========" -ForegroundColor Green 