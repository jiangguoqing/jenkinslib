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


def Docker_Build(images_name,images_tag){
              sh 'echo ---'
              sh "docker version"
              //sh "docker build -t $images_name:$images_tag ."
              //sh 'docker build -t mrjiangguoqing/gojgq-dev-${GIT_BRANCH}-${GIT_SHA:0:7}-$(date +%s):v5 .'
              //sh 'docker login  -u mrjiangguoqing -p jgq123'
              sh 'docker images'
              sh "echo $images_tag $images_name"
              docker.withRegistry('https://566420885017.dkr.ap-southeast-1.amazonaws.com', 'ecr:ap-southeast-1:ecr'){
              def newApp = docker.build "$images_name:${images_tag}"
              newApp.push()
              }
              //sh "docker push $images_name:$images_tag"
              //sh 'docker push mrjiangguoqing/gojgq-dev-${GIT_BRANCH}-${GIT_SHA:0:7}-$(date +%s):v5'
              sh '''
              echo "you did it!!!!!!!  yes!!"
              '''
              //def image = "$images_name:$images_tag"
              //return image
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



