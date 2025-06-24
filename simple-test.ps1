# 登录获取token
$loginBody = '{"username":"admin","password":"admin123"}'
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$token = $loginResponse.data.token
Write-Host "Token获取成功: $token"

# 创建船舶
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$shipData = '{"name":"PowerShell测试船舶","typeCn":"集装箱船","typeEn":"Container Ship","flag":"中国","mmsi":"413991234","imoNumber":"IMO9991234","grossTonnage":12000,"deadweightTonnage":18000,"status":0}'
Write-Host "发送数据: $shipData"

try {
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/ships" -Method POST -Body $shipData -Headers $headers
    Write-Host "创建成功: " -NoNewline
    Write-Host ($createResponse | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "创建失败: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        Write-Host "状态码: $statusCode"
    }
} 