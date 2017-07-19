package pl.elpassion.eltc

import java.util.*

fun createBuild(id: Int = 1, number: Int = 7) = Build(
        id = id,
        number = number,
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

fun createProject(id: String = "Project1", name: String = "Project name") = Project(
        id = id,
        name = name,
        href = "href")
