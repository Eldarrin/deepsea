node ('master') {
  stage 'build'
  openshiftBuild(buildConfig: 'myapp', showBuildLogs: "true")

  stage 'deploy'
  openshiftDeploy(deploymentConfig: 'myapp')

}