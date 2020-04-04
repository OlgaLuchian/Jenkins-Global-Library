#!/usr/bin/env groovy
package com.lib

def isAdmin(username) {
    def instance = Jenkins.getInstance()
    return instance.getAuthorizationStrategy().getACL(User.get(username))
    .hasPermission(User.get(username).impersonate(), hudson.model.Hudson.ADMINISTER)
}


def scheduleBaseJobs(String baseName, String jobName) {
  /* If Job name contains 'base' and branch name is master or develop
  * scheduleBaseJobs schedule the job to run from 1 through 6
  */

  if (baseName.contains('base'))  {
    if (jobName == 'master' || jobName == 'develop') {
      properties([[$class: 'RebuildSettings',
      autoRebuild: false,
      rebuildDisabled: false],
      // “At minute 0 past every hour from 1 through 6.”
      pipelineTriggers([cron('0 1-6 * * *')])])
    }
  }
}

return this 