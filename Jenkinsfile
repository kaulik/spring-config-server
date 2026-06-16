pipeline {
    agent any

    parameters {
        string(
            name: 'BUILD_ID',
            defaultValue: "${env.BUILD_NUMBER}",
            description: 'Docker image tag / build identifier'
        )
    }

    environment {
        IMAGE_NAME = "spring-config-server"
        FULL_IMAGE  = "${IMAGE_NAME}:${params.BUILD_ID}"
    }

    tools {
        maven 'Maven3'
    }

    stages {

        stage('Validate') {
            steps {
                echo "BUILD_ID : ${params.BUILD_ID}"
                sh 'docker info'
                sh 'ls -la'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean compile -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                sh """
                    docker build \
                      --build-arg BUILD_ID=${params.BUILD_ID} \
                      -t ${IMAGE_NAME}:${params.BUILD_ID} \
                      -t ${IMAGE_NAME}:latest \
                      .
                """
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([
                    string(credentialsId: 'CONFIG_REPO_URI',      variable: 'CONFIG_REPO_URI'),
                    string(credentialsId: 'CONFIG_REPO_USERNAME',  variable: 'CONFIG_REPO_USERNAME'),
                    string(credentialsId: 'CONFIG_REPO_PASSWORD',  variable: 'CONFIG_REPO_PASSWORD')
                ]) {
                    sh """
                        docker stop ${IMAGE_NAME} || true
                        docker rm   ${IMAGE_NAME} || true
                        docker ps -q --filter publish=8686 | xargs -r docker stop
                        docker ps -aq --filter publish=8686 | xargs -r docker rm
                        docker run -d \
                          --name ${IMAGE_NAME} \
                          -p 8686:8686 \
                          -e CONFIG_REPO_URI=${CONFIG_REPO_URI} \
                          -e CONFIG_REPO_USERNAME=${CONFIG_REPO_USERNAME} \
                          -e CONFIG_REPO_PASSWORD=${CONFIG_REPO_PASSWORD} \
                          -e CONFIG_REPO_BRANCH=main \
                          -e BUILD_ID=${params.BUILD_ID} \
                          --add-host=host.docker.internal:host-gateway \
                          --restart unless-stopped \
                          ${IMAGE_NAME}:${params.BUILD_ID}
                    """
                }
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f || true'
        }
        success {
            echo "Deployed ${env.FULL_IMAGE} successfully"
        }
        failure {
            echo "Build failed"
        }
    }
}
