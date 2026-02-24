@echo off
chcp 65001 >nul
title LA CHISPA ADECUADA - Iniciador
color 0A

echo ============================================
echo    LA CHISPA ADECUADA - SIMULACION
echo ============================================
echo.

REM Definimos la ruta base del proyecto
set BASE_PATH=D:\.github\PSP\010-LA-CHISPA-ADECUADA
set OUT_PATH=%BASE_PATH%\out\production

echo Iniciando servidores y clientes...
echo (Usando clases compiladas por IntelliJ)
echo.

REM ===== SERVIDORES (Se inician primero) =====

echo [SERVIDOR] Iniciando Alacena (Puerto 5005)...
start "ALACENA - Puerto 5005" cmd /k "cd /d %OUT_PATH%\ServidorAlacenaPociones && java Alacena"
timeout /t 3 >nul

echo [SERVIDOR] Iniciando Taberna (Puerto 5002)...
start "TABERNA - Puerto 5002" cmd /k "cd /d %OUT_PATH%\ServidorTaberna && java Taberna"
timeout /t 3 >nul

echo [SERVIDOR] Iniciando Mercado (Puerto 5003)...
start "MERCADO - Puerto 5003" cmd /k "cd /d %OUT_PATH%\ServidorMercado && java Mercado"
timeout /t 3 >nul

echo [SERVIDOR] Iniciando Porton Norte (Puerto 5004)...
start "PORTON NORTE - Puerto 5004" cmd /k "cd /d %OUT_PATH%\ServidorPortonNorte && java Porton"
timeout /t 3 >nul

REM ===== CLIENTES-SERVIDORES (Protagonistas) =====

echo [PROTAGONISTA] Iniciando Elisabetha (Puerto 5000)...
start "ELISABETHA - Puerto 5000" cmd /k "cd /d %OUT_PATH%\Cliente-Servidor-Elisabetha && java Elisabetha"
timeout /t 3 >nul

echo [PROTAGONISTA] Iniciando Lance (Puerto 5001)...
start "LANCE - Puerto 5001" cmd /k "cd /d %OUT_PATH%\Cliente-Servidor-Lance && java Lance"
timeout /t 4 >nul

REM ===== CLIENTES (Personajes secundarios con datos por defecto) =====

echo [CLIENTE] Iniciando Alquimistas (2 alquimistas)...
start "ALQUIMISTAS" cmd /k "cd /d %OUT_PATH%\Cliente-Alquimista && (echo localhost&echo 5005&echo 5000&echo 5001&echo 2&echo Merlinus&echo Gandolfo) | java Alquimista"
timeout /t 3 >nul

echo [CLIENTE] Iniciando Caballeros (2 caballeros)...
start "CABALLEROS" cmd /k "cd /d %OUT_PATH%\Cliente-CaballeroDelPorton && (echo localhost&echo 5001&echo 2&echo Sir Galahad&echo Sir Percival) | java Caballero"
timeout /t 3 >nul

echo [CLIENTE] Iniciando Damas (2 damas)...
start "DAMAS DEL LAZO" cmd /k "cd /d %OUT_PATH%\Cliente-DamaDelLazo && (echo localhost&echo 5000&echo 2&echo Lady Morgana&echo Lady Guinevere) | java Dama"

echo.
echo ============================================
echo    SIMULACION INICIADA CORRECTAMENTE
echo ============================================
echo.
echo Se han abierto las siguientes ventanas:
echo   - ALACENA (Puerto 5005)
echo   - TABERNA (Puerto 5002)
echo   - MERCADO (Puerto 5003)
echo   - PORTON NORTE (Puerto 5004)
echo   - ELISABETHA (Puerto 5000)
echo   - LANCE (Puerto 5001)
echo   - ALQUIMISTAS (2): Merlinus, Gandolfo
echo   - CABALLEROS (2): Sir Galahad, Sir Percival
echo   - DAMAS (2): Lady Morgana, Lady Guinevere
echo.
echo Cierra esta ventana cuando quieras detener todo.
echo.
pause

