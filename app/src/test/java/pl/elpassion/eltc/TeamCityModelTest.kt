@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import pl.elpassion.eltc.api.InvalidCredentialsException
import pl.elpassion.eltc.api.TeamCityApi
import pl.elpassion.eltc.api.UnknownHostException
import pl.elpassion.eltc.builds.BuildsRepository
import pl.elpassion.eltc.builds.SelectableProject
import pl.elpassion.eltc.login.AuthData
import pl.elpassion.eltc.login.LoginRepository

class TeamCityModelTest {

    val loginRepository = mock<LoginRepository>()
    val buildsRepository = mock<BuildsRepository>()
    val api = mock<TeamCityApi>()
    val model = TeamCityModelImpl(api, loginRepository, buildsRepository)
    val observer = TestObserver<AppState>()

    @Before
    fun setup() {
        whenever(api.getProjects()).thenNever()
        whenever(api.getBuilds()).thenNever()
        model.state.subscribe(observer)
    }

    @Test
    fun `Emit InitialState before app start`() {
        observer.assertValue(InitialState)
    }

    @Test
    fun `Start LoadingState on SubmitCredentials action`() {
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        observer.assertLastValue(LoadingState)
    }

    @Test
    fun `Display correct error on submitting unknown host`() {
        whenever(api.getBuilds()).thenError(UnknownHostException)
        model.perform(SubmitCredentials("invalid", "user:pass"))
        observer.assertLastValue(LoginState(error = LoginState.Error.UNKNOWN_HOST))
    }

    @Test
    fun `Display invalid credentials error on unauthorized call to teamcity api`() {
        whenever(api.getBuilds()).thenError(InvalidCredentialsException)
        model.perform(SubmitCredentials("http://teamcity:8111", "user:wrong_pass"))
        observer.assertLastValue(LoginState(error = LoginState.Error.INVALID_CREDENTIALS))
    }

    @Test
    fun `Display builds and projects lists from api`() {
        val buildList = listOf(
                createBuild(id = 668),
                createBuild(id = 669))
        val projectList = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        whenever(api.getBuilds()).thenJust(buildList)
        whenever(api.getProjects()).thenJust(projectList)
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        observer.assertLastValue(BuildsState(buildList, projectList))
    }

    @Test
    fun `Save credentials in repository on login`() {
        whenever(api.getProjects()).thenJust(emptyList())
        whenever(api.getBuilds()).thenJust(emptyList())
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        verify(loginRepository).authData = AuthData("http://teamcity:8111", "user:pass")
    }

    @Test
    fun `Clear credentials in repository on login error`() {
        whenever(api.getBuilds()).thenError(UnknownHostException)
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        verify(loginRepository).authData = null
    }

    @Test
    fun `Start loading if credentials are available in repository on app start`() {
        stubLoginRepositoryToReturnAuthData()
        model.perform(StartApp)
        observer.assertLastValue(LoadingState)
    }

    @Test
    fun `Display login if credentials are not available in repository on app start`() {
        whenever(loginRepository.authData).thenReturn(null)
        model.perform(StartApp)
        observer.assertLastValue(LoginState())
    }

    @Test
    fun `Display builds if auth data available in repository on app start`() {
        whenever(api.getBuilds()).thenJust(emptyList())
        stubLoginRepositoryToReturnAuthData()
        model.perform(StartApp)
        verify(api).getBuilds()
    }

    @Test
    fun `Call api for filtered builds when specific projects are selected`() {
        val selectedProjects = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        whenever(api.getBuildsForProjects(any())).thenJust(emptyList())
        stubLoginRepositoryToReturnAuthData()
        whenever(buildsRepository.selectedProjects).thenReturn(selectedProjects)
        model.perform(StartApp)
        verify(api).getBuildsForProjects(projectIds = selectedProjects.map { it.id })
    }

    @Test
    fun `Start loading on refresh list`() {
        stubLoginRepositoryToReturnAuthData()
        model.perform(RefreshList)
        observer.assertLastValue(LoadingState)
    }

    @Test
    fun `Display select projects dialog on select projects action`() {
        val allProjects = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        whenever(api.getProjects()).thenJust(allProjects)
        whenever(api.getBuilds()).thenJust(emptyList())
        stubLoginRepositoryToReturnAuthData()
        model.perform(StartApp)
        model.perform(SelectProjects)
        observer.assertLastValue(SelectProjectsDialogState(
                allProjects.map { SelectableProject(it, false) }))
    }

    @Test
    fun `Select currently selected projects on select projects action`() {
        val project1 = createProject(id = "Project1")
        val project2 = createProject(id = "Project2")
        val selectedProjects = listOf(project1)
        val allProjects = listOf(project1, project2)
        whenever(api.getProjects()).thenJust(allProjects)
        whenever(api.getBuildsForProjects(any())).thenJust(emptyList())
        stubLoginRepositoryToReturnAuthData()
        whenever(buildsRepository.selectedProjects).thenReturn(selectedProjects)
        model.perform(StartApp)
        model.perform(SelectProjects)
        observer.assertLastValue(SelectProjectsDialogState(listOf(
                SelectableProject(project1, isSelected = true),
                SelectableProject(project2, isSelected = false))))
    }

    @Test
    fun `Display only builds for selected projects`() {
        val allBuilds = listOf(
                createBuild(id = 668),
                createBuild(id = 669),
                createBuild(id = 670))
        val selectedProjects = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        val allProjects = selectedProjects + listOf(createProject(id = "Project3"))
        whenever(api.getProjects()).thenJust(allProjects)
        whenever(api.getBuildsForProjects(any())).thenJust(listOf(allBuilds[0], allBuilds[1]))
        stubLoginRepositoryToReturnAuthData()
        whenever(buildsRepository.selectedProjects).thenReturn(selectedProjects)
        model.perform(StartApp)
        model.perform(SelectProjects)
        model.perform(SubmitProjects(selectedProjects))
        observer.assertLastValue(BuildsState(listOf(allBuilds[0], allBuilds[1]), allProjects))
    }

    @Test
    fun `Save selected projects in repository`() {
        val selectedProjects = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        val allProjects = selectedProjects + listOf(createProject(id = "Project3"))
        whenever(api.getProjects()).thenJust(allProjects)
        model.perform(SubmitProjects(selectedProjects))
        verify(buildsRepository).selectedProjects = selectedProjects
    }

    @Test
    fun `Display login on logout`() {
        model.perform(Logout)
        observer.assertLastValue(LoginState())
    }

    @Test
    fun `Clear auth data on logout`() {
        model.perform(Logout)
        verify(loginRepository).authData = null
    }

    @Test
    fun `Clear selected projects on logout`() {
        model.perform(Logout)
        verify(buildsRepository).selectedProjects = emptyList()
    }

    @Test
    fun `Display login without error on login error accepted`() {
        whenever(api.getBuilds()).thenError(UnknownHostException)
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        model.perform(AcceptLoginError)
        observer.assertLastValue(LoginState(error = null))
    }

    private fun stubLoginRepositoryToReturnAuthData() {
        whenever(loginRepository.authData).thenReturn(AuthData("http://teamcity:8111", "user:pass"))
    }
}