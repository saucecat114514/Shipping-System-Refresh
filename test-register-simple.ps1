# 简化的注册测试脚本
$baseUrl = "http://localhost:8080/api"

Write-Host "测试注册API..." -ForegroundColor Yellow

# 测试数据
$registerData = @{
    username = "newuser"
    password = "password123"
    confirmPassword = "password123"
    email = "newuser@test.com"
    realName = "新用户"
}

$jsonData = $registerData | ConvertTo-Json -Depth 10
Write-Host "请求数据: $jsonData" -ForegroundColor Cyan

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $jsonData -ContentType "application/json; charset=utf-8"
    Write-Host "注册成功!" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "注册失败!" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $reader.BaseStream.Position = 0
            $errorResponse = $reader.ReadToEnd()
            Write-Host "服务器响应: $errorResponse" -ForegroundColor Red
            $reader.Close()
        } catch {
            Write-Host "无法读取错误响应" -ForegroundColor Red
        }
    }
} 