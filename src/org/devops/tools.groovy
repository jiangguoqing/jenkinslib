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


def Docker_Build(){
              sh 'docker build -t 167.71.195.24:30002/myharbor/gojgq-dev-${GIT_BRANCH}-${GIT_SHA:0:7}-$(date +%s):v5 .'
              sh 'docker login 167.71.195.24:30002 -u jgq -p Jgq123456'
              sh 'docker push 167.71.195.24:30002/myharbor/gojgq-dev:v5'
              sh '''
              echo "you did it!!!!!!!  yes!!"
              '''
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




/**
 * This pipeline will build and deploy a Docker image with Kaniko
 * https://github.com/GoogleContainerTools/kaniko
 * without needing a Docker host
 *
 * You need to create a jenkins-docker-cfg secret with your docker config
 * as described in
 * https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/#create-a-secret-in-the-cluster-that-holds-your-authorization-token
 */

def podTemplate(yaml: '''
              kind: Pod
              spec:
                containers:
                - name: kaniko
                  image: gcr.io/kaniko-project/executor:v1.6.0-debug
                  imagePullPolicy: Always
                  command:
                  - sleep
                  args:
                  - 99d
                  volumeMounts:
                    - name: jenkins-docker-cfg
                      mountPath: /kaniko/.docker
                volumes:
                - name: jenkins-docker-cfg
                  projected:
                    sources:
                    - secret:
                        name: regcred
                        items:
                          - key: .dockerconfigjson
                            path: config.json
'''
  ) {

  node(POD_LABEL) {
    stage('Build with Kaniko') {
      git 'https://github.com/jenkinsci/docker-inbound-agent.git'
      container('kaniko') {
        sh '/kaniko/executor -f `pwd`/Dockerfile -c `pwd` --insecure --skip-tls-verify --cache=true --destination=mydockerregistry:5000/myorg/myimage'
      }
    }
  }
}
