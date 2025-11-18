# 🔧 Guía de Configuración de Jenkins para Mi Playlist

Esta guía te ayudará a configurar Jenkins localmente para ejecutar el pipeline de CI/CD de Mi Playlist Musical.

---

## 📥 Instalación de Jenkins

### Opción 1: Mac con Homebrew (Recomendado)

```bash
# Instalar Jenkins LTS
brew install jenkins-lts

# Iniciar Jenkins
brew services start jenkins-lts

# Verificar que está corriendo
brew services list
```

Jenkins estará disponible en: `http://localhost:8080`

### Opción 2: Windows

1. Descargar el instalador desde: https://www.jenkins.io/download/
2. Ejecutar el instalador .msi
3. Seguir el asistente de instalación
4. Jenkins se iniciará automáticamente como servicio

### Opción 3: Docker (Multiplataforma)

```bash
# Ejecutar Jenkins en Docker
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts
```

---

## 🔐 Primer Acceso a Jenkins

### 1. Obtener la Contraseña Inicial

**Mac/Linux:**
```bash
cat /Users/$(whoami)/.jenkins/secrets/initialAdminPassword
```

**Windows:**
```
C:\Program Files\Jenkins\secrets\initialAdminPassword
```

**Docker:**
```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 2. Desbloquear Jenkins

1. Abrir navegador en `http://localhost:8080`
2. Pegar la contraseña inicial
3. Click en "Continue"

### 3. Instalar Plugins

Seleccionar: **"Install suggested plugins"**

Plugins adicionales recomendados:
- Pipeline
- Git
- Maven Integration
- JUnit Plugin
- Blue Ocean (opcional, para mejor visualización)

### 4. Crear Usuario Admin

- Username: `admin`
- Password: (tu contraseña segura)
- Full name: Tu nombre
- Email: tu@email.com

---

## ⚙️ Configuración de Herramientas Globales

### 1. Configurar JDK

1. Ir a: `Manage Jenkins` → `Global Tool Configuration`

2. Scroll hasta **JDK**

3. Click en "Add JDK"

4. Configurar:
   - **Name**: `JDK-17`
   - **Desmarcar** "Install automatically"
   - **JAVA_HOME**: Ruta de tu instalación de Java

**Encontrar JAVA_HOME:**

Mac/Linux:
```bash
/usr/libexec/java_home -v 17
```

Windows:
```
C:\Program Files\Java\jdk-17
```

### 2. Configurar Maven

1. En la misma página, scroll hasta **Maven**

2. Click en "Add Maven"

3. Configurar:
   - **Name**: `Maven-3.9`
   - **Marcar** "Install automatically"
   - **Version**: Seleccionar 3.9.x más reciente

### 3. Configurar Git (si no está)

1. Scroll hasta **Git**

2. Click en "Add Git"

3. Configurar:
   - **Name**: `Default`
   - **Path to Git executable**: `git` (o ruta completa)

4. Click en "Save"

---

## 📦 Crear el Job del Pipeline

### Método 1: Pipeline desde SCM (Git)

Si tienes el código en un repositorio Git:

1. Dashboard → **New Item**

2. Configurar:
   - **Item name**: `mi-playlist-pipeline`
   - **Type**: Pipeline
   - Click "OK"

3. En la configuración del job:

   **General:**
   - ☑ Discard old builds
     - Strategy: Log Rotation
     - Days to keep builds: 7
     - Max # of builds to keep: 10

   **Pipeline:**
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: (tu URL de Git)
   - **Branch**: `*/main` (o tu rama principal)
   - **Script Path**: `Jenkinsfile`

4. Click "Save"

### Método 2: Pipeline Script Directo

Si NO tienes Git configurado:

1. Dashboard → **New Item**

2. Configurar:
   - **Item name**: `mi-playlist-pipeline`
   - **Type**: Pipeline
   - Click "OK"

3. En la configuración del job:

   **Pipeline:**
   - **Definition**: Pipeline script
   - Copiar y pegar el contenido del `Jenkinsfile` en el editor

4. **IMPORTANTE**: Modificar la etapa de Checkout:

```groovy
stage('Checkout') {
    steps {
        script {
            echo '========================================='
            echo '  ETAPA 1: Usando código local'
            echo '========================================='
            echo "Workspace: ${WORKSPACE}"
        }
        // Comentar o eliminar: checkout scm
    }
}
```

5. Click "Save"

---

## 🚀 Ejecutar el Pipeline

### Primera Ejecución

1. Ir al job: `mi-playlist-pipeline`

2. Click en **"Build Now"**

3. Ver el progreso:
   - En la cola de builds aparecerá `#1`
   - Click en el número para ver detalles
   - Click en **"Console Output"** para ver logs

### Interpretando los Resultados

**✅ Build Exitoso:**
- Icono azul/verde
- Todas las etapas completadas
- Artefacto JAR generado

**❌ Build Fallido:**
- Icono rojo
- Ver "Console Output" para detalles
- Revisar la etapa que falló

### Visualización con Blue Ocean (Opcional)

1. Instalar plugin "Blue Ocean"

2. Dashboard → Click en "Open Blue Ocean"

3. Visualización moderna del pipeline con:
   - Timeline de etapas
   - Logs por etapa
   - Historial visual

---

## 🔍 Troubleshooting

### Error: "Java_Home is not set"

**Solución:**
1. Verify JDK configuration en Global Tool Configuration
2. Asegurar que JAVA_HOME apunta a JDK 17+

### Error: "mvn: command not found"

**Solución:**
1. Verificar configuración de Maven en Global Tool Configuration
2. Asegurar "Install automatically" está marcado

### Error: "Workspace is not writable"

**Mac/Linux:**
```bash
# Dar permisos al workspace de Jenkins
chmod -R 755 ~/.jenkins/workspace
```

**Windows:**
- Click derecho en carpeta Jenkins
- Propiedades → Seguridad → Editar
- Dar permisos completos al usuario

### Error: "Port 8080 already in use"

Jenkins usa el puerto 8080 por defecto. Si Mi Playlist también usa ese puerto:

**Opción 1 - Cambiar puerto de Jenkins:**

Mac:
```bash
# Editar archivo de configuración
nano /usr/local/opt/jenkins-lts/homebrew.mxcl.jenkins-lts.plist
# Cambiar --httpPort=8080 a --httpPort=9090
```

**Opción 2 - Cambiar puerto de la aplicación:**

Editar `application.properties`:
```properties
server.port=8081
```

### Tests fallan en Jenkins pero pasan localmente

**Posibles causas:**
1. Versión diferente de Java
2. Falta de permisos de escritura
3. Variables de entorno

**Solución:**
```groovy
// Agregar en Jenkinsfile, stage Test:
environment {
    JAVA_HOME = tool name: 'JDK-17'
}
```

---

## 📊 Configuración de Reportes

### JUnit Test Results

Ya está configurado en el Jenkinsfile:

```groovy
post {
    always {
        junit allowEmptyResults: true,
              testResults: '**/target/surefire-reports/*.xml'
    }
}
```

### Ver Reportes

1. Ir al build específico
2. Click en "Test Result"
3. Ver:
   - Tests pasados/fallados
   - Duración de tests
   - Tendencias

---

## 🔔 Configurar Notificaciones (Opcional)

### Email

1. `Manage Jenkins` → `Configure System`

2. **Extended E-mail Notification**:
   - SMTP server: smtp.gmail.com
   - SMTP Port: 587
   - Credentials: (agregar tu email/password)

3. Agregar en Jenkinsfile:
```groovy
post {
    failure {
        emailext (
            subject: "Build Failed: ${env.JOB_NAME}",
            body: "Build ${env.BUILD_NUMBER} failed",
            to: "tu@email.com"
        )
    }
}
```

---

## 🎯 Mejores Prácticas

### 1. Builds Automáticos

Configurar trigger de builds:

**Poll SCM:**
```
# Verificar cambios cada 5 minutos
H/5 * * * *
```

**GitHub Webhook:**
- Más eficiente
- Build inmediato al hacer push

### 2. Limpieza Automática

Ya configurado en Jenkinsfile:
```groovy
options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
}
```

### 3. Parallel Stages

Para pipelines más rápidos:
```groovy
stage('Tests Paralelos') {
    parallel {
        stage('Unit Tests') {
            steps { sh 'mvn test' }
        }
        stage('Integration Tests') {
            steps { sh 'mvn verify' }
        }
    }
}
```

---

## 📚 Recursos Adicionales

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Pipeline Syntax Reference](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Jenkins Plugins](https://plugins.jenkins.io/)

---

## ✅ Checklist de Configuración

- [ ] Jenkins instalado y corriendo
- [ ] Usuario admin creado
- [ ] JDK 17 configurado
- [ ] Maven 3.9 configurado
- [ ] Job del pipeline creado
- [ ] Primera ejecución exitosa
- [ ] Tests pasando
- [ ] Artefacto JAR generado

---

**¡Listo! Tu pipeline de CI/CD está configurado correctamente. 🎉**
