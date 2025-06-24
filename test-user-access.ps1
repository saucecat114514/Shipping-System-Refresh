# 测试不同角色用户的访问权限
$baseUrl = "http://localhost:8080/api"

Write-Host "=== 用户权限测试 ===" -ForegroundColor Green

# 1. 测试管理员登录
Write-Host "`n1. 测试管理员登录..." -ForegroundColor Yellow
try {
    $adminLogin = @{
        username = "admin"
        password = "admin123"
    } | ConvertTo-Json
    
    $adminResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $adminLogin -ContentType "application/json"
    Write-Host "✅ 管理员登录成功!" -ForegroundColor Green
    Write-Host "角色: $($adminResponse.data.role)" -ForegroundColor Cyan
    $adminToken = $adminResponse.data.token
} catch {
    Write-Host "❌ 管理员登录失败!" -ForegroundColor Red
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 测试新注册的CUSTOMER用户登录
Write-Host "`n2. 测试客户登录..." -ForegroundColor Yellow
try {
    $customerLogin = @{
        username = "newuser"
        password = "password123"
    } | ConvertTo-Json
    
    $customerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $customerLogin -ContentType "application/json"
    Write-Host "✅ 客户登录成功!" -ForegroundColor Green
    Write-Host "角色: $($customerResponse.data.role)" -ForegroundColor Cyan
    $customerToken = $customerResponse.data.token
} catch {
    Write-Host "❌ 客户登录失败，可能是用户不存在" -ForegroundColor Yellow
    Write-Host "尝试创建新的测试用户..." -ForegroundColor Yellow
    
    try {
        $registerData = @{
            username = "testcustomer"
            password = "password123"
            confirmPassword = "password123"
            email = "customer@test.com"
            realName = "测试客户"
        } | ConvertTo-Json
        
        $registerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerData -ContentType "application/json"
        Write-Host "✅ 新客户注册成功!" -ForegroundColor Green
        Write-Host "角色: $($registerResponse.data.role)" -ForegroundColor Cyan
        $customerToken = $registerResponse.data.token
    } catch {
        Write-Host "❌ 客户注册失败!" -ForegroundColor Red
        Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
        $customerToken = $null
    }
}

# 3. 测试用户访问权限
Write-Host "`n3. 测试用户访问权限..." -ForegroundColor Yellow

function Test-UserAccess {
    param(
        [string]$token,
        [string]$roleName,
        [string]$endpoint
    )
    
    if (-not $token) {
        Write-Host "❌ $roleName - 没有有效token" -ForegroundColor Red
        return
    }
    
    try {
        $headers = @{ "Authorization" = "Bearer $token" }
        $response = Invoke-RestMethod -Uri "$baseUrl$endpoint" -Method Get -Headers $headers
        Write-Host "✅ $roleName - 可以访问 $endpoint" -ForegroundColor Green
    } catch {
        $statusCode = $_.Exception.Response.StatusCode
        if ($statusCode -eq 403) {
            Write-Host "❌ $roleName - 权限不足访问 $endpoint" -ForegroundColor Red
        } elseif ($statusCode -eq 401) {
            Write-Host "❌ $roleName - 未授权访问 $endpoint" -ForegroundColor Red
        } else {
            Write-Host "⚠️ $roleName - 访问 $endpoint 出错: $statusCode" -ForegroundColor Yellow
        }
    }
}

# 测试各种端点
$testEndpoints = @(
    "/auth/current",
    "/users",
    "/ports",
    "/ships",
    "/routes",
    "/voyages",
    "/orders"
)

Write-Host "`n--- 管理员权限测试 ---" -ForegroundColor Cyan
foreach ($endpoint in $testEndpoints) {
    Test-UserAccess -token $adminToken -roleName "管理员" -endpoint $endpoint
}

if ($customerToken) {
    Write-Host "`n--- 客户权限测试 ---" -ForegroundColor Cyan
    foreach ($endpoint in $testEndpoints) {
        Test-UserAccess -token $customerToken -roleName "客户" -endpoint $endpoint
    }
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "如果看到权限不足的错误，说明后端权限控制正常工作" -ForegroundColor Cyan
Write-Host "如果所有访问都成功，说明权限系统可能需要调整" -ForegroundColor Cyan 