package org.devops


//格式化输出
def PrintMes(value,color){
    colors = ['red'   : "\033[40;31m >>>>>>>>>>>${value}<<<<<<<<<<< \033[0m",
              'blue'  : "\033[47;34m ${value} \033[0m",
              'green' : "[1;32m>>>>>>>>>>${value}>>>>>>>>>>[m",
              'green1' : "\033[40;32m >>>>>>>>>>>${value}<<<<<<<<<<< \033[0m" ]
    ansiColor('xterm') {
        println(colors[color])
    }
}



def Docker_Build(images_name,images_tag,branchname,dir){
              properties([
                parameters([
                  string(defaultValue: "$images_name:${images_tag}", description: 'Variable description', name: 'latest_tag', trim: true)
                ])
              ])

              sh 'echo ---'
              sh "echo ${latest_tag}"
              sh "docker version"
              sh "docker login -u mrjiangguoqing -p jgq123"
              //sh "docker build -t $images_name:$images_tag ."
              //sh 'docker build -t mrjiangguoqing/gojgq-dev-${GIT_BRANCH}-${GIT_SHA:0:7}-$(date +%s):v5 .'
              //sh 'docker login  -u mrjiangguoqing -p jgq123'
              sh 'docker images'
              sh "echo $images_tag $images_name"
              //dir("src/accountingservice/") {
              
              if (dir == 'cartservice'|| dir == '' ) {
              docker.withRegistry('https://566420885017.dkr.ecr.ap-southeast-1.amazonaws.com', 'ecr:ap-southeast-1:ecr'){
              def newApp = docker.build("$images_name:${images_tag}","-f src/${dir}/src/Dockerfile .")  
              //"src/accountingservice/Dockerfile"
              newApp.push()                                  
              } 
              }
              else {
              docker.withRegistry('https://566420885017.dkr.ecr.ap-southeast-1.amazonaws.com', 'ecr:ap-southeast-1:ecr'){
              def newApp = docker.build("$images_name:${images_tag}","-f src/${dir}/Dockerfile .")  
              //"src/accountingservice/Dockerfile"
              newApp.push()
                    }
              }
              //}
              //sh "docker push $images_name:$images_tag"
              //sh 'docker push mrjiangguoqing/gojgq-dev-${GIT_BRANCH}-${GIT_SHA:0:7}-$(date +%s):v5'
              sh '''
              echo "you did it!!!!!!!  yes!!"
              '''
              //def image = "$images_name:$images_tag"
              //return image
}


//代码拉取
def pull_code(){
                    branchname = ref - "refs/heads/"
                    sh "echo ${branchname}  ${srcurl}"

                    tools.PrintMes("获取代码","green")
    checkout scmGit(branches: [[name: "*/${branchname}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f286958b-d924-4f6e-8720-7a63a2c44717', url: "${srcurl}"]])

}








def Sonar() {
    timeout(time: 3, unit: 'MINUTES') {
        echo "Initializing quality gates..."
        sh 'sleep 10' //small delay because project quality can still being published on previous stage (specially on bigger projects).  
        def result = waitForQualityGate() //this is enabled by quality gates plugin: https://wiki.jenkins.io/display/JENKINS/Quality+Gates+Plugin
        if (result.status != 'OK') {
             error "Pipeline aborted due to quality gate failure: ${result.status}"
        } else {
             echo "Quality gate passed with result: ${result.status}"
        }
    }
}



