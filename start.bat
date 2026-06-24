@echo off
REM 一键启动脚本 (Windows)
REM 1. 启动 Redis
REM 2. 启动 Spring Boot
REM 3. 启动 Vue 前端(开发模式)

echo === 启动 Redis ===
start "Redis" "C:\Program Files\Redis\redis-server.exe" || echo 请先安装 Redis

echo === 启动 Spring Boot ===
cd /d "%~dp0platform-spring-boot"
start "SpringBoot" cmd /k "mvnw.cmd spring-boot:run"

echo === 启动 Vue 前端 ===
cd /d "%~dp0frontend"
start "Vue" cmd /k "npm run serve"

echo === 全部服务已启动 ===
echo 后端: http://localhost:8090
echo 前端: http://localhost:8080
pause
