package pl.elpassion.eltc

import java.util.*

fun createBuild(id: Int) = Build(
        id = id,
        number = 7,
        status = "SUCCESS",
        state = "finished",
        branchName = "master",
        webUrl = "webUrl",
        statusText = "Tests passed: 1",
        queuedDate = Date(),
        startDate = Date(),
        finishDate = Date(),
        buildType = createBuildType())

fun createBuildType() = BuildType(
        id = "TeamcityAndroidClient_Build",
        name = "Build",
        projectName = "Teamcity Android Client")

fun createProject(id: String) = Project(
        id = id,
        name = "Project name",
        href = "href")
