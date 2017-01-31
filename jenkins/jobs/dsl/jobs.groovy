// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"
def mobileFolderName =  projectFolderName+ "/Mobile_Apps"
def mobileFolder = folder(mobileFolderName) { displayName('Mobile Applications') }
def goschedFolderName = mobileFolderName + "/GoSchedule"
def goschedFolder = folder(goschedFolderName)

//Repositories
def gitrepo =  "https://github.com/cvolalo/goschedule.git"
def samplegit = "https://innersource.accenture.com/scm/tbu/adop-cartridge-goschedule.git"
def pomlocation = "C:\\Users\\mary.l.l.dela.torre\\Desktop\\repo\\original_repo\\goschedule\\v1\\pom.xml"

// Jobs
def buildapplication = freeStyleJob(projectFolderName + "/Build_Application")
def codeanalysis = freeStyleJob(projectFolderName + "/Code_Analysis")
def functionaltest = mavenJob(projectFolderName + "/Functional_Test")
def serverappium = freeStyleJob(projectFolderName + "/Server_Appium")
def deploy = freeStyleJob(projectFolderName + "/Deploy")
def sample = freeStyleJob(projectFolderName + "/SampleJob")
def sample2 = freeStyleJob(projectFolderName + "/SampleJob2")

//Pipeline

def sample_pipeline = buildPipelineView(projectFolderName + "/Sample_Pipeline")

sample_pipeline.with{
	title('SamplePipeline')
    displayedBuilds(1)
    selectedJob("/SampleJob")
    refreshFrequency(5)
}

// Job Configuration
buildapplication.with{
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
		  trigger("Code_Analysis"){
				condition("SUCCESS")
				parameters{
					predefinedProp("CUSTOM_WORKSPACE",'$WORKSPACE')
				}
			}
		}
	}
}

codeanalysis.with{
	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
	configure { Project ->
        Project / builders << 'hudson.plugins.sonar.SonarRunnerBuilder'{
            project('')
            properties('''# Required metadata
sonar.projectKey=org.sonarqube:java-simple-sq-scanner
sonar.projectName=Java :: Simple Project Not Compiled :: SonarQube Scanner
sonar.projectVersion=1.0

# Comma-separated paths to directories with sources (required)
sonar.sources=.

# Language
sonar.language=java

# Encoding of the source files
sonar.sourceEncoding=UTF-8''')
            javaOpts('')
            additionalArguments('')
            jdk('(Inherit From Job)')
            task('')
        }
    }
	publishers{
		downstreamParameterized{
		  trigger("Server_Appium, Functional_Test"){
				condition("SUCCESS")
				parameters{
					predefinedProp("CUSTOM_WORKSPACE",'$WORKSPACE')
				}
			}
		}
	}
}

functionaltest.with{
	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
	rootPOM(pomlocation)
	goals('test')
	publishers{
		downstreamParameterized{
		  trigger("Deploy"){
				condition("SUCCESS")
				parameters{
					predefinedProp("CUSTOM_WORKSPACE",'$WORKSPACE')
				}
			}
		}
	}
}

serverappium.with{
	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
	steps{
		batchFile('start server_appium.bat \n @echo off \n ping 127.0.0.1 -n 420 \n Taskkill /im node.exe /f')
	}
}

deploy.with{
	parameters{
		stringParam("CUSTOM_WORKSPACE","","")
	}
	steps{
		batchFile('cd C:\\Users\\mary.l.l.dela.torre\\.jenkins\\workspace\\Build Application\\build\\outputs\\apk \n copy "Build Application-release.apk" "C:\\Users\\mary.l.l.dela.torre\\OneDrive - Accenture\\GoScheduleAPK\\Build Application-release.apk"')
	}
}

sample.with{
	wrappers {
        preBuildCleanup()
    }
	scm{
		git{
			remote{
			url(samplegit)
			credentials("adop-jenkins-master")
			}
			branch("*/master")
			extensions {
				cloneOptions {
					timeout(60)
				}
			}
		}
	}
	steps {
        shell('echo Hello World!')
    }
	publishers {
        downstreamParameterized {
            trigger('SampleJob2') {
                condition('SUCCESS')
                parameters {
                    predefinedProp("var1", "hello")
					
                }
            }
        }
    }
}
sample2.with{
    parameters {
        stringParam('var1', '', '')
    }
	steps {
        shell('echo $var1!')
    }
}
