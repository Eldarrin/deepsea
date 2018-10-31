pipeline {
  agent {
      label 'maven'
  }
  stages {
  	stage('Build Deepsea') {
      steps {
        sh "mvn clean fabric8:deploy -Popenshift"
      }
    }
  }
}