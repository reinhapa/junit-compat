#!groovy
 
buildNow()
 
private void buildNow() {
    checkEnvironment()
 
    commitStage()
 
    if (!isOnMaster()) {
        return;
    }
 
    integrationStage()
}
 
private void checkEnvironment() {
    node {
        echo "Building on branch: ${env.BRANCH_NAME}"
    }
}

private void commitStage() {
    stage name: 'Commit'
 
    node {
        if (isScmConfigured()) {
            // This is the path in a "multibranch workflow" job.
            checkout scm
        } else {
            // This is the path if you copy paste the script into a "workflow" job.
            // It simplifies development.
            git 'https://github.com/reinhapa/junit-compat.git'
        }

        gradle 'clean'
        try {
            gradle '--refresh-dependencies --continue --stacktrace build'
            gradle '--continue --stacktrace -x test'
        } finally {
            step([$class: 'JUnitResultArchiver', testResults: 'build/test-results/TEST-*.xml'])
        }
    }
}

private boolean isOnMaster() {
    return !env.BRANCH_NAME || env.BRANCH_NAME == 'master';
}

private boolean isScmConfigured() {
    // if the SCM is not configured, then the branch name is null
    return env.BRANCH_NAME;
}

private void integrationStage() {
}

private void gradle(args) {
    if (isUnix()) {
        sh "./gradlew $args"
    } else {
        bat "./gradlew.bat $args"
    }
}