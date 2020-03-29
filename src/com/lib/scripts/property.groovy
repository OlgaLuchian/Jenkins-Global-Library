properties([[$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
parameters([
    extendedChoice(bindings: '', description: 'Please select docker image to deploy', descriptionPropertyValue: '', groovyClasspath: '', groovyScript: 'return ["Hello"]', multiSelectDelimiter: ',', name: 'selectedDockerImage', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_SINGLE_SELECT', visibleItemCount: 5)
    
    ])])