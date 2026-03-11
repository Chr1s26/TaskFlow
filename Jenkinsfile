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
                sh '''
                docker buildx build \
                --platform linux/amd64 \
                -t htetkyawswarlinn/taskflow-backend:latest \
                --push ./backend
                '''
            }
        }

        stage('Build Frontend Image') {
            steps {
                sh '''
                docker buildx build \
                --platform linux/amd64 \
                -t htetkyawswarlinn/taskflow-frontend:latest \
                --push ./frontend
                '''
            }
        }

    stage('Deploy to EC2') {
        steps {
            sshagent(['ec2-key']) {
                sh '''
                ssh -o StrictHostKeyChecking=no ubuntu@3.106.240.218 "
                cd taskflow &&
                docker-compose pull &&
                docker-compose up -d
                "
                '''
            }
        }
    }

    }
}