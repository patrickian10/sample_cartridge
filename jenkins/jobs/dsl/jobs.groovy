// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"
def mobileFolderName =  projectFolderName+ "/Mobile_Apps"
def mobileFolder = folder(mobileFolderName) { displayName('Mobile Applications') }


// Jobs
def codeanalysis = freeStyleJob(projectFolderName + "/Code_Analysis")
def buildapplication = freeStyleJob(projectFolderName + "/Build_Application")
def functionaltest = freeStyleJob(projectFolderName + "/Functional_Test")
def serverappium = freeStyleJob(projectFolderName + "/Server_Appium")
def deploy = freeStyleJob(projectFolderName + "/Deploy")


//Pipeline
def sample_pipeline = buildPipelineView(projectFolderName + "/Mobile_Apps_Pipeline")

sample_pipeline.with{
	title('Mobile_Applications_Pipeline')
    displayedBuilds(1)
    selectedJob("/Code_Analysis")
    refreshFrequency(5)
}

// Job Configuration

//START OF CODE ANALYSIS JOB CONFIGURATION//
codeanalysis.with{

	configure { Project ->
        Project / builders << 'hudson.plugins.sonar.SonarRunnerBuilder'{
            project('')
            properties('''# Required metadata
sonar.projectKey=MobileApp
sonar.projectName=Code_Analysis
sonar.projectVersion=1.0
sonar.sources=src

            javaOpts('')
            additionalArguments('')
            jdk('(Inherit From Job)')
            task('')
        }
    }

	publishers{
		downstreamParameterized{
		  trigger("Build_Application"){
				condition("SUCCESS")
				parameters{
					predefinedProp("CUSTOM_WORKSPACE",'$WORKSPACE')
				}
			}
		}
	}
}
//END OF CODE ANALYSIS JOB CONFIGURATION//

//START OF BUILD APPLICATION JOB CONFIGURATION//
buildapplication.with{

	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
	configure { Project -> Project {
		assignedNode('WindowsSlave')
		}
	}
	
	
	scm{
		git{
			remote{
			url('')
			credentials("")
			}
			branch("*/master")
			extensions {
				cloneOptions {
					timeout(60)
				}
			}
		}
	}
	steps{
		batchFile('Rem delete old build\n gradlew clean')
		batchFile('Rem build new\n gradlew assembleRelease')
	}
	publishers{
		downstreamParameterized{
		  trigger("Functional_Test,Server_Appium"){
				condition("SUCCESS")
				parameters{
					predefinedProp("CUSTOM_WORKSPACE",'$WORKSPACE')
				}
			}
		}
	}
}
//END OF BUILD APPLICATION JOB CONFIGURATION//