@echo off
REM ###############################################################################
REM Script de Deployment para Mi Playlist Musical - Windows
REM Ejecuta la aplicación Spring Boot en un entorno local
REM ###############################################################################

setlocal enabledelayedexpansion

REM Configuración
set APP_NAME=mi-playlist
set APP_VERSION=1.0.0
set JAR_NAME=%APP_NAME%-%APP_VERSION%.jar
set JAR_PATH=target\%JAR_NAME%
set PORT=8081

REM Banner
echo.
echo ========================================================
echo    Mi Playlist Musical - Deployment Script (Windows)
echo ========================================================
echo.

REM Verificar si Java está instalado
:check_java
echo [+] Verificando instalacion de Java...

where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Error: Java no esta instalado
    echo Por favor instala Java 17 o superior
    echo Descarga desde: https://adoptium.net/
    pause
    exit /b 1
)

REM Obtener versión de Java
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION_STRING=%%i
)
set JAVA_VERSION_STRING=%JAVA_VERSION_STRING:"=%
for /f "delims=. tokens=1" %%v in ("%JAVA_VERSION_STRING%") do (
    set JAVA_VERSION=%%v
)

if %JAVA_VERSION% lss 17 (
    echo [X] Error: Se requiere Java 17 o superior
    echo Version actual: %JAVA_VERSION%
    pause
    exit /b 1
)

echo [OK] Java %JAVA_VERSION% detectado
echo.

REM Verificar si Maven está instalado
:check_maven
echo [+] Verificando instalacion de Maven...

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] Maven no esta instalado (requerido solo para build)
    set MAVEN_AVAILABLE=0
) else (
    echo [OK] Maven detectado
    set MAVEN_AVAILABLE=1
)
echo.

REM Verificar si el puerto está disponible
:check_port
echo [+] Verificando disponibilidad del puerto %PORT%...

netstat -ano | findstr ":%PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [X] Error: El puerto %PORT% ya esta en uso
    echo.
    netstat -ano | findstr ":%PORT% "
    echo.
    set /p KILL_PROCESS="Deseas terminar el proceso y continuar? (S/N): "
    if /i "!KILL_PROCESS!"=="S" (
        for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT% " ^| findstr "LISTENING"') do (
            echo Terminando proceso con PID %%a...
            taskkill /F /PID %%a >nul 2>&1
        )
        echo [OK] Proceso terminado
    ) else (
        exit /b 1
    )
) else (
    echo [OK] Puerto %PORT% disponible
)
echo.

REM Función para hacer build de la aplicación
:build_app
echo [+] Compilando aplicacion...

if %MAVEN_AVAILABLE% equ 0 (
    echo [X] Maven no esta disponible
    echo Si ya tienes el JAR compilado, omite este paso
    goto :check_jar
)

echo Ejecutando: mvn clean package -DskipTests
echo.

call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo [X] Error en el build
    pause
    exit /b 1
)

echo [OK] Build completado exitosamente
echo.
goto :eof

REM Verificar que el JAR existe
:check_jar
echo [+] Verificando artefacto JAR...

if not exist "%JAR_PATH%" (
    echo [X] Error: No se encontro %JAR_PATH%
    echo.
    set /p BUILD_NOW="Deseas compilar la aplicacion ahora? (S/N): "
    if /i "!BUILD_NOW!"=="S" (
        call :build_app
        if not exist "%JAR_PATH%" (
            echo [X] Error: El JAR no se genero correctamente
            pause
            exit /b 1
        )
    ) else (
        pause
        exit /b 1
    )
)

echo [OK] JAR encontrado: %JAR_PATH%

REM Mostrar información del JAR
for %%A in ("%JAR_PATH%") do set JAR_SIZE=%%~zA
set /a JAR_SIZE_MB=%JAR_SIZE% / 1024 / 1024
echo   Tamanio: %JAR_SIZE_MB% MB
echo.
goto :eof

REM Crear el directorio de datos si no existe
:prepare_data_dir
echo [+] Preparando directorios...

set DATA_DIR=src\main\resources\data
if not exist "%DATA_DIR%" (
    mkdir "%DATA_DIR%"
    echo [OK] Directorio de datos creado
) else (
    echo [OK] Directorio de datos existe
)
echo.
goto :eof

REM Ejecutar la aplicación
:run_app
echo.
echo ========================================================
echo    Iniciando Mi Playlist Musical
echo ========================================================
echo.
echo La aplicacion estara disponible en:
echo http://localhost:%PORT%
echo.
echo Presiona Ctrl+C para detener la aplicacion
echo.
echo Iniciando...
echo.

REM Ejecutar el JAR
java -jar "%JAR_PATH%" --server.port=%PORT%

echo.
echo [+] Aplicacion detenida
pause
exit /b 0

REM Función principal
:main

REM Verificar argumentos
if "%1"=="--build" (
    echo Modo: Build y Deploy
    echo.
    call :check_java
    call :build_app
    call :check_jar
    call :prepare_data_dir
    call :run_app
    exit /b 0
)

if "%1"=="--help" (
    echo Uso: deploy-windows.bat [opcion]
    echo.
    echo Opciones:
    echo   (sin opciones)  Ejecutar la aplicacion
    echo   --build         Compilar y ejecutar
    echo   --help          Mostrar esta ayuda
    echo.
    pause
    exit /b 0
)

REM Ejecución normal
call :check_java
call :check_port
call :check_jar
call :prepare_data_dir
call :run_app

:end
