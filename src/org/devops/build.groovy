package org.devops


def Docker_Build(images_name,images_tag,branchname){
              properties([
                parameters([
                  string(defaultValue: "$images_name:${images_tag}", description: 'Variable description', name: 'latest_tag', trim: true)
                ])
              ])

              sh 'echo ---'
              sh "echo ${latest_tag}"
              sh "docker version"
              //sh "docker build -t $images_name:$images_tag ."
              //sh 'docker build -t mrjiangguoqing/gojgq-dev-${GIT_BRANCH}-${GIT_SHA:0:7}-$(date +%s):v5 .'
              //sh 'docker login  -u mrjiangguoqing -p jgq123'
              sh 'docker images'
              sh "echo $images_tag $images_name"
              docker.withRegistry('https://566420885017.dkr.ecr.ap-southeast-1.amazonaws.com', 'ecr:ap-southeast-1:ecr'){
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


