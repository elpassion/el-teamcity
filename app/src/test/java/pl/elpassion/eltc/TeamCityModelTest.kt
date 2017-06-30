@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import java.util.*

class TeamCityModelTest {

    val repository = mock<Repository>()
    val api = mock<TeamCityApi>()
    val model = TeamCityModel(api, repository)
    val observer = TestObserver<AppState>()

    @Before
    fun setup() {
        model.state.subscribe(observer)
    }

    @Test
    fun `Emit InitialState before app start`() {
        observer.assertValue(InitialState)
    }

    @Test
    fun `Start LoadingState on SubmitCredentials action`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.never())
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        observer.assertLastValue(LoadingState)
    }

    @Test
    fun `Display correct error on submitting unknown host`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.error(UnknownHostException))
        model.perform(SubmitCredentials("invalid", "user:pass"))
        observer.assertLastValue(UnknownHostState)
    }

    @Test
    fun `Display invalid credentials error on unauthorized call to teamcity api`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.error(InvalidCredentialsException))
        model.perform(SubmitCredentials("http://teamcity:8111", "user:wrong_pass"))
        observer.assertLastValue(InvalidCredentialsState)
    }

    @Test
    fun `Display builds and projects lists from api`() {
        val buildList = listOf(
                createBuild(id = 668),
                createBuild(id = 669))
        val projectList = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        whenever(api.getBuilds(any())).thenReturn(Single.just(buildList))
        whenever(api.getProjects(any())).thenReturn(Single.just(projectList))
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        observer.assertLastValue(MainState(buildList, projectList))
    }

    @Test
    fun `Call api with passed credentials`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.just(emptyList()))
        model.perform(SubmitCredentials("http://teamcity:8111", "user1:pass1"))
        verify(api).getBuilds(credentials = "user1:pass1")
    }

    @Test
    fun `Save credentials in repository`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.just(emptyList()))
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        verify(repository).authData = AuthData("http://teamcity:8111", "user:pass")
    }

    @Test
    fun `Start loading if credentials are available in repository on app start`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.never())
        whenever(repository.authData).thenReturn(AuthData("http://teamcity:8111", "user:pass"))
        model.perform(StartApp)
        observer.assertLastValue(LoadingState)
    }

    @Test
    fun `Display login if credentials are not available in repository on app start`() {
        whenever(repository.authData).thenReturn(null)
        model.perform(StartApp)
        observer.assertLastValue(LoginState)
    }

    @Test
    fun `Call api with passed credentials if available in repository on app start`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.just(emptyList()))
        whenever(repository.authData).thenReturn(AuthData("http://teamcity:8111", "user:pass"))
        model.perform(StartApp)
        verify(api).getBuilds(credentials = "user:pass")
    }

    @Test
    fun `Start loading on refresh list`() {
        whenever(api.getProjects(any())).thenReturn(Single.never())
        whenever(api.getBuilds(any())).thenReturn(Single.never())
        whenever(repository.authData).thenReturn(AuthData("http://teamcity:8111", "user:pass"))
        model.perform(RefreshList)
        observer.assertLastValue(LoadingState)
    }

    @Test
    fun `Display select projects dialog on select projects action`() {
        val projectList = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        whenever(api.getProjects(any())).thenReturn(Single.just(projectList))
        whenever(api.getBuilds(any())).thenReturn(Single.just(emptyList()))
        whenever(repository.authData).thenReturn(AuthData("http://teamcity:8111", "user:pass"))
        model.perform(StartApp)
        model.perform(SelectProjects)
        observer.assertLastValue(SelectProjectsDialogState(projectList))
    }

    @Test
    fun `Display only builds for specified project`() {
        val buildList = listOf(
                createBuild(id = 668),
                createBuild(id = 669))
        val projectBuildList = buildList.take(1)
        val projectList = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        whenever(api.getProjects(any())).thenReturn(Single.just(projectList))
        whenever(api.getBuilds(any())).thenReturn(Single.just(buildList))
        whenever(api.getBuildsForProject(any(), any())).thenReturn(Single.just(projectBuildList))
        whenever(repository.authData).thenReturn(AuthData("http://teamcity:8111", "user:pass"))
        model.perform(StartApp)
        model.perform(SelectProjects)
        model.perform(SubmitProject(projectList.first()))
        observer.assertLastValue(MainState(projectBuildList, projectList))
    }

    private fun createBuild(id: Int) = Build(
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

    private fun createBuildType() = BuildType(
            id = "TeamcityAndroidClient_Build",
            name = "Build",
            projectName = "Teamcity Android Client")

    private fun createProject(id: String) = Project(
            id = id,
            name = "Project name",
            href = "href")
}