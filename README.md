# FuchiCorp Jenkins Global Library
#FuchiCorp

![](https://jenkins.io/images/logos/baturro/baturro.png )

This page is contains how to use Jenkins Global Library.  You will need to configure the Jenkins Shared library on your Jenkins to be able to use this library.  Use following link to be able to configure [Jenkins Global Library](https://jenkins.io/doc/book/pipeline/shared-libraries/). On FuchiCorp Jenkins it's already configured by this script  [link](https://github.com/fuchicorp/common_tools/blob/2f0639c77c83b8b7b812434ee2681bf0bbd3f8be/charts/jenkins/values.yaml#L246) 

### Supported Languages 
* Python
* Java


Credentials 
`nexus-docker-creds`

## Build and Deploy 
Deploy job is using Kubernetes plugin in jenkins [link](https://github.com/jenkinsci/kubernetes-plugin) make sure 
Docker build plugin in Jenkins [Link](https://jenkins.io/doc/book/pipeline/docker/)


### Application build process 
To be able to build the application you will need to define class `JenkinsCommonDockerBuildPipeline`  and call a function `runPipeline` You will need to make sure that build job is multi branch job. 

Example code `JenkinsBuilder.groovy`
``` 
@Library('CommonLib@master') _
def common = new com.lib.JenkinsCommonDockerBuildPipeline()
common.runPipeline()
```

You will need to code put `JenkinsBuilder.groovy` in your application.  After you pushed to SCM you will need to create a Jenkins Job with following naming convention `APPNAME-build`.  

[image:F6112E37-9F74-4773-A1AF-7D293213FA82-51230-000123FD96134A02/Screen Shot 2020-04-17 at 1.35.43 AM.png]


## Application deploy process

To be able to do application deployment you will need to define class `JenkinsCommonDeployPipeline` and call the the function `runPipeline`. 

Example Code `JenkinsDeployer.groovy`
```
@Library('CommonLib@master') _
def common = new com.lib.JenkinsCommonDeployPipeline()
common.runPipeline()
```

Same process you will need to push to SCM and create your Jenkins job with following name `APPNAME-deploy`. 
[image:8432DCED-5ACB-438D-88BB-0BC96D5482E5-51230-0001240FE67CF47F/Screen Shot 2020-04-17 at 1.37.04 AM.png]


