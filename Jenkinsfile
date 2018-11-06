pipeline {
  agent {
      label 'maven'
  }
  stages {
  	stage('Build Deepsea Images') {
      steps {
        sh "mvn package fabric8:build -Popenshift"
      }
    }
  }
}