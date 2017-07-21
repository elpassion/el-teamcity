package pl.elpassion.eltc

import pl.elpassion.eltc.api.Build
import pl.elpassion.eltc.api.BuildType
import pl.elpassion.eltc.api.Project
import pl.elpassion.eltc.builds.SelectableProject
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

fun createSelectableProject(name: String, isSelected: Boolean = false) = SelectableProject(
        project = createProject(name = name),
        isSelected = isSelected)

fun createProject(id: String = "Project1", name: String = "Project name") = Project(
        id = id,
        name = name,
        href = "href")
