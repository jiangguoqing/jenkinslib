package org.devops


def juge_branch(){
                if ("${branchname}" == "release"){
                skip = "true"
                gitCommit = env.GIT_COMMIT.substring(0,8)
                unixTime = (new Date().time.intdiv(1000))
                developmentTag = "${branchname}-${gitCommit}-${unixTime}"

                sh "echo ${latest_tag}"
                sh "echo I am there"
                docker.withRegistry('https://566420885017.dkr.ecr.ap-southeast-1.amazonaws.com', 'ecr:ap-southeast-1:ecr'){
                sh "docker pull ${latest_tag}"
                sh "docker images"
                sh "docker tag ${latest_tag} $repo:${developmentTag}"
                sh "docker push  $repo:${developmentTag}"
                sh "echo really nice!"
                }
                } 

                if ("${branchname}" == "master"){
                skip = "true"
                gitCommit = env.GIT_COMMIT.substring(0,8)
                unixTime = (new Date().time.intdiv(1000))
                developmentTag = "${branchname}-${gitCommit}-${unixTime}"

                sh "echo ${latest_tag}"
                sh "echo I am there"
                docker.withRegistry('https://566420885017.dkr.ecr.ap-southeast-1.amazonaws.com', 'ecr:ap-southeast-1:ecr'){
                sh "docker pull ${latest_tag}"
                sh "docker images"
                sh "docker tag ${latest_tag} $repo:${developmentTag}"
                sh "docker push  $repo:${developmentTag}"
                sh "echo really nice!"
                }
                }                   

}