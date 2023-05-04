package org.devops

def codescan(){
                scannerHome = tool 'sonarscanner'
                }
                withSonarQubeEnv('sonar') {
                sh "${scannerHome}/bin/sonar-scanner"
                waitForQualityGate abortPipeline: false
}
