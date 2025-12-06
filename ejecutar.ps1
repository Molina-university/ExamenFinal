# Script PowerShell para ejecutar AgroSense

Write-Host "========================================" -ForegroundColor Green
Write-Host "   AgroSense - Sistema de Monitoreo" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Verificar Java
Write-Host "Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "[OK] Java instalado: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Java no encontrado" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Verificar Maven
Write-Host "Verificando Maven..." -ForegroundColor Yellow
$mavenPath = Get-Command mvn -ErrorAction SilentlyContinue

if ($mavenPath) {
    Write-Host "[OK] Maven encontrado" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ejecutando AgroSense..." -ForegroundColor Cyan
    Write-Host ""
    mvn javafx:run
} else {
    Write-Host "[ERROR] Maven no esta instalado" -ForegroundColor Red
    Write-Host ""
    Write-Host "OPCIONES PARA EJECUTAR:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "1. INSTALAR MAVEN (Opcion Rapida):" -ForegroundColor Cyan
    Write-Host "   - Descargar: https://maven.apache.org/download.cgi"
    Write-Host "   - Extraer a: C:\apache-maven"
    Write-Host "   - Ejecutar: " -NoNewline
    Write-Host '$env:Path += ";C:\apache-maven\bin"' -ForegroundColor White
    Write-Host "   - Luego ejecutar este script nuevamente"
    Write-Host ""
    Write-Host "2. USAR INTELLIJ IDEA (Opcion Mas Facil):" -ForegroundColor Cyan
    Write-Host "   - Descargar: https://www.jetbrains.com/idea/download/"
    Write-Host "   - Abrir proyecto en IntelliJ"
    Write-Host "   - Click derecho en AgroSenseFX.java -> Run"
    Write-Host ""
    
    Read-Host "Presiona Enter para salir"
}
