@echo off
echo ========================================
echo 旅游系统数据库初始化脚本
echo ========================================
echo.

echo 请输入MySQL root用户密码:
set /p MYSQL_PASSWORD=
echo.
echo 正在创建数据库和表...
echo.

mysql -u root -p%MYSQL_PASSWORD% < "%~dp0..\sql\db_minecraft.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 数据库初始化成功！
    echo ========================================
) else (
    echo.
    echo ========================================
    echo 数据库初始化失败！
    echo 请检查：
    echo 1. MySQL服务是否已启动
    echo 2. root用户密码是否正确
    echo 3. MySQL是否已安装
    echo ========================================
)

echo.
pause