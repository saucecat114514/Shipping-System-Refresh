@echo off
echo 启动航运管理系统开发环境...
echo.

REM 检查Java和Node环境
echo 检查开发环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java环境，请先安装Java 17或以上版本
    pause
    exit /b 1
)

node --version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Node.js环境，请先安装Node.js
    pause
    exit /b 1
)

echo 环境检查通过！
echo.

REM 启动后端
echo 启动后端服务...
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
cd ..

REM 等待后端启动
echo 等待后端服务启动...
timeout /t 10 /nobreak

REM 启动前端
echo 启动前端服务...
cd web
start "前端服务" cmd /k "npm run dev"
cd ..

echo.
echo 服务启动完成！
echo 后端地址: http://localhost:8080/api
echo 前端地址: http://localhost:5173
echo API文档: http://localhost:8080/api/swagger-ui.html
echo 连接测试: http://localhost:5173/test-connection
echo.
echo 按任意键退出...
pause 