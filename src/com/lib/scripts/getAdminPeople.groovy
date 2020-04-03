import hudson.model.Hudson
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy    

def trigerUser = 'fsadykov'
def environment = "prod" 
def commonFunction = new com.lib.commonFunction()

if (trigerUser in prodRole ) {
    println("You are allowed to do prod deployments!!")
} else {
    if (commonFunction.isAdmin(trigerUser)) {
        println("You are alowed to do this trigger")
    } else {
        println("You are not allowed to do prod deployments!!")
    }
}



 
def authStrategy = Hudson.instance.getAuthorizationStrategy()

def cleanUsers = { it.flatten().sort().unique() - "null"}
if(authStrategy instanceof RoleBasedAuthorizationStrategy){
           
   def permissions = authStrategy.roleMaps.inject([:]){map, it -> map + it.value.grantedRoles}
   def users = cleanUsers(permissions*.value)

   println(users)

}else{
   println "Not able to list the permissions by user"
} 