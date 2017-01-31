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
def sample_pipeline = buildPipelineView(mobileFolderName + "/Mobile_Apps_Pipeline")

sample_pipeline.with{
	title('Mobile_Applications_Pipeline')
    displayedBuilds(1)
    selectedJob("/Code_Analysis")
    refreshFrequency(5)
}
