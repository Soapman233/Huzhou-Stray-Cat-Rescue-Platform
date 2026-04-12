@echo off
REM 后端启动脚本，附带环境变量配置

REM 设置微信小程序信息
set WECHAT_APPID=wxa34bb71dc143f47e

REM 设置JWT密钥 (必须足够长且复杂，已自动生成)
set JWT_SECRET=HzscaWeb_Graduation_Project_JwtSecret_2024_0123456789_abcdef

echo 环境变量配置完成：
echo WECHAT_APPID = %WECHAT_APPID%
echo JWT_SECRET = %JWT_SECRET%
echo.

if "%WECHAT_SECRET%"=="" (
  echo WECHAT_SECRET 未设置。请先在 Windows 用户环境变量中设置 WECHAT_SECRET 后再启动。
  pause
  exit /b 1
)

echo 正在启动 Spring Boot 后端...
cd hzsca-springboot\hzsca
call mvnw.cmd spring-boot:run

pause
