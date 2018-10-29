node{
  stage ('Build') {
 
    git url: 'https://github.com/Eldarrin/deepsea'
 
    withMaven(
        // Maven installation declared in the Jenkins "Global Tool Configuration"
        maven: 'maven',
        // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
        // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
        ) {
 
      // Run the maven build
      sh "mvn clean package fabric8:build -Popenshift"
 
    } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe & FindBugs reports...
  }
}