/*** BEGIN META {
  "name" : "Change AWS Credentials",
  "comment" : "Modify AWS credentials in aws-credentials-plugin",
  "parameters" : ['old_access_key', 'new_access_key', 'new_secret_key'],
  "authors" : [
    { name : "Rishabh Das" }
  ]
} END META**/

import com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl

def updateCredentials = { old_access_key, new_aws_access_key, new_aws_secret_key ->
    def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.jenkins.plugins.awscredentials.AmazonWebServicesCredentials.class,
        jenkins.model.Jenkins.instance
    )

    def c = creds.findResult {it.accessKey==old_access_key ? it : null}

    if (c){
        println "Found credentials ${c.id}"

        def credentials_store = jenkins.model.Jenkins.instance.getExtensionList(
            "com.cloudbees.plugins.credentials.SystemCredentialsProvider"
        )[0].getStore()

        def result = credentials_store.updateCredentials(
            com.cloudbees.plugins.credentials.domains.Domain.global(), 
            c, 
            new AWSCredentialsImpl(
                c.scope, c.id, new_aws_access_key, new_aws_access_key, c.description
            )
        )

        if (result) {
            println "Access Keys updated for ${c.id}" 
        } else {
            println "Failed to change Access Keys for ${c.id}"
        }
    } else {
      println "Could not find credential for ${old_access_key}"
    }
}

updateCredentials("$old_access_key", "$new_access_key", "$new_secret_key")
