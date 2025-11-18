/**
 * Jenkinsfile para Mi Playlist Musical
 * Pipeline de CI/CD completo siguiendo las mejores prácticas
 *
 * Etapas del Pipeline:
 * 1. Checkout - Obtener código del repositorio
 * 2. Build - Compilar la aplicación
 * 3. Test - Ejecutar tests automáticos
 * 4. Package - Crear el artefacto JAR
 * 5. Deploy - Desplegar la aplicación
 */

pipeline {
    agent any

    tools {
        maven 'Maven-3.9'  // Configurar en Jenkins Global Tool Configuration
        jdk 'JDK-17'       // Configurar en Jenkins Global Tool Configuration
    }

    environment {
        // Variables de entorno
        APP_NAME = 'mi-playlist'
        APP_VERSION = '1.0.0'
        DEPLOY_DIR = "${WORKSPACE}/deploy"
        JAR_NAME = "${APP_NAME}-${APP_VERSION}.jar"

        // Puerto de la aplicación
        APP_PORT = '8080'

        // Colores para output (opcional)
        ANSI_COLOR_MAP = 'xterm'
    }

    options {
        // Mantener solo los últimos 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))

        // Timeout para el pipeline completo
        timeout(time: 30, unit: 'MINUTES')

        // Timestamps en el log
        timestamps()

        // Deshabilitar checkout automático
        skipDefaultCheckout()
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    echo '========================================='
                    echo '  ETAPA 1: Checkout del Código'
                    echo '========================================='
                }

                // Checkout del código fuente
                checkout scm

                script {
                    echo "✓ Código obtenido exitosamente"
                    echo "Workspace: ${WORKSPACE}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo '========================================='
                    echo '  ETAPA 2: Build de la Aplicación'
                    echo '========================================='
                }

                // Limpiar y compilar el proyecto
                sh '''
                    echo "Limpiando proyecto..."
                    mvn clean

                    echo "Compilando proyecto..."
                    mvn compile -DskipTests
                '''

                script {
                    echo "✓ Build completado exitosamente"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo '========================================='
                    echo '  ETAPA 3: Ejecución de Tests'
                    echo '========================================='
                }

                // Ejecutar tests unitarios
                sh '''
                    echo "Ejecutando tests unitarios..."
                    mvn test
                '''

                script {
                    echo "✓ Tests ejecutados exitosamente"
                }
            }

            post {
                always {
                    // Publicar resultados de tests
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

                    script {
                        echo "Resultados de tests publicados"
                    }
                }
            }
        }

        stage('Code Quality') {
            steps {
                script {
                    echo '========================================='
                    echo '  ETAPA 4: Análisis de Calidad de Código'
                    echo '========================================='
                }

                // Análisis de código con Checkstyle (opcional)
                script {
                    try {
                        sh 'mvn checkstyle:checkstyle'
                        echo "✓ Análisis de código completado"
                    } catch (Exception e) {
                        echo "⚠ Advertencia: Checkstyle encontró problemas (no crítico)"
                    }
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    echo '========================================='
                    echo '  ETAPA 5: Empaquetado de la Aplicación'
                    echo '========================================='
                }

                // Crear el JAR ejecutable
                sh '''
                    echo "Creando JAR ejecutable..."
                    mvn package -DskipTests

                    echo "Verificando artefacto generado..."
                    ls -lh target/*.jar
                '''

                script {
                    echo "✓ Aplicación empaquetada exitosamente"
                }
            }

            post {
                success {
                    // Archivar el artefacto generado
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true

                    script {
                        echo "✓ Artefacto archivado en Jenkins"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo '========================================='
                    echo '  ETAPA 6: Deployment'
                    echo '========================================='
                }

                // Preparar directorio de deployment
                sh '''
                    echo "Preparando directorio de deployment..."
                    mkdir -p ${DEPLOY_DIR}

                    # Copiar JAR al directorio de deployment
                    cp target/*.jar ${DEPLOY_DIR}/${JAR_NAME}

                    echo "Copiando scripts de deployment..."
                    cp deploy-mac.sh ${DEPLOY_DIR}/ || true
                    cp deploy-windows.bat ${DEPLOY_DIR}/ || true

                    # Hacer ejecutables los scripts
                    chmod +x ${DEPLOY_DIR}/*.sh || true

                    echo "Contenido del directorio de deployment:"
                    ls -lh ${DEPLOY_DIR}
                '''

                script {
                    echo "✓ Aplicación lista para deployment"
                    echo ""
                    echo "==================================================="
                    echo "  Deployment Manual:"
                    echo "  - Mac/Linux: ./deploy-mac.sh"
                    echo "  - Windows: deploy-windows.bat"
                    echo "==================================================="
                }
            }
        }
    }

    post {
        success {
            script {
                echo ''
                echo '================================================='
                echo '  ✓ PIPELINE COMPLETADO EXITOSAMENTE'
                echo '================================================='
                echo "Build #${BUILD_NUMBER} - ${currentBuild.fullDisplayName}"
                echo "Duración: ${currentBuild.durationString}"
                echo "Artefacto: ${JAR_NAME}"
                echo '================================================='
            }
        }

        failure {
            script {
                echo ''
                echo '================================================='
                echo '  ✗ PIPELINE FALLÓ'
                echo '================================================='
                echo "Build #${BUILD_NUMBER} - ${currentBuild.fullDisplayName}"
                echo "Revisa los logs para más detalles"
                echo '================================================='
            }
        }

        always {
            // Limpiar workspace si es necesario (opcional)
            script {
                echo "Limpieza de workspace..."
            }
        }
    }
}
