pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/ReyazShaik/java-project-maven-new.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Scanning') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
                }
            }
        }

        stage('Artifacts') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Uploading to S3') {
            steps {
                s3Upload(
                    consoleLogLevel: 'INFO',
                    profileName: 'S3Cred',
                    userMetadata: [],
                    dontWaitForConcurrentBuildCompletion: false,
                    pluginFailureResultConstraint: 'FAILURE',
                    dontSetBuildResultOnFailure: false,
                    entries: [[
                        sourceFile: 'target/*.war',
                        bucket: 'jenkins-test-djibril',
                        selectedRegion: 'us-east-1',
                        noUploadOnFailure: true,
                        flatten: true
                    ]]
                )
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                ansiblePlaybook(
                    credentialsId: 'ansibleCred',
                    disableHostKeyChecking: true,
                    installation: 'ansible',
                    inventory: '/etc/ansible/hosts',
                    playbook: '/etc/ansible/deploy.yml',
                    vaultTmpPath: ''
                )
            }
        }
    }
}