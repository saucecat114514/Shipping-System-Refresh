# 测试用户注册API
$baseUrl = "http://localhost:8080/api"

# 测试数据
$registerData = @{
    username = "testuser"
    password = "password123"
    confirmPassword = "password123"
    email = "test@example.com"
    realName = "测试用户"
} | ConvertTo-Json -Depth 10

Write-Host "测试用户注册API..." -ForegroundColor Yellow
Write-Host "请求数据: $registerData" -ForegroundColor Cyan

try {
    # 发送注册请求
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerData -ContentType "application/json; charset=utf-8"
    
    Write-Host "注册成功!" -ForegroundColor Green
    Write-Host "响应数据:" -ForegroundColor Cyan
    $response | ConvertTo-Json -Depth 10
    
    # 测试使用注册返回的token获取当前用户信息
    if ($response.data.token) {
        Write-Host "`n测试获取当前用户信息..." -ForegroundColor Yellow
        $headers = @{
            "Authorization" = "Bearer " + $response.data.token
        }
        
        $currentUserResponse = Invoke-RestMethod -Uri "$baseUrl/auth/current" -Method Get -Headers $headers
        Write-Host "当前用户信息:" -ForegroundColor Cyan
        $currentUserResponse | ConvertTo-Json -Depth 10
    }
    
} catch {
    Write-Host "注册失败!" -ForegroundColor Red
    Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $errorResponse = $reader.ReadToEnd()
        Write-Host "服务器响应: $errorResponse" -ForegroundColor Red
    }
}

Write-Host "`n测试完成!" -ForegroundColor Green 