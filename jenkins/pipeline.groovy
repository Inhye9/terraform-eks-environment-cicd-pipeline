def printEnvs() {
    stage("PrintENVs") {
        print "echo APP_NAME :: ${APP_NAME}"
        print "echo PRJ_TARGET :: ${PRJ_TARGET}"
        print "echo PRJ_NAME :: ${PRJ_NAME}"
        print "echo JAVA_OPS :: ${JAVA_OPS}"
        print "echo GIT_BRANCH :: ${GIT_BRANCH}"
        print "echo GIT_APP_REPOS :: ${GIT_APP_REPOS}"
        print "echo GIT_CREDENTIALS_ID :: ${GIT_CREDENTIALS_ID}"
        print "echo MVN_COMMAND :: ${MVN_COMMAND}"
        print "echo DOCKER_IMAGE_NAME :: ${DOCKER_IMAGE_NAME}"
        print "echo ARGOCD_CREDENTIALS_ID :: ${ARGOCD_CREDENTIALS_ID}"
        print "echo S3_UTIL_CMD :: ${S3_UTIL_CMD}"
        print "echo S3_SOURCE_DIRS :: ${S3_SOURCE_DIRS}"
        print "echo S3_DELETE_CACHE_PATH :: ${S3_DELETE_CACHE_PATH}"
        print "echo S3_BUCKET_NAME :: ${S3_BUCKET_NAME}"
        print "echo S3_KEY :: ${S3_KEY}"
        print "echo S3_CACHE_DIST_ID :: ${S3_CACHE_DIST_ID}"
        print "echo ENABLE_S3_UPLOAD_YN :: ${ENABLE_S3_UPLOAD_YN}"
        print "echo ENABLE_S3_CACHE_YN :: ${ENABLE_S3_CACHE_YN}"
        print "echo ENABLE_BUILD_YN :: ${ENABLE_BUILD_YN}"
        print "echo ENABLE_BUILD_NODE_YN :: ${ENABLE_BUILD_NODE_YN}"
        print "echo ENABLE_DOCKER_YN :: ${ENABLE_DOCKER_YN}"
        print "echo ENABLE_DOCKER_NODE_YN :: ${ENABLE_DOCKER_NODE_YN}"
        print "echo ENABLE_DEPLOY_YN :: ${ENABLE_DEPLOY_YN}"
        print "echo BUILD_NUMBER :: ${BUILD_NUMBER}"
        print "echo WORKSPACE :: ${env.WORKSPACE}"
        
    }

}

def gitCheckout(url, branch, credentialsId) {
    script {
        if (branch.contains("/tags/")) {
            checkout([$class: 'GitSCM',
                      branches: [[name: "${branch}"]],
                      userRemoteConfigs: [
                              [url: "${url}", credentialsId: "${credentialsId}"]
                      ],
                      poll: false,
                      extensions: [[$class: 'CloneOption',
                                    depth: 0,
                                    noTags: false,
                                   ]]
            ])

        } else {
            checkout([$class: 'GitSCM',
                      branches: [[name: "${branch}"]],
                      userRemoteConfigs: [
                              [url: "${url}", credentialsId: "${credentialsId}"]
                      ]
            ])

        }

    }
}

def _push2S3(s3Cmd, srcDir, s3BucketName, s3Key) {

    try{
        sh """#!/bin/bash
            echo "s3 bucket : ${s3BucketName} upload"
            ${s3Cmd} ${srcDir} s3://${s3BucketName}${s3Key} --delete &> /dev/null
            """
    } catch(e) {
        sh "echo ${e}"
    }
}


def _nuxt2S3(s3Cmd, srcDir, s3BucketName, s3Key) {

	sh """#!/bin/bash
	    ${s3Cmd} ${srcDir} s3://${s3BucketName}${s3Key} &> /dev/null
	"""

}

def _deleteCache(id, path){
    try{
        sh """#!/bin/bash
            echo "cache delete"
            aws cloudfront create-invalidation --distribution-id ${id} --paths ${path} &> /dev/null
            """
    } catch(e) {
        sh "echo ${e}"
    }
}





def nuxtToS3() {
    if(ENABLE_S3_NUXT_UPLOAD_YN == "Y") {
        stage('Push To Nuxt S3') {
            script {
				dir("project") {                        	
                    this._nuxt2S3(S3_UTIL_CMD, S3_NUXT_SOURCE_DIRS, S3_BUCKET_NAME, S3_NUXT_KEY)
                }
            }
        }
    }
}



/**
 * push static resource to Object Storage
 *
 * @return
 */
def pushToS3() {
    if(ENABLE_S3_UPLOAD_YN == "Y") {
        stage('Push To S3') {
            script {

				dir("project") {                        	
                    this._push2S3(S3_UTIL_CMD, S3_SOURCE_DIRS, S3_BUCKET_NAME, S3_KEY)
                }
            }
        }
    }

    if(ENABLE_S3_CACHE_YN == "Y") {
        stage('Cache Delete') {
            script {

                dir("project") {
                    this._deleteCache(S3_CACHE_DIST_ID, S3_DELETE_CACHE_PATH)
                }
            }
        }
    }
}

/**
 * Maven Build and make jar file..
 * @return
 */
def buildMvn() {
    stage('MVN Build') {
        dir("project") {
            script {
            	configFileProvider([configFile(fileId: 'maven-settings-xml', variable: 'MVN_SETTINGS')]){
		              echo "${MVN_COMMAND}"
   			          sh """#!/bin/bash
        	          ${MVN_COMMAND} -s $MVN_SETTINGS 
            	      """
            	}
            }
        }
    }
}

/**
 * NodeJs Build and make jar file..
 * @return
 */
def buildNodeJs() {
    stage('NodeJs Build') {
        dir("project") {
            nodejs(nodeJSInstallationName: 'nodejs 16.3.0') {
            	if(PRJ_TARGET == "dev" && ENABLE_DOCKER_NODE_YN == "Y") {
                sh """#!/bin/bash
                    export NODE_OPTIONS=--max_old_space_size=2048
					npm install
					rm package-lock.json
					npm run ${BUILD_PROFILE}                    
                    
                """
                }else{
                	sh """#!/bin/bash
					npm install
					rm package-lock.json
					npm run ${BUILD_PROFILE}                    
                    
                """
                }
            }
        }
    }
}

/**
 * Make docker image and push to storage(nexus docker repository)
 * PRD: whatap 사용 o 
 * QA : whatap 사용 X  --> whatap jar 파일을 소스코드에서 lookup  
 * STG: whatap 사용 O
 * DEV: whatap 사용 X
 * @return
 */
def copyLibrarys() {
	if(PRJ_TARGET == "prd"){
		if(ENABLE_DOCKER_YN == "Y") {
			sh """#!/bin/bash
			if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/prd/java/* project/plugins/whatap/
	        
	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/prd/Dockerfile_v5 project/Dockerfile &> /dev/null
	        """
	    }
	    else if(ENABLE_DOCKER_NODE_YN == 'Y'){	    
	        sh """#!/bin/bash
	        if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/prd/nodejs/* project/
	        
	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/prd/DockerfileForNode_v5 project/Dockerfile &> /dev/null
	    	"""
	    }
	} else if (PRJ_TARGET == "qa") {
		if(ENABLE_DOCKER_YN == "Y") {
			sh """#!/bin/bash
			if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/qa/java/* project/plugins/whatap/
	        
	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/qa/Dockerfile_v5 project/Dockerfile &> /dev/null
	        """
	    }
	    else if(ENABLE_DOCKER_NODE_YN == 'Y'){	    
	        sh """#!/bin/bash
	        if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/qa/nodejs/* project/
	        
	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/qa/DockerfileForNode_v5 project/Dockerfile &> /dev/null
	        """
	    }
	} else if (PRJ_TARGET == "stg") {
		if(ENABLE_DOCKER_YN == "Y") {
			sh """#!/bin/bash
			if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/stg/java/* project/plugins/whatap/
	        
	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/stg/Dockerfile_v5 project/Dockerfile &> /dev/null
	        """
	    }
	    else if(ENABLE_DOCKER_NODE_YN == 'Y'){	    
	        sh """#!/bin/bash
	        if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/stg/nodejs/* project/

	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/stg/DockerfileForNode_v5 project/Dockerfile &> /dev/null
	        """
	    }
	} else {
		if(ENABLE_DOCKER_YN == "Y") {
			sh """#!/bin/bash			
			if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/dev/java/* project/plugins/whatap/
	        
	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/dev/Dockerfile_v5 project/Dockerfile &> /dev/null
	        """
	    }
	    else if(ENABLE_DOCKER_NODE_YN == 'Y'){	    
	        sh """#!/bin/bash
	        if [ ! -d project/plugins/whatap ]; then 
				mkdir -p project/plugins/whatap
			fi
	        cp -r scm/plugins/whatap/dev/nodejs/* project/

	        cp -r scm/plugins/isms project/plugins/
	        cp -r scm/docker/dev/DockerfileForNode_v5 project/Dockerfile &> /dev/null
	        """
	    }
	}
	
}

def buildDocker() {

    stage('Docker Image') {
        dir("project") {
        	def img = ""
        	if(PRJ_TARGET == "qa" && ENABLE_DOCKER_YN == "Y") {
        	   img = docker.build(
                    "${DOCKER_IMAGE_NAME}:latest",
                    "-f \"Dockerfile\" . \
                    --build-arg FILE_EXT=${MVN_PACKAGING_FORMAT} \
                    --build-arg PRJ_NAME=${PRJ_NAME} \
                    --build-arg JAVA_OPTS_MEM='${JAVA_OPS}' \
                    --no-cache \
                    "
            )
            }else{
            	img = docker.build(
                    "${DOCKER_IMAGE_NAME}:latest",
                    "-f \"Dockerfile\" . \
                    --build-arg FILE_EXT=${MVN_PACKAGING_FORMAT} \
                    --build-arg PRJ_NAME=${PRJ_NAME} \
                    --build-arg JAVA_OPTS_MEM='${JAVA_OPS}' \
                    "
            )
            }

            retry(3) {
                docker.withRegistry("${DOCKER_REPO_URL}", "${DOCKER_REPO_CREDENTIALS_ID}") {
                    retry(5) {
                        try{
                            img.push("latest")
                        } catch (e) {
                            sleep(10)
                            throw e
                        }
                    }
                }
            }
        }
    }
}

def buildDockerNode() {

    stage('Docker Image') {
        dir("project") {
            def img = docker.build(
                    "${DOCKER_IMAGE_NAME}:latest",
                    "-f \"Dockerfile\" . \
                    --build-arg BUILD_PROFILE=${BUILD_PROFILE} \
                    --build-arg START_PROFILE=${START_PROFILE} \
                    "
            )
            retry(3) {
                docker.withRegistry("${DOCKER_REPO_URL}", "${DOCKER_REPO_CREDENTIALS_ID}") {
                    retry(5) {
                        try{
                            img.push("latest")
                        } catch (e) {
                            sleep(10)
                            throw e
                        }
                    }
                }
            }
        }
    }
}

def _deliveryWithArgoCD() {
	sh '''
		argocd app set ${APP_NAME} -p application.build_number=${BUILD_NUMBER}
		${ARGOCD_SYNC_CMD}
	'''
}

/**
 * Deploy to kubernetes cluster with argocd
 *
 * @return
 */
def delivery() {
    if(ENABLE_DEPLOY_YN == "Y") {
        stage('Deploy') {
        	dir("scm/chart") {
				script {				
		            try {
						withCredentials([string(credentialsId: "${ARGOCD_CREDENTIALS_ID}", variable: 'ARGOCD_AUTH_TOKEN')]) {
			            	this._deliveryWithArgoCD()
						}	
		            } catch(e) {
		                sh "echo ${e}"
		                throw e
		            }
				}
			}
        }
    }
}

/**
 * clean up resources before completing the jenkins pipeline work
 * @return
 */
def clean() {

	cleanWs()

    if(ENABLE_DOCKER_YN == "Y" || ENABLE_BUILD_NODE_YN == "Y" ) {
        script {
            try {
                sh """#!/bin/bash
                	docker images -q -f dangling=true | xargs --no-run-if-empty docker rmi
                """
            } catch (e) {
                sh "echo ${e}"
            }
            
            /**
             * Delete the copied plugins folder during Docker build

		    *dir("project/plugins") {
		    *  deleteDir()
		    *}
             */
        }
    }
}

def cleanUp() {
    stage('CleanUp') {
        this.clean()
    }
}

def build() {
    try{
	    stage('Clone') {
	        dir("project") {
	            this.gitCheckout(GIT_APP_REPOS, GIT_BRANCH, GIT_CREDENTIALS_ID)
	        }
	    }
	
	    this.copyLibrarys()
	
	    if(ENABLE_BUILD_NODE_YN == "Y") {
	        this.buildNodeJs()
	    }else if(ENABLE_BUILD_YN == "Y"){
	    	this.buildMvn()
	    }
	
	    if(ENABLE_DOCKER_YN == "Y") {
	        this.buildDocker()
	    }else if(ENABLE_DOCKER_NODE_YN == "Y") {
	        this.buildDockerNode()
	        this.nuxtToS3()
	    }
	
	    this.delivery()
	    this.pushToS3()
	
	}finally{
		this.cleanUp()
	}

}

return this
