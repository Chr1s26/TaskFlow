pipeline {

    agent any

    environment {
        DOCKER_USER = "htetkyawswarlinn"
        EC2_IP = "3.106.240.218"
    }

    stages {

         stage('Checkout') {
             steps {
                 git branch: 'main',
                     url: 'https://github.com/Chr1s26/TaskFlow.git'
             }
         }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Backend Image') {
            steps {
                sh 'docker build -t htetkyawswarlinn/taskflow-backend ./backend'
            }
        }

        stage('Build Frontend Image') {
            steps {
                sh 'docker build -t htetkyawswarlinn/taskflow-frontend ./frontend'
            }
        }

        stage('Push Backend Image') {
            steps {
                sh 'docker push htetkyawswarlinn/taskflow-backend'
            }
        }

        stage('Push Frontend Image') {
            steps {
                sh 'docker push htetkyawswarlinn/taskflow-frontend'
            }
        }

        stage('Deploy to EC2') {
            steps {
                sh '''
                ssh -o StrictHostKeyChecking=no ubuntu@3.106.240.218 "
                cd taskflow &&
                docker compose pull &&
                docker compose up -d
                "
                '''
            }
        }

    }
}