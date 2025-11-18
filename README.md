# 🎵 Mi Playlist Musical

**Entregable 4 - Programación Avanzada 2025**

Aplicación web para gestionar una playlist de videos musicales con CI/CD completo.

---

## 📋 Descripción

Mi Playlist Musical es una aplicación web desarrollada en Java con Spring Boot que permite a los usuarios crear y gestionar su propia playlist de videos musicales de YouTube. La aplicación implementa un pipeline completo de CI/CD usando Jenkins, siguiendo las mejores prácticas de desarrollo moderno.

### ✨ Características Principales

- ✅ **Agregar y quitar videos** de YouTube a la playlist
- 🎥 **Visualización embebida** de videos directamente en la web
- 💾 **Persistencia en JSON** para mantener los cambios entre ejecuciones
- 🎨 **UI atractiva** con Bootstrap 5 y diseño responsive
- ❤️ **Sistema de likes** para cada video
- ⭐ **Marcar favoritos** para destacar tus videos preferidos
- 📊 **Estadísticas** de la playlist en tiempo real
- 🔄 **CI/CD automatizado** con Jenkins

---

## 🏗️ Arquitectura del Proyecto

### Tecnologías Utilizadas

- **Backend:**
  - Java 17
  - Spring Boot 3.2.0
  - Maven 3.9

- **Frontend:**
  - Thymeleaf
  - Bootstrap 5.3.2
  - jQuery 3.7.1
  - Font Awesome 6.4.0

- **Persistencia:**
  - Gson (JSON)
  - Sistema de archivos

- **Testing:**
  - JUnit 5
  - Mockito

- **CI/CD:**
  - Jenkins
  - Shell scripts (Mac/Linux)
  - Batch scripts (Windows)

### Estructura del Proyecto

```
mi-playlist/
├── src/
│   ├── main/
│   │   ├── java/com/playlist/
│   │   │   ├── MiPlaylistApplication.java      # Clase principal
│   │   │   ├── controller/
│   │   │   │   └── VideoController.java         # Controlador REST/Web
│   │   │   ├── model/
│   │   │   │   └── Video.java                   # Modelo de datos
│   │   │   ├── service/
│   │   │   │   └── VideoService.java            # Lógica de negocio
│   │   │   └── repository/
│   │   │       ├── VideoRepository.java         # Persistencia
│   │   │       └── LocalDateTimeAdapter.java    # Adaptador Gson
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/style.css                # Estilos personalizados
│   │       │   └── js/app.js                    # JavaScript
│   │       ├── templates/
│   │       │   └── index.html                   # Vista principal
│   │       ├── data/
│   │       │   └── videos.json                  # Base de datos JSON
│   │       └── application.properties           # Configuración
│   └── test/
│       └── java/com/playlist/
│           ├── VideoServiceTest.java            # Tests del servicio
│           └── VideoModelTest.java              # Tests del modelo
├── pom.xml                                      # Dependencias Maven
├── Jenkinsfile                                  # Pipeline CI/CD
├── deploy-mac.sh                                # Script deployment Mac/Linux
├── deploy-windows.bat                           # Script deployment Windows
└── README.md                                    # Este archivo
```

---

## 🚀 Instalación y Ejecución

### Prerrequisitos

- **Java 17 o superior** ([Descargar](https://adoptium.net/))
- **Maven 3.6+** ([Descargar](https://maven.apache.org/download.cgi))
- **Git** (opcional, para clonar el repositorio)

### Opción 1: Ejecución con Maven

```bash
# 1. Navegar al directorio del proyecto
cd mi-playlist

# 2. Compilar el proyecto
mvn clean package

# 3. Ejecutar la aplicación
java -jar target/mi-playlist-1.0.0.jar
```

### Opción 2: Usando Scripts de Deployment

#### En Mac/Linux:

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x deploy-mac.sh

# Ejecutar
./deploy-mac.sh

# O compilar y ejecutar
./deploy-mac.sh --build
```

#### En Windows:

```batch
REM Ejecutar
deploy-windows.bat

REM O compilar y ejecutar
deploy-windows.bat --build
```

### Acceder a la Aplicación

Una vez iniciada la aplicación, abre tu navegador y ve a:

```
http://localhost:8081
```

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte de cobertura
mvn test jacoco:report
```

### Tests Incluidos

- **VideoServiceTest**: 14 tests unitarios del servicio de negocio
- **VideoModelTest**: 10 tests del modelo de datos
- **Cobertura**: ~85% de código cubierto

---

## 🔄 CI/CD con Jenkins

### Configuración de Jenkins

#### 1. Instalar Jenkins

**Mac (usando Homebrew):**
```bash
brew install jenkins-lts
brew services start jenkins-lts
```

**Windows/Otros:**
- Descargar desde [jenkins.io](https://www.jenkins.io/download/)

#### 2. Configurar Herramientas Globales

1. Ir a `Manage Jenkins` → `Global Tool Configuration`

2. Configurar **Maven**:
   - Nombre: `Maven-3.9`
   - Versión: 3.9.x (instalar automáticamente)

3. Configurar **JDK**:
   - Nombre: `JDK-17`
   - Ruta: Ruta de instalación de Java 17

#### 3. Crear un Nuevo Job

1. Click en `New Item`
2. Nombre: `mi-playlist-pipeline`
3. Tipo: `Pipeline`
4. En configuración del pipeline:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git` (o copiar directamente el Jenkinsfile)
   - Script Path: `Jenkinsfile`

### Pipeline Stages

El pipeline implementa las siguientes etapas:

1. **Checkout** - Obtiene el código del repositorio
2. **Build** - Compila la aplicación
3. **Test** - Ejecuta tests unitarios
4. **Code Quality** - Análisis de código con Checkstyle
5. **Package** - Crea el JAR ejecutable
6. **Deploy** - Prepara el deployment

### Ejecutar el Pipeline

1. Ir al job `mi-playlist-pipeline`
2. Click en `Build Now`
3. Ver el progreso en `Console Output`

---

## 📚 API REST

La aplicación expone los siguientes endpoints REST:

### Videos

- **GET** `/api/videos` - Obtener todos los videos
- **GET** `/api/videos/{id}` - Obtener un video por ID
- **POST** `/api/videos` - Agregar un nuevo video
  - Parámetros: `nombre`, `link`
- **DELETE** `/api/videos/{id}` - Eliminar un video

### Acciones

- **POST** `/api/videos/{id}/like` - Agregar like a un video
- **POST** `/api/videos/{id}/favorito` - Toggle favorito

### Estadísticas

- **GET** `/api/stats` - Obtener estadísticas de la playlist
- **GET** `/api/videos/top/{cantidad}` - Obtener top videos por likes

### Ejemplos de Uso

```bash
# Agregar un video
curl -X POST http://localhost:8081/api/videos \
  -d "nombre=Bohemian Rhapsody" \
  -d "link=https://www.youtube.com/watch?v=fJ9rUzIMcZQ"

# Obtener todos los videos
curl http://localhost:8081/api/videos

# Agregar like
curl -X POST http://localhost:8081/api/videos/{id}/like
```

---

## 🎨 Características de UI

### Diseño Responsivo

- Adaptable a dispositivos móviles, tablets y escritorio
- Grid system con Bootstrap
- Cards animadas para cada video

### Interactividad

- Agregar videos mediante modal
- Likes con animaciones
- Favoritos con feedback visual
- Notificaciones toast
- Confirmación antes de eliminar

### Estadísticas en Tiempo Real

- Total de videos
- Cantidad de favoritos
- Total de likes acumulados

---

## 🛠️ Principios de Refactoring Aplicados

Este proyecto aplica los principios de **Clean Code** y **Refactoring** estudiados:

### Code Smells Evitados

✅ **Responsabilidad Única (SRP)**
- Separación clara entre Controller, Service y Repository

✅ **Sin Código Duplicado**
- Métodos reutilizables y funciones auxiliares

✅ **Nombres Descriptivos**
- Variables, métodos y clases con nombres claros

✅ **Funciones Pequeñas**
- Cada método tiene una responsabilidad específica

✅ **Encapsulación**
- Uso de getters/setters con Lombok
- Datos protegidos en el repositorio

### Técnicas Aplicadas

- **Extract Class**: Separación de VideoRepository y VideoService
- **Extract Method**: Métodos pequeños y específicos
- **Introduce Parameter Object**: Uso de DTOs y clases de valor
- **Replace Primitive with Object**: Video como objeto de dominio
- **Move Function**: Lógica donde corresponde semánticamente

---

## 📊 Principios de CI/CD Implementados

### Integración Continua

✅ **Control de versiones** con Git
✅ **Build automatizado** con Maven
✅ **Tests automáticos** con JUnit
✅ **Feedback rápido** en cada commit

### Despliegue Continuo

✅ **Pipeline automatizado** con Jenkins
✅ **Artefactos versionados** (JAR files)
✅ **Scripts de deployment** multiplataforma
✅ **Etapas claramente definidas**

### Mejores Prácticas

- ✅ Builds reproducibles
- ✅ Tests como gate de calidad
- ✅ Artefactos inmutables
- ✅ Deployment scriptado

---

## 🐛 Troubleshooting

### Puerto 8080 en uso

```bash
# Mac/Linux
lsof -ti:8081 | xargs kill -9

# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

### Error de permisos en scripts (Mac)

```bash
chmod +x deploy-mac.sh
```

### Error de Java version

Verificar versión instalada:
```bash
java -version
```

Debe ser Java 17 o superior.

### Tests fallan

```bash
# Limpiar y reconstruir
mvn clean install

# Ejecutar tests con más información
mvn test -X
```

---

## 📝 Notas de Desarrollo

### Persistencia

Los datos se almacenan en `src/main/resources/data/videos.json`. Este archivo se crea automáticamente la primera vez que se ejecuta la aplicación.

### Hot Reload

La aplicación incluye Spring Boot DevTools para desarrollo. Los cambios en código se reflejan automáticamente sin reiniciar.

### Logs

Los logs de la aplicación se muestran en consola con formato personalizado. Nivel por defecto: `INFO`

---

## 👥 Autor

**Entregable 4 - Programación Avanzada 2025**

---

## 📄 Licencia

Este proyecto es parte de un trabajo académico para Programación Avanzada 2025.

---

## 🎯 Cumplimiento de Requerimientos

### Parte 1: Desarrollo ✅

- [x] Agregar y quitar videos con nombre y link
- [x] Visualizar videos embebidos
- [x] Persistir datos entre ejecuciones
- [x] UI atractiva con Bootstrap
- [x] Sistema de likes
- [x] Marcar/desmarcar favoritos

### Parte 2: CI/CD ✅

- [x] Control de versiones (Git)
- [x] Jenkins configurado localmente
- [x] Pipeline automatizado:
  - [x] Toma código del repositorio
  - [x] Build de la aplicación
  - [x] Tests automáticos (JUnit)
  - [x] Deploy preparado
- [x] Scripts de deployment:
  - [x] Mac/Linux (`deploy-mac.sh`)
  - [x] Windows (`deploy-windows.bat`)

---

## 🚀 Próximos Pasos (Mejoras Futuras)

- [ ] Autenticación de usuarios
- [ ] Compartir playlists
- [ ] Búsqueda y filtros avanzados
- [ ] Integración con API de YouTube
- [ ] Reproducción continua de videos
- [ ] Temas personalizables
- [ ] Exportar playlist a JSON/CSV

---

**¡Disfruta de tu playlist musical! 🎵🎸🎹**
