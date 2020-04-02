def trigerUser = 'fsadykov'
def environment = "prod" 
def adminList = ["tunji", "ktalant", "aclipco"]


if (trigerUser in adminList ) {
    println("You are allowed to do prod deployments!!")
} else {
    if (environment != 'prod') {
        println("You are alowed to do this trigger")
    } else {
        println("You are not allowed to do prod deployments!!")
    }
}

import com.michelin.cio.hudson.plugins.rolestrategy.*
import groovy.json.JsonSlurper
def myJsonreader = new JsonSlurper()
def members = myJsonreader.parse(new URL("https://fsadykov:111a221e0f7f59f81899247cc2f519f48e@jenkins.fuchicorp.com/role-strategy/strategy/getRole?type=globalRoles&roleName=admin"))


def roleBasedAuthenticationStrategy = new RoleBasedAuthorizationStrategy()

println(roleBasedAuthenticationStrategy.doGetRole(type='globalRoles', roleName='admin'))
type='globalRoles', roleName='admin'
getRole ?type=globalRoles&roleName=admin'