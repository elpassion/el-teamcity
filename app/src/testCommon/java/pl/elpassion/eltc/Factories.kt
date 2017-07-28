package pl.elpassion.eltc

import pl.elpassion.eltc.builds.SelectableProject
import java.util.*

fun createBuild(id: Int = 1,
                number: String = "7",
                state: String = "finished",
                status: String = "SUCCESS",
                projectName: String = "Teamcity Android Client",
                statusText: String = "Tests passed: 1",
                startDate: Date = Date(),
                finishDate: Date? = Date(),
                webUrl: String = "webUrl") = Build(
        id = id,
        number = number,
        status = status,
        state = state,
        branchName = "master",
        webUrl = webUrl,
        statusText = statusText,
        queuedDate = Date(),
        startDate = startDate,
        finishDate = finishDate,
        buildType = createBuildType(projectName))

fun createBuildType(projectName: String = "Teamcity Android Client") = BuildType(
        id = "TeamcityAndroidClient_Build",
        name = "Build",
        projectName = projectName)

fun createChange(comment: String = "Comment",
                 username: String = "user",
                 version: String = "fds3fdsa23fdem9ek2nfkd9",
                 date: Date = Date()) = Change(
        id = "1",
        version = version,
        username = username,
        date = date,
        webUrl = "changeUrl",
        comment = comment)

fun createSelectableProject(name: String, isSelected: Boolean = false) = SelectableProject(
        project = createProject(name = name),
        isSelected = isSelected)

fun createProject(id: String = "Project1", name: String = "Project name") = Project(
        id = id,
        name = name,
        href = "href")
