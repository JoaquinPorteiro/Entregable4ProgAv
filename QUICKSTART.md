# 🚀 Guía Rápida de Inicio - Mi Playlist Musical

Esta guía te permitirá tener la aplicación funcionando en menos de 5 minutos.

---

## ⚡ Inicio Rápido (Opción más simple)

### Paso 1: Verificar prerrequisitos

```bash
# Verificar Java (debe ser versión 17 o superior)
java -version

# Si no tienes Java 17, descarga desde:
# https://adoptium.net/
```

### Paso 2: Ejecutar la aplicación

**En Mac/Linux:**
```bash
cd mi-playlist
./deploy-mac.sh
```

**En Windows:**
```batch
cd mi-playlist
deploy-windows.bat
```

### Paso 3: Abrir en el navegador

```
http://localhost:8080
```

**¡Listo! La aplicación ya está funcionando. 🎉**

---

## 🔨 Si quieres compilar desde cero

### Paso 1: Instalar Maven

**Mac (con Homebrew):**
```bash
brew install maven
```

**Windows:**
Descargar desde: https://maven.apache.org/download.cgi

### Paso 2: Compilar

```bash
cd mi-playlist
mvn clean package
```

### Paso 3: Ejecutar

```bash
java -jar target/mi-playlist-1.0.0.jar
```

---

## 🧪 Ejecutar Tests

```bash
cd mi-playlist
mvn test
```

---

## 🔄 Configurar Jenkins (CI/CD)

Ver guía completa en: [JENKINS-SETUP.md](JENKINS-SETUP.md)

**Resumen:**

1. Instalar Jenkins:
   ```bash
   # Mac
   brew install jenkins-lts
   brew services start jenkins-lts
   ```

2. Abrir `http://localhost:8080`

3. Configurar herramientas (JDK-17, Maven-3.9)

4. Crear nuevo job tipo "Pipeline"

5. Configurar para usar el `Jenkinsfile`

6. Click en "Build Now"

---

## 📖 Más Información

- **Documentación completa**: [README.md](README.md)
- **Configuración Jenkins**: [JENKINS-SETUP.md](JENKINS-SETUP.md)
- **API REST**: Ver sección API en README.md

---

## 🆘 Problemas Comunes

### Puerto 8080 en uso

```bash
# Mac/Linux - matar proceso en puerto 8080
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Java no encontrado

Descargar e instalar Java 17 desde: https://adoptium.net/

### Permisos en Mac

```bash
chmod +x deploy-mac.sh
```

---

## ✨ Primeros Pasos en la Aplicación

1. **Agregar tu primer video:**
   - Click en "Agregar Nuevo Video"
   - Nombre: "Bohemian Rhapsody - Queen"
   - Link: `https://www.youtube.com/watch?v=fJ9rUzIMcZQ`
   - Click en "Guardar Video"

2. **Dar likes:** Click en el botón ❤️

3. **Marcar favoritos:** Click en el botón ⭐

4. **Ver favoritos:** Click en "Favoritos" en el menú

5. **Eliminar video:** Click en el botón 🗑️

---

**¡Disfruta de tu playlist musical! 🎵**
