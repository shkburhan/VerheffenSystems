pipeline {
    agent any

    environment {
        REMOTE_USER = 'ubuntu'
        REMOTE_HOST = '3.111.149.65'
        REMOTE_PATH = '/var/www/html'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/shkburhan/VerheffenSystems.git'
            }
        }

        stage('Build (Optional)') {
            steps {
                echo '⚙️ Build step here (optional for HTML/CSS)'
                // You can add npm build steps here if needed
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo '🚀 Deploying to EC2...'
                sshagent(['ec2-ssh-key2']) {
                    sh """
                        scp -o StrictHostKeyChecking=no -r * ubuntu@3.111.149.65:/var/www/html/
                        scp -o StrictHostKeyChecking=no ubuntu@3.111.149.65 "sudo cp -r /tmp/deploy/* /var/www/html/"
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Deployment completed successfully!'
        }
        failure {
            echo '❌ Deployment failed.'
        }
    }
}
