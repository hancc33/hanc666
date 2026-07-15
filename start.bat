@echo off
chcp 65001 >nul
echo ========================================
echo   助苗·助梦·护苗 - 暑期社会实践展示平台
echo ========================================
echo.

echo [1/2] 启动后端 (Spring Boot)...
cd /d "%~dp0backend"
start "Backend" cmd /k "mvn spring-boot:run"

echo [2/2] 启动前端 (Vue3)...
cd /d "%~dp0frontend"
start "Frontend" cmd /k "npm run dev"

echo.
echo ========================================
echo   系统启动中，请稍候...
echo ========================================
echo   后端: http://localhost:8080
echo   前端: http://localhost:5173
echo   管理后台: http://localhost:5173/admin/login
echo   管理员账号: admin / admin123
echo ========================================
echo.
echo 按任意键关闭此窗口...
pause >nul
