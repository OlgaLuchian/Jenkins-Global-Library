#!/usr/bin/env groovy
package com.lib
import groovy.json.JsonSlurper
import hudson.FilePath


def runPipeline() {
  def common_docker = new JenkinsDeployerPipeline()
  def environment = ""
  def gitCommitHash = ""
  def branch = "${scm.branches[0].name}".replaceAll(/^\*\//, '').replace("/", "-").toLowerCase()
  def k8slabel = "jenkins-pipeline-${UUID.randomUUID().toString()}"
  def repositoryName = "${JOB_NAME}"
      .split('/')[0]
      .replace('-fuchicorp', '')
      .replace('-build', '')
      .replace('-deploy', '')

  if (branch.contains('feature')) {
      repositoryName = repositoryName + '-feature'
  }

  try {
    properties([
      parameters([
        booleanParam(defaultValue: false,
          description: 'Click this if you would like to deploy to latest',
          name: 'PUSH_LATEST'
          )])])

      def slavePodTemplate = """
      metadata:
        labels:
          k8s-label: ${k8slabel}
        annotations:
          jenkinsjoblabel: ${env.JOB_NAME}-${env.BUILD_NUMBER}
      spec:
        affinity:
          podAntiAffinity:
            requiredDuringSchedulingIgnoredDuringExecution:
            - labelSelector:
                matchExpressions:
                - key: component
                  operator: In
                  values:
                  - jenkins-jenkins-master
              topologyKey: "kubernetes.io/hostname"
        containers:
        - name: docker
          image: docker:latest
          imagePullPolicy: Always
          command:
          - cat
          tty: true
          volumeMounts:
            - mountPath: /var/run/docker.sock
              name: docker-sock
            - mountPath: /etc/secrets/service-account/
              name: google-service-account
        - name: fuchicorptools
          image: fuchicorp/buildtools
          imagePullPolicy: Always
          command:
          - cat
          tty: true
          volumeMounts:
            - mountPath: /var/run/docker.sock
              name: docker-sock
            - mountPath: /etc/secrets/service-account/
              name: google-service-account
        serviceAccountName: common-service-account
        securityContext:
          runAsUser: 0
          fsGroup: 0
        volumes:
          - name: google-service-account
            secret:
              secretName: google-service-account
          - name: docker-sock
            hostPath:
              path: /var/run/docker.sock
    """

  podTemplate(name: k8slabel, label: k8slabel, yaml: slavePodTemplate) {
      node(k8slabel) {

        container('fuchicorptools') {
          stage("Pulling the code") {
            checkout scm
            echo env.GIT_COMMIT
            echo env.GIT_BRANCH
            echo env.GIT_REVISION
            gitCommitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
            
          }

          stage('Build docker image') {
            dir("${WORKSPACE}/deployments/docker") {
              // Build the docker image
              dockerImage = docker.build(repositoryName, "--build-arg branch_name=${branch} .")
            }
          }

          stage('Push image') {


            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "nexus-docker-creds", usernameVariable: 'docker_username', passwordVariable: 'docker_password']]) {
              sh """#!/bin/bash -e

              until docker login --username ${env.docker_username} --password ${env.docker_password} https://docker.fuchicorp.com
              do
                echo "Trying to login to docker private system"
                sleep 3
              done
              """
            }


             // Push image to the Nexus with new release
              docker.withRegistry('https://docker.fuchicorp.com', 'nexus-docker-creds') {
                  dockerImage.push("${gitCommitHash}") 
                  // messanger.sendMessage("slack", "SUCCESS", slackChannel)

                  if (params.PUSH_LATEST) {
                    dockerImage.push("latest")
                }
              }


           }

           stage("Clean up")

           sh "docker rmi --no-prune docker.fuchicorp.com/${repositoryName}:0.${BUILD_NUMBER}"

           if (params.PUSH_LATEST) {
             sh "docker rmi --no-prune docker.fuchicorp.com/${repositoryName}:latest"
           }

         }
      }
    }
  } catch (e) {
    currentBuild.result = 'FAILURE'
    println("ERROR Detected:")
    println(e.getMessage())
  }
}



return this
