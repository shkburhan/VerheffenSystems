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
                sshagent(['ec2-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no $REMOTE_USER@$REMOTE_HOST 'sudo rm -rf $REMOTE_PATH/*'
                        ssh $REMOTE_USER@$REMOTE_HOST 'mkdir -p /tmp/deploy-temp'
                        scp -o StrictHostKeyChecking=no -r ./* $REMOTE_USER@$REMOTE_HOST:/tmp/deploy-temp
                        ssh $REMOTE_USER@$REMOTE_HOST 'sudo mv /tmp/deploy-temp/* $REMOTE_PATH && sudo chown -R www-data:www-data $REMOTE_PATH'
                        ssh $REMOTE_USER@$REMOTE_HOST 'sudo systemctl reload nginx'
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
