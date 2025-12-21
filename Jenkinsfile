pipeline {
    agent any

    environment {
        MVN_DOCKER_IMAGE = 'maven:3.9.9-eclipse-temurin-17'
        M2_REPO = "${env.HOME}/.m2"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/userhello1/untitled'
            }
        }


        stage('Build common-events') {
            steps {
                script {
                    docker.image(MVN_DOCKER_IMAGE).inside("-v ${M2_REPO}:/root/.m2") {
                        dir('common-events') {
                            sh 'ls -l'
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build billing-service') {
            steps {
                script {
                    docker.image(MVN_DOCKER_IMAGE).inside("-v ${M2_REPO}:/root/.m2") {
                        dir('billing-service') {
                            sh 'ls -l'
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build customer-service') {
            steps {
                script {
                    docker.image(MVN_DOCKER_IMAGE).inside("-v ${M2_REPO}:/root/.m2") {
                        dir('customer-service') {
                            sh 'ls -l'
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build inventory-service') {
            steps {
                script {
                    docker.image(MVN_DOCKER_IMAGE).inside("-v ${M2_REPO}:/root/.m2") {
                        dir('inventory-service') {
                            sh 'ls -l'
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build discovery-service') {
            steps {
                script {
                    docker.image(MVN_DOCKER_IMAGE).inside("-v ${M2_REPO}:/root/.m2") {
                        dir('discovery-service') {
                            sh 'ls -l'
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build gateway') {
            steps {
                script {
                    docker.image(MVN_DOCKER_IMAGE).inside("-v ${M2_REPO}:/root/.m2") {
                        dir('gateway') {
                            sh 'ls -l'
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
            }
        }

        /*
        stage('Run services with Docker Compose (LOCAL)') {
            steps {
                sh '''
                docker-compose build --no-cache
                '''
            }
        }
        */

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                kubectl apply -f k8s/
                '''
            }
        }

    }

    post {
        always {
            echo "Pipeline terminé"
        }
        success {
            echo "Build réussi"
        }
        failure {
            echo "Build échoué"
        }
    }
}
