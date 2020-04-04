#!/usr/bin/env groovy
package com.lib.scripts

def isAdmin(username) {
    def instance = Jenkins.getInstance()
    return instance.getAuthorizationStrategy().getACL(User.get(username))
    .hasPermission(User.get(username).impersonate(), hudson.model.Hudson.ADMINISTER)
}


return this 