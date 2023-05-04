package org.devops


def pull_code(branchname,srcurl){
    checkout scmGit( branches: [[name: "*/${branchname}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f286958b-d924-4f6e-8720-7a63a2c44717', url: "${srcurl}"]])    
}