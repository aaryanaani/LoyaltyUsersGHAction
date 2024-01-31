#!/usr/bin/groovy

@Library('sketcher-shared-library') _  // Import Jenkins Global Library

pipeline {
  agent any
  environment {
    service_name    = "loyaltyusers"
    dev_branch      = "master"
    rel_branch      = "release"
    yamlfile        = "${service_name}.yaml"
    app_version     = readFile('version.txt').trim()
    build_id        = "${BUILD_NUMBER}" // Jenkins build ID
    dev_version     = "${app_version}.${build_id}"
    release_version = "${app_version}.r${build_id}"
    docker_context  = "${WORKSPACE}"
    dockerfile      = "Dockerfile"
    build_notify    = "xcijv-pkdev@prokarma.com"
    PATH = "/var/lib/cloudbees-jenkins-distribution/.local/bin/:$PATH"
  }
  // START THE PIPELINE
  stages {
    // All branches get built and scaned
    stage ('Build & Scan') {
      steps {
          script {            
               sh "chmod +x gradlew"
               sh "./gradlew  clean build"                           
          } 
      }
    }
    stage ('ShiftLeft Analysis') {
        steps {
            script {
            	sh "./gradlew  clean build"  
                sh '/usr/local/bin/sl analyze --tag app.teamname=Loyalty --vcs-prefix-correction "*=src/main/java" --app LoyaltyUsers --java build/libs/LoyaltyUsers-0.0.1-SNAPSHOT.jar'
            }
        }
    }   
    stage ('Create Image') {
      environment {
        region      = "us-east-2"
        account     = "149216511563" //dev
        docker_repo = "${account}.dkr.ecr.${region}.amazonaws.com"
        docker_url  = "https://149216511563.dkr.ecr.us-east-2.amazonaws.com/loyaltyusers"
      }
      steps {
        script {
          // Clean up
         container.rmDanglingContainers()
         container.rmDanglingImages()
         container.rmDanglingVolumes()
         withAWS(credentials: 'skx-dig-dev-LoyaltyDeveloper', region: 'us-east-2') {
          sh "aws ecr get-login --region ${region} --no-include-email > ecrlogin"
          def command = readFile('ecrlogin').trim()
          sh("${command}")
          sh "docker build --rm --pull --no-cache -t ${service_name} ${docker_context}"
          sh "docker tag ${service_name}:latest ${docker_repo}/${service_name}:${dev_version}"
          sh "docker push ${docker_repo}/${service_name}:${dev_version}"
        }
        }
      }
    }
    
    stage ('Deploy to Dev') {
      environment {
        region      = "us-east-2"
        account     = "149216511563" //dev
        docker_repo = "${account}.dkr.ecr.${region}.amazonaws.com"
        docker_url  = "https://${docker_repo}"
        clusterName = "LoyaltyServices" // eks-dev xcijv-dev-eks-cluster-alpha
        environment = "dev"     
        taskFamily  = "loyaltyusers"
        serviceName = "loyaltyusers"
        taskDefile  = "file://task-definition-${dev_version}.json"
      }
      steps {
        script {
        	input message: 'Do you want to approve the deploy in Dev?'
            withAWS(credentials: 'skx-dig-dev-LoyaltyDeveloper', region: 'us-east-2') {
              sh  " sed -e  's;%BUILD_TAG%;${dev_version};g'  task-definition-input-dev.json > task-definition-${environment}.json"
              sh  " sed -e  's;%BUILD_ENV_VAR%;${environment};g'  task-definition-${environment}.json > task-definition-${dev_version}.json"
              // Get current [TaskDefinition#revision-number]
              def currTaskDef = sh (returnStdout: true,script: "aws ecs describe-task-definition  --task-definition ${taskFamily} | egrep 'revision' | tr ',' ' ' | awk '{print \$2}'").trim()
              // get Current task definition
                       
              if(currTaskDef) {
                    sh  "aws ecs update-service  --cluster ${clusterName}  --service ${serviceName}  --task-definition ${taskFamily}:${currTaskDef} --desired-count 0"
                }  
              //sleep 120 
              //def currentTask = sh ( returnStdout: true, script:" aws ecs list-tasks --cluster ${clusterName} --family ${taskFamily} --output text | egrep 'TASKARNS'| awk '{print \$2}' ").trim()                            
              //if (currentTask) {
              //    sh "aws ecs stop-task --cluster ${clusterName} --task ${currentTask} "
              //}
              sh  " aws ecs register-task-definition  --family ${taskFamily} --cli-input-json ${taskDefile}"
              def taskRevision = sh (returnStdout: true,script:  "aws ecs describe-task-definition --task-definition ${taskFamily} | egrep 'revision'   | tr ',' ' ' | awk '{print \$2}' ").trim()
              // ECS update service to use the newly registered [TaskDefinition#revision]
              sh  "aws ecs update-service  --cluster ${clusterName} --service ${serviceName} --task-definition ${taskFamily}:${taskRevision}  --desired-count 1 "
        }
        }
        }
      }
    stage ('Deploy to QA') {
      environment {
        region      = "us-east-2"
        account     = "149216511563" //dev
        docker_repo = "${account}.dkr.ecr.${region}.amazonaws.com"
        docker_url  = "https://${docker_repo}"
        clusterName = "loyaltyapp-qa" // eks-dev xcijv-dev-eks-cluster-alpha
        environment = "staging"     
        taskFamily  = "loyaltyusers"
        serviceName = "loyaltyusers-qa"
        taskDefile  = "file://task-definition-${dev_version}.json"
      }
      steps {
        script {
            input message: 'Do you want to approve the deploy in QA?'
            withAWS(credentials: 'loyalty-dev', region: 'us-east-1') {
              sh  " sed -e  's;%BUILD_TAG%;${dev_version};g'  task-definition-input-qa.json > task-definition-${environment}.json"
              sh  " sed -e  's;%BUILD_ENV_VAR%;${environment};g'  task-definition-${environment}.json > task-definition-${dev_version}.json"
              // Get current [TaskDefinition#revision-number]
              def currTaskDef = sh (returnStdout: true,script: "aws ecs describe-task-definition  --task-definition ${taskFamily} | egrep 'revision' | tr ',' ' ' | awk '{print \$2}'").trim()
              // get Current task definition
                       
              if(currTaskDef) {
                    sh  "aws ecs update-service  --cluster ${clusterName}  --service ${serviceName}  --task-definition ${taskFamily}:${currTaskDef} --desired-count 0"
                }  
              //sleep 120 
              //def currentTask = sh ( returnStdout: true, script:" aws ecs list-tasks  --cluster ${clusterName} --family ${taskFamily} --output text | egrep 'TASKARNS'| awk '{print \$2}' ").trim()                            
              //if (currentTask) {
              //    sh "aws ecs stop-task --cluster ${clusterName} --task ${currentTask} "
              //}
              sh  " aws ecs register-task-definition  --family ${taskFamily} --cli-input-json ${taskDefile}"
              def taskRevision = sh (returnStdout: true,script:  "aws ecs describe-task-definition --task-definition ${taskFamily} | egrep 'revision'   | tr ',' ' ' | awk '{print \$2}' ").trim()
              // ECS update service to use the newly registered [TaskDefinition#revision]
              sh  "aws ecs update-service  --cluster ${clusterName} --service ${serviceName} --task-definition ${taskFamily}:${taskRevision}  --desired-count 2 "
        }
        }
        }
      }    
    stage ('Deploy to Prod') {
      environment {
        region      = "us-east-2"
        account     = "149216511563" //dev
        docker_repo = "${account}.dkr.ecr.${region}.amazonaws.com"
        docker_url  = "https://${docker_repo}"
        clusterName = "loyaltyapp" // eks-dev xcijv-dev-eks-cluster-alpha
        environment = "prod"     
        taskFamily  = "loyaltyusers"
        serviceName = "loyaltyusers"
        taskDefile  = "file://task-definition-${dev_version}.json"
      }
      steps {
        script {
            input message: 'Do you want to approve the deploy in Prod?'
            withAWS(credentials: 'loyalty-dev', region: 'us-east-1') {
              sh  " sed -e  's;%BUILD_TAG%;${dev_version};g'  task-definition.json > task-definition-${environment}.json"
              sh  " sed -e  's;%BUILD_ENV_VAR%;${environment};g'  task-definition-${environment}.json > task-definition-${dev_version}.json"
              // Get current [TaskDefinition#revision-number]
              def currTaskDef = sh (returnStdout: true,script: "aws ecs describe-task-definition  --task-definition ${taskFamily} | egrep 'revision' | tr ',' ' ' | awk '{print \$2}'").trim()
              // get Current task definition
                        
              if(currTaskDef) {
                    sh  "aws ecs update-service  --cluster ${clusterName}  --service ${serviceName}  --task-definition ${taskFamily}:${currTaskDef} --desired-count 0"
                }
              //sleep 120      
              //def currentTask = sh ( returnStdout: true, script:" aws ecs list-tasks  --cluster ${clusterName} --family ${taskFamily} --output text | egrep 'TASKARNS'| awk '{print \$2}' ").trim()                                
              //if (currentTask) {
              //    sh "aws ecs stop-task --cluster ${clusterName} --task ${currentTask} "
              // }                
              sh  " aws ecs register-task-definition  --family ${taskFamily} --cli-input-json ${taskDefile}"
              def taskRevision = sh (returnStdout: true,script:  "aws ecs describe-task-definition --task-definition ${taskFamily} | egrep 'revision'   | tr ',' ' ' | awk '{print \$2}' ").trim()
              // ECS update service to use the newly registered [TaskDefinition#revision]
              sh  "aws ecs update-service  --cluster ${clusterName} --service ${serviceName} --task-definition ${taskFamily}:${taskRevision}  --desired-count 2 "
        }
        }
        }
      }
    }
    post {
    always {
        echo 'cleaning up workspace'
        deleteDir() /* clean up our workspace */
            }
    }
  }
  
  

