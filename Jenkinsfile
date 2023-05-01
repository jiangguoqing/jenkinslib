//不要使用单引号

@Library('sharedlib')


def tools = new org.devops.tools()
String srcurl = "${env.srcUrl}"
String branchname = "${env.gitbranchName}"
String repo = "566420885017.dkr.ecr.ap-southeast-1.amazonaws.com/java"


environment {
    DOCKER_BUILDKIT='1'
}
//不要使用单引号

gitCommit = ''
branchName = ''
unixTime = ''
developmentTag = ''
releaseTag = ''
image_name = 'mrjiangguoqing/jgq:1.0'
newtag = ''

pipeline {
    agent  {
      kubernetes {
        label 'hello'
        yaml '''
    apiVersion: v1
    kind: Pod
    metadata:
       name: clean-ci
    spec:
       containers:
       - name: trivy
         image: 'aquasec/trivy:0.21.1'
         command: ["/bin/sh"]
         args: ["-c","while true; do sleep 86400; done"]
         volumeMounts:
         - mountPath: /var/run
           name: cache-dir
       - name: docker
         image: 'docker:20.10.16-dind'
         command:
         - dockerd
         - --host=unix:///var/run/docker.sock
         - --host=tcp://0.0.0.0:8000
         - --insecure-registry=167.71.195.24:30002
         securityContext:
           privileged: true
         volumeMounts:
         - mountPath: /var/run
           name: cache-dir
       - name: clean-ci
         image: 'docker:stable'
         command: ["/bin/sh"]
         args: ["-c","while true; do sleep 86400; done"]
         volumeMounts:
         - mountPath: /var/run
           name: cache-dir
       volumes:
       - name: cache-dir
         emptyDir: {}
        '''.stripIndent()
          }
    }

//读取不到，怎么读取？默认怎么设定
parameters {
  string defaultValue: 'http://159.223.41.2:30615/root/ta', name: 'srcurl'
  choice choices: ['dev','master', 'dev'], name: 'gitbranchname'
}


    options {
        timestamps()
//        skip
    }

  triggers {
    GenericTrigger(
     genericVariables: [
      [key: 'ref', value: '$.ref']
     ],
     genericRequestVariables: [
     [key: 'runOpts', regexpFilter: ''],
     [key: 'requestWithNumber', regexpFilter: '[^0-9]'],
     [key: 'requestWithString', regexpFilter: '']
         ],
     causeString: 'Triggered on $ref',
     token: 'jgq-X-go-app',
     regexpFilterExpression: '',
     regexpFilterText: '',
     printContributedVariables: true,
     printPostContent: true
    )
  }




    stages {


/*
    stage('Some step') {
      steps {
        sh "echo $ref"
      }
    }
*/


/*        stage('GWT env') {
            steps {
                sh "echo $ref"
                sh "printenv"
            }
        }
*/

       stage("pull code"){
			steps{
                script {
                branchname = ref - "refs/heads/"
                sh "echo ${branchname}  ${srcurl}"

                tools.PrintMes("获取代码","green")
checkout scmGit(branches: [[name: "*/${branchname}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f286958b-d924-4f6e-8720-7a63a2c44717', url: "${srcurl}"]])
                }
			}
		}


        stage('juge-branch') {
            steps {
                container('docker'){
                script{
                if ("${branchname}" == "release"){
                gitCommit = env.GIT_COMMIT.substring(0,8)
                unixTime = (new Date().time.intdiv(1000))
                developmentTag = "${branchname}-${gitCommit}-${unixTime}"
                //变量必须加在双引号内
                //把镜像拉到本地，然后tag一个新的标签，在cd就行了。
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
                tools.Docker_Build(repo,developmentTag,"${branchname}")
                }
                }
                }
            }
        }


        stage('Example') {
            steps {
                echo "Hello ${params.PERSON}"
            }
        }




/*    stage('Some step') {
      steps {
        sh "echo $ref"
      }
    }
*/


/*        stage("stage 1: Test dingding notify") {
            steps {
            	echo 'Test dingding notify'
                script {
                    env.commit = "${sh(script:'git log --oneline --no-merges|head -1', returnStdout: true)}"
                    sh "echo -------------"
                    sh "echo $branchname"
                    sh "echo $commit"
                    sh "env"
                    //变量如何使用？

                }
            }
        }
*/

/*
		stage("build & SonarQube analysis") {
            steps {
                script {
                scannerHome = tool 'sonarscanner'
                }
                withSonarQubeEnv('sonar') {
                sh "${scannerHome}/bin/sonar-scanner"
                }
            }
        }

       stage("Quality Gate"){
			steps{
				timeout(time: 15, unit: 'MINUTES') {
					waitForQualityGate abortPipeline: false
				}
			}
		}

*/
        stage('code scan') {
            steps {
                timeout(time:5, unit:"MINUTES"){
                    script{
                        println("check code")
                    }
                }
            }
        }

//        stage ('test') {
//            steps {
//                parallel (
//                    "unit tests": { sh 'mvn test' },
//                    "integration tests": { sh 'mvn integration-test' }
//                )
//            }
//        }

        stage('Build') {
            steps {
                container('docker'){
                 script {
                    gitCommit = env.GIT_COMMIT.substring(0,8)
                    unixTime = (new Date().time.intdiv(1000))
                    developmentTag = "${branchname}-${gitCommit}-${unixTime}"
                    sh "mkdir -p /sys/fs/cgroup/systemd"
                    sh "mount -t cgroup -o none,name=systemd cgroup /sys/fs/cgroup/systemd"
                    sh "chmod +x ./mvnw"
                    //sh "env"
                    sh "echo  -----------"
                    sh "echo ${developmentTag}"
                    tools.Docker_Build(repo,developmentTag,"${branchname}")

                }
            }
         }
        }

 /*      stage('Deploy') {
            steps {
                script{
                        docker.withRegistry('https://566420885017.dkr.ap-southeast-1.amazonaws.com', 'ecr:ap-southeast-1:ecr') {
                    app.push("${env.BUILD_NUMBER}")
                    app.push("latest")
                    }
                }
            }
*/


//
/*        stage('scan with trivy') {
            steps {
                container ('trivy'){
                sh "trivy --no-progress --exit-code 1 --severity HIGH,CRITICAL mrjiangguoqing/jgq:1.0"
                //sh "pwd;trivy image -f json -o results.json mrjiangguoqing/jgq:1.0"

                //recordIssues(tools: [trivy(pattern: 'results.json')])
                publishHTML target : [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: '',
                    reportFiles: 'results.json',
                    reportName: 'Trivy Scan',
                    reportTitles: 'Trivy Scan'
                ]
            }
        }
        }
*/
        stage('Test') {
            steps {
emailext attachmentsPattern: 'results.json', body: 'scann', mimeType: 'text/html', recipientProviders: [buildUser(), culprits(), developers(), requestor()], subject: '', to: 'mrjiangguoqing@gmail.com'
                }
            }

        stage('Deploy') {
            steps {
                timeout(time:5, unit:"MINUTES"){
                    script{
                        println("check code")
                    }
                }
            }
        }
    }


post {
    changed {
       echo 'pipeline post changed'
    }
    always {
       echo 'pipeline post always'
    }
    success {
       script{
        echo 'pipeline post always'
       }
    }
    failure {
       script{
            mail to: 'team@example.com', subject: 'Pipeline failed', body: "${env.BUILD_URL}"
       }
    }

    aborted {
      script{
        currentBuild.description += '\n cancel'
      }
    }
    }
}

