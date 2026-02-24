@echo off
chcp 65001 >nul
title Limpiador de archivos .class
color 0C

echo ============================================
echo    LIMPIANDO ARCHIVOS .CLASS
echo ============================================
echo.

set BASE_PATH=D:\.github\PSP\010-LA-CHISPA-ADECUADA

echo Buscando y eliminando archivos .class...
echo.

del /s /q "%BASE_PATH%\*.class" 2>nul

echo.
echo ============================================
echo    LIMPIEZA COMPLETADA
echo ============================================
echo.
pause

