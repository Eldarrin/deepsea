pipeline {
  agent {
      label 'maven'
  }
  stages {
  	stage('Build Deepsea Images') {
      steps {
        sh "mvn -f deepsea-underwriting/deepsea-underwriting-actuarial/pom.xml package fabric8:build -Popenshift"
      }
    }
  }
}