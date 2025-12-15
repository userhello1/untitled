pipeline {
    agent any

    stages {

        stage('Build common-events') {
            steps {
                dir('common-events') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Build billing-service') {
            steps {
                dir('billing-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build customer-service') {
            steps {
                dir('customer-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build inventory-service') {
            steps {
                dir('inventory-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build discovery-service') {
            steps {
                dir('discovery-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build gateway') {
            steps {
                dir('gateway') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
    }
}
