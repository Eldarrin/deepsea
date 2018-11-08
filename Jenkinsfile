pipeline {
  agent {
      label 'maven'
  }
  stages {
  	stage('Build Deepsea Images') {
      steps {
        sh "mvn clean fabric8:deploy -Popenshift"
      }
    }
  }
}