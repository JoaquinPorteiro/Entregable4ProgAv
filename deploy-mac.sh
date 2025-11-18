#!/bin/bash

###############################################################################
# Script de Deployment para Mi Playlist Musical - Mac/Linux
# Ejecuta la aplicación Spring Boot en un entorno local
###############################################################################

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuración
APP_NAME="mi-playlist"
APP_VERSION="1.0.0"
JAR_NAME="${APP_NAME}-${APP_VERSION}.jar"
JAR_PATH="target/${JAR_NAME}"
PORT=8081

# Banner
echo -e "${BLUE}"
echo "========================================================"
echo "   Mi Playlist Musical - Deployment Script (Mac/Linux)"
echo "========================================================"
echo -e "${NC}"

# Función para verificar si Java está instalado
check_java() {
    echo -e "${YELLOW}→ Verificando instalación de Java...${NC}"

    if ! command -v java &> /dev/null; then
        echo -e "${RED}✗ Error: Java no está instalado${NC}"
        echo "Por favor instala Java 17 o superior"
        echo "Descarga desde: https://adoptium.net/"
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)

    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo -e "${RED}✗ Error: Se requiere Java 17 o superior${NC}"
        echo "Versión actual: $JAVA_VERSION"
        exit 1
    fi

    echo -e "${GREEN}✓ Java ${JAVA_VERSION} detectado${NC}"
}

# Función para verificar si Maven está instalado
check_maven() {
    echo -e "${YELLOW}→ Verificando instalación de Maven...${NC}"

    if ! command -v mvn &> /dev/null; then
        echo -e "${YELLOW}⚠ Maven no está instalado (requerido solo para build)${NC}"
        return 1
    fi

    echo -e "${GREEN}✓ Maven detectado${NC}"
    return 0
}

# Función para verificar si el puerto está disponible
check_port() {
    echo -e "${YELLOW}→ Verificando disponibilidad del puerto ${PORT}...${NC}"

    if lsof -Pi :${PORT} -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo -e "${RED}✗ Error: El puerto ${PORT} ya está en uso${NC}"
        echo "Procesos usando el puerto ${PORT}:"
        lsof -i :${PORT}
        echo ""
        read -p "¿Deseas terminar el proceso y continuar? (s/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Ss]$ ]]; then
            echo "Terminando proceso..."
            kill -9 $(lsof -t -i:${PORT}) 2>/dev/null
            echo -e "${GREEN}✓ Proceso terminado${NC}"
        else
            exit 1
        fi
    else
        echo -e "${GREEN}✓ Puerto ${PORT} disponible${NC}"
    fi
}

# Función para hacer build de la aplicación
build_app() {
    echo -e "${YELLOW}→ Compilando aplicación...${NC}"

    if ! check_maven; then
        echo -e "${RED}✗ Maven no está disponible${NC}"
        echo "Si ya tienes el JAR compilado, omite este paso"
        return 1
    fi

    echo "Ejecutando: mvn clean package -DskipTests"

    if mvn clean package -DskipTests; then
        echo -e "${GREEN}✓ Build completado exitosamente${NC}"
        return 0
    else
        echo -e "${RED}✗ Error en el build${NC}"
        return 1
    fi
}

# Función para verificar que el JAR existe
check_jar() {
    echo -e "${YELLOW}→ Verificando artefacto JAR...${NC}"

    if [ ! -f "$JAR_PATH" ]; then
        echo -e "${RED}✗ Error: No se encontró ${JAR_PATH}${NC}"
        echo ""
        read -p "¿Deseas compilar la aplicación ahora? (s/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Ss]$ ]]; then
            if build_app; then
                if [ ! -f "$JAR_PATH" ]; then
                    echo -e "${RED}✗ Error: El JAR no se generó correctamente${NC}"
                    exit 1
                fi
            else
                exit 1
            fi
        else
            exit 1
        fi
    fi

    echo -e "${GREEN}✓ JAR encontrado: ${JAR_PATH}${NC}"

    # Mostrar información del JAR
    JAR_SIZE=$(ls -lh "$JAR_PATH" | awk '{print $5}')
    echo "  Tamaño: ${JAR_SIZE}"
}

# Función para crear el directorio de datos si no existe
prepare_data_dir() {
    echo -e "${YELLOW}→ Preparando directorios...${NC}"

    DATA_DIR="src/main/resources/data"
    if [ ! -d "$DATA_DIR" ]; then
        mkdir -p "$DATA_DIR"
        echo -e "${GREEN}✓ Directorio de datos creado${NC}"
    else
        echo -e "${GREEN}✓ Directorio de datos existe${NC}"
    fi
}

# Función para ejecutar la aplicación
run_app() {
    echo ""
    echo -e "${BLUE}========================================================"
    echo "   Iniciando Mi Playlist Musical"
    echo "========================================================${NC}"
    echo ""
    echo "La aplicación estará disponible en:"
    echo -e "${GREEN}http://localhost:${PORT}${NC}"
    echo ""
    echo "Presiona Ctrl+C para detener la aplicación"
    echo ""
    echo -e "${YELLOW}Iniciando...${NC}"
    echo ""

    # Ejecutar el JAR
    java -jar "$JAR_PATH" --server.port=${PORT}
}

# Función de limpieza al salir
cleanup() {
    echo ""
    echo -e "${YELLOW}Deteniendo aplicación...${NC}"
    echo -e "${GREEN}✓ Aplicación detenida${NC}"
    exit 0
}

# Registrar trap para limpieza
trap cleanup SIGINT SIGTERM

# Función principal
main() {
    # Verificaciones
    check_java
    check_port
    check_jar
    prepare_data_dir

    # Ejecutar aplicación
    run_app
}

# Mostrar menú de opciones
if [ "$1" == "--build" ]; then
    echo "Modo: Build y Deploy"
    check_java
    build_app
    check_jar
    prepare_data_dir
    run_app
elif [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
    echo "Uso: ./deploy-mac.sh [opción]"
    echo ""
    echo "Opciones:"
    echo "  (sin opciones)  Ejecutar la aplicación"
    echo "  --build         Compilar y ejecutar"
    echo "  --help, -h      Mostrar esta ayuda"
    echo ""
    exit 0
else
    # Ejecución normal
    main
fi
