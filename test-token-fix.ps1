Write-Host "=== 航运系统Token修复验证测试 ===" -ForegroundColor Green
Write-Host ""

# 测试基本连接
Write-Host "1. 测试后端服务连接..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/test/health" -Method GET
    Write-Host "✓ 后端服务连接正常" -ForegroundColor Green
    Write-Host "响应: $($response.msg)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 后端服务连接失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 测试登录获取token
Write-Host "2. 测试用户登录..." -ForegroundColor Yellow
$loginData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/login" -Method POST -Body $loginData -ContentType "application/json"
    if ($loginResponse.code -eq 200) {
        $token = $loginResponse.data.token
        Write-Host "✓ 登录成功，获取到token" -ForegroundColor Green
        Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    } else {
        Write-Host "✗ 登录失败: $($loginResponse.msg)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ 登录请求失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 测试需要token的API
Write-Host "3. 测试需要token的API..." -ForegroundColor Yellow

# 测试港口列表API
try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $portsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/ports/all" -Method GET -Headers $headers
    if ($portsResponse.code -eq 200) {
        $portCount = $portsResponse.data.Count
        Write-Host "✓ 港口API调用成功，共 $portCount 个港口" -ForegroundColor Green
    } else {
        Write-Host "✗ 港口API调用失败: $($portsResponse.msg)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 港口API请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试船舶列表API
try {
    $shipsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/ships/all" -Method GET -Headers $headers
    if ($shipsResponse.code -eq 200) {
        $shipCount = $shipsResponse.data.Count
        Write-Host "✓ 船舶API调用成功，共 $shipCount 艘船舶" -ForegroundColor Green
    } else {
        Write-Host "✗ 船舶API调用失败: $($shipsResponse.msg)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 船舶API请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 测试无token的API调用（应该失败）
Write-Host "4. 测试无token的API调用（应该失败）..." -ForegroundColor Yellow
try {
    $noTokenResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/ports/all" -Method GET
    Write-Host "✗ 无token调用竟然成功了，权限控制可能有问题" -ForegroundColor Red
} catch {
    Write-Host "✓ 无token调用正确被拒绝" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Green
Write-Host ""
Write-Host "修复说明:" -ForegroundColor Cyan
Write-Host "1. 港口地图页面已修改为使用 getAllPorts() API函数" -ForegroundColor White
Write-Host "2. 船舶追踪页面已修改为使用 getAllShips() API函数" -ForegroundColor White
Write-Host "3. 这些API函数使用配置好的request实例，会自动添加JWT token" -ForegroundColor White
Write-Host "4. 前端页面现在应该能正常获取数据，不再出现token错误" -ForegroundColor White
Write-Host ""
Write-Host "如果测试通过，请在浏览器中访问 http://localhost:3000 测试港口地图和船舶追踪功能" -ForegroundColor Yellow 