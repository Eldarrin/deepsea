pipeline {
  agent {
      label 'maven'
  }
  stages {
  	stage('Build Deepsea Common') {
      steps {
        sh "mvn -f deepsea-common/pom.xml install"
      }
    }
    stage('Build Deepsea Shared') {
      steps {
        sh "mvn -f deepsea-shared/pom.xml package fabric8:build -Popenshift"
      }
    }
    stage('Build Deepsea Underwriting Actuarial') {
      steps {
        sh "mvn -f deepsea-underwriting/deepsea-underwriting-actuarial/pom.xml package fabric8:build -Popenshift"
      }
    }
    stage('Build Deepsea Underwriting UI') {
      steps {
        sh "mvn -f deepsea-underwriting/deepsea-underwriting-ui/pom.xml package fabric8:build -Popenshift"
      }
    }
  }
}