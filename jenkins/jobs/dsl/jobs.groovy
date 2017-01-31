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
