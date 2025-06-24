@echo off
echo Testing order creation with customerId provided...

echo 1. Login as customer...
curl -X POST "http://localhost:8080/api/auth/login" ^
     -H "Content-Type: application/json" ^
     -d "{\"username\":\"iambb\",\"password\":\"123456\"}" > login_response.json

for /f "tokens=*" %%i in ('powershell -command "& {(Get-Content login_response.json | ConvertFrom-Json).data.token}"') do set TOKEN=%%i

echo Token: %TOKEN%

echo.
echo 2. Getting current user info...
curl -X GET "http://localhost:8080/api/auth/current-user" ^
     -H "Authorization: Bearer %TOKEN%" ^
     -H "Content-Type: application/json"

echo.
echo.
echo 3. Creating order WITH customerId...
curl -X POST "http://localhost:8080/api/orders" ^
     -H "Authorization: Bearer %TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"orderNumber\":\"ORD20250624WITHID\",\"customerId\":281,\"cargoName\":\"测试货物\",\"cargoType\":\"普通货物\",\"cargoWeight\":25.5,\"cargoVolume\":50.0,\"isUrgent\":false,\"notes\":\"测试订单创建功能\"}"

echo.
echo.
echo Done! 