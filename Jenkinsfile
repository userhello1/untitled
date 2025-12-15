pipeline {
    agent any

    stages {

        stage('Build common-events') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/common-events":/app \
                  -v "$HOME/.m2":/root/.m2 \
                  -w /app \
                  maven:3.9.9-eclipse-temurin-17 \
                  mvn clean install -DskipTests
                '''
            }
        }

        stage('Build billing-service') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/billing-service":/app \
                  -v "$HOME/.m2":/root/.m2 \
                  -w /app \
                  maven:3.9.9-eclipse-temurin-17 \
                  mvn clean package -DskipTests
                '''
            }
        }

        stage('Build customer-service') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/customer-service":/app \
                  -v "$HOME/.m2":/root/.m2 \
                  -w /app \
                  maven:3.9.9-eclipse-temurin-17 \
                  mvn clean package -DskipTests
                '''
            }
        }

        stage('Build inventory-service') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/inventory-service":/app \
                  -v "$HOME/.m2":/root/.m2 \
                  -w /app \
                  maven:3.9.9-eclipse-temurin-17 \
                  mvn clean package -DskipTests
                '''
            }
        }

        stage('Build discovery-service') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/discovery-service":/app \
                  -v "$HOME/.m2":/root/.m2 \
                  -w /app \
                  maven:3.9.9-eclipse-temurin-17 \
                  mvn clean package -DskipTests
                '''
            }
        }

        stage('Build gateway') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/gateway":/app \
                  -v "$HOME/.m2":/root/.m2 \
                  -w /app \
                  maven:3.9.9-eclipse-temurin-17 \
                  mvn clean package -DskipTests
                '''
            }
        }
    }
}
