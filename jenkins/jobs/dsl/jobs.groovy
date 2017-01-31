// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"
def mobileFolderName =  projectFolderName+ "/Mobile_Apps"
def mobileFolder = folder(mobileFolderName) { displayName('Mobile Applications') }


// Jobs
def codeanalysis = freeStyleJob(mobileFolderName + "/Code_Analysis")
def buildapplication = freeStyleJob(mobileFolderName + "/Build_Application")
def functionaltest = freeStyleJob(mobileFolderName + "/Functional_Test")
def serverappium = freeStyleJob(mobileFolderName + "/Server_Appium")
def deploy = freeStyleJob(mobileFolderName + "/Deploy")


//Pipeline
def sample_pipeline = buildPipelineView(mobileFolderName + "/Mobile_Apps")

sample_pipeline.with{
	title('Mobile_Applications_Pipeline')
    displayedBuilds(5)
    selectedJob("/Deploy")
    refreshFrequency(5)
}

// Job Configuration

codeanalysis.with{

	scm{
		git{
		  remote{
			url('')
			credentials("")
		  }
		  branch("*/master")
		}
	}
	
	configure { Project -> Project / builders << 'hudson.plugins.sonar.SonarRunnerBuilder'{
            project('')
            properties('''# Required metadata
sonar.projectKey=MobileApp
sonar.projectName=Code_Analysis
sonar.projectVersion=1.0
sonar.sources=src''')

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

buildapplication.with{

	scm{
		git{
		  remote{
			url('')
			credentials("")
		  }
		  branch("*/master")
		}
	}
	
	label("WindowsSlave")
	
	steps{
		batchFile('Rem delete old build\ngradlew clean')
		batchFile('Rem build new\ngradlew assembleRelease')
	}
	publishers{
		downstreamParameterized{
		  trigger("Functional_Test,Server_Appium"){
				condition("SUCCESS")
				parameters{
					predefinedProp("CUSTOM_WORKSPACE",'$CUSTOM_WORKSPACE')
				}
			}
		}
	}
	
	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
}


functionaltest.with{
	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
	
	label("WindowsSlave")
	
	scm{
		git{
		  remote{
			url('')
			credentials("")
		  }
		  branch("*/master")
		}
	}
	
	steps{
		shell('cd v1\nmvn test')
	}
	
	publishers{
		downstreamParameterized{
		  trigger("Deploy"){
				condition("SUCCESS")
				parameters{
					predefinedProp("CUSTOM_WORKSPACE",'$CUSTOM_WORKSPACE')
				}
			}
		}
	}
}

serverappium.with{
	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
	
	label("WindowsSlave")
	
	steps{
		batchFile("cd C:\\jenkins\\workspace\\Devops\\Goschedule\\Build_Application \nstart /B server_appium.bat \nping 127.0.0.1 -n 300 | find \"Reply\" >nul \n@echo off")
	}
}