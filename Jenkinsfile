pipeline {
    agent any
    tools  {
    jdk "OpenJDK17"
    gradle "Gradle8"
    }

    stages {
        stage('Build') {
            steps {
                sh 'gradle clean build'
            }
        }
        stage('Deploy') {
            when {
                anyOf {
                    branch 'master'
                }
            }
            steps {
                sshagent(['ci-user-ssh']) {
                    sh 'scp -o StrictHostKeyChecking=no ./build/libs/easy-tourney-be-0.0.1-SNAPSHOT.jar easytourney@easy-tourney.mgm-edv.de:/home/easytourney'
                    sh 'ssh  -o StrictHostKeyChecking=no easytourney@easy-tourney.mgm-edv.de "sudo fuser -k -n tcp 8080 || true"'
                    sh 'ssh  -o StrictHostKeyChecking=no easytourney@easy-tourney.mgm-edv.de "nohup java -jar easy-tourney-be-0.0.1-SNAPSHOT.jar > log.txt 2>&1 &"'
                }
            }
        }
    }
}