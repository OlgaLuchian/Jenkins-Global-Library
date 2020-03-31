def repositoryName = 'academy'
def gitCommitHash = 'ccf891f'


node("master") {
    println("${docker_image}")
    build job: 'academy-fuchicorp-deploy/master', 
    parameters: [
        [$class: 'BooleanParameterValue', name: 'terraform_apply', value: true],
        [$class: 'StringParameterValue', name: 'selectedDockerImage', value: "${repositoryName}:${gitCommitHash}"], 
        [$class: 'StringParameterValue', name: 'environment', value: 'dev']
        
        ]
}