pipeline {
  agent {
      label 'maven'
  }
  stages {
  	stage('Build Deepsea Images') {
      steps {
        sh "mvn -f deepsea-underwriting/pom.xml package fabric8:build -Popenshift"
      }
    }
  }
}