@echo off
REM Script para ejecutar AgroSense con JavaFX

echo ========================================
echo    AgroSense - Sistema de Monitoreo
echo ========================================
echo.

REM Verificar si existe Maven
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven encontrado
    echo Ejecutando con Maven...
    mvn javafx:run
) else (
    echo [ERROR] Maven no esta instalado
    echo.
    echo Por favor instala Maven o usa IntelliJ IDEA:
    echo 1. Descargar Maven: https://maven.apache.org/download.cgi
    echo 2. O usar IntelliJ IDEA Community (mas facil)
    echo.
    pause
)
