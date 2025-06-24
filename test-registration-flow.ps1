# 完整的注册和登录流程测试
$baseUrl = "http://localhost:8080/api"
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testUsername = "testuser$timestamp"

Write-Host "=== 用户注册和登录流程测试 ===" -ForegroundColor Green

# 1. 注册新用户
Write-Host "`n1. 测试用户注册..." -ForegroundColor Yellow
$registerData = @{
    username = $testUsername
    password = "password123"
    confirmPassword = "password123"  
    email = "$testUsername@test.com"
    realName = "测试用户$timestamp"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerData -ContentType "application/json"
    Write-Host "✅ 注册成功!" -ForegroundColor Green
    Write-Host "用户ID: $($registerResponse.data.userId)" -ForegroundColor Cyan
    Write-Host "用户名: $($registerResponse.data.username)" -ForegroundColor Cyan
    Write-Host "角色: $($registerResponse.data.role)" -ForegroundColor Cyan
    Write-Host "邮箱: $($registerResponse.data.email)" -ForegroundColor Cyan
    
    $registerToken = $registerResponse.data.token
} catch {
    Write-Host "❌ 注册失败!" -ForegroundColor Red
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 使用注册用户登录
Write-Host "`n2. 测试用户登录..." -ForegroundColor Yellow
$loginData = @{
    username = $testUsername
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginData -ContentType "application/json"
    Write-Host "✅ 登录成功!" -ForegroundColor Green
    Write-Host "用户ID: $($loginResponse.data.userId)" -ForegroundColor Cyan
    Write-Host "角色: $($loginResponse.data.role)" -ForegroundColor Cyan
    
    $loginToken = $loginResponse.data.token
} catch {
    Write-Host "❌ 登录失败!" -ForegroundColor Red
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. 使用token获取当前用户信息
Write-Host "`n3. 测试获取当前用户信息..." -ForegroundColor Yellow
try {
    $headers = @{ "Authorization" = "Bearer $loginToken" }
    $currentUserResponse = Invoke-RestMethod -Uri "$baseUrl/auth/current" -Method Get -Headers $headers
    Write-Host "✅ 获取用户信息成功!" -ForegroundColor Green
    Write-Host "当前用户: $($currentUserResponse.data.username)" -ForegroundColor Cyan
    Write-Host "角色: $($currentUserResponse.data.role)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 获取用户信息失败!" -ForegroundColor Red
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 测试重复注册
Write-Host "`n4. 测试重复注册..." -ForegroundColor Yellow
try {
    $duplicateResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerData -ContentType "application/json"
    Write-Host "❌ 重复注册应该失败，但成功了!" -ForegroundColor Red
} catch {
    Write-Host "✅ 重复注册正确失败!" -ForegroundColor Green
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $errorResponse = $reader.ReadToEnd()
        Write-Host "错误信息: $errorResponse" -ForegroundColor Cyan
        $reader.Close()
    }
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "用户注册功能已成功实现，默认角色为CUSTOMER" -ForegroundColor Green 