@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import com.nhaarman.mockito_kotlin.*
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
import pl.elpassion.eltc.settings.Settings
import pl.elpassion.eltc.settings.SettingsRepository

class TeamCityModelTest {

    private val loginRepository = mock<LoginRepository>()
    private val buildsRepository = mock<BuildsRepository>()
    private val settingsRepository = mock<SettingsRepository>()
    private val api = mock<TeamCityApi>()
    private val model = TeamCityModelImpl(api, loginRepository, buildsRepository, settingsRepository)
    private val observer = TestObserver<AppState>()

    @Before
    fun setup() {
        whenever(api.getProjects()).thenNever()
        whenever(api.getBuilds()).thenNever()
        whenever(api.getQueuedBuilds()).thenNever()
        whenever(api.getChanges(any())).thenNever()
        whenever(api.getTests(any())).thenNever()
        whenever(api.getProblemOccurrences(any())).thenNever()
        whenever(settingsRepository.settings).thenReturn(Settings.DEFAULT)
        model.state.subscribe(observer)
    }

    @Test
    fun `Emit InitialState before app start`() {
        observer.assertValue(InitialState)
    }

    @Test
    fun `Start LoadingState on SubmitCredentials action`() {
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        observer.assertLastValue(LoadingBuildsState)
    }

    @Test
    fun `Display correct error on submitting unknown host`() {
        whenever(api.getBuilds()).thenError(UnknownHostException)
        model.perform(SubmitCredentials("invalid", CREDENTIALS))
        observer.assertLastValue(LoginState(error = LoginState.Error.UNKNOWN_HOST))
    }

    @Test
    fun `Display invalid credentials error on unauthorized call to teamcity api`() {
        whenever(api.getBuilds()).thenError(InvalidCredentialsException)
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, "user:wrong_pass"))
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
        whenever(api.getQueuedBuilds()).thenJust(emptyList())
        whenever(api.getProjects()).thenJust(projectList)
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        observer.assertLastValue(BuildsState(buildList, projectList,
                Settings.DEFAULT.notificationsFrequencyInMinutes))
    }

    @Test
    fun `Display queued and started builds from api`() {
        val queuedBuilds = listOf(
                createBuild(id = 700),
                createBuild(id = 701))
        val startedBuilds = listOf(
                createBuild(id = 668),
                createBuild(id = 669))
        whenever(api.getQueuedBuilds()).thenJust(queuedBuilds)
        whenever(api.getBuilds()).thenJust(startedBuilds)
        whenever(api.getProjects()).thenJust(emptyList())
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        observer.assertLastValue(BuildsState(queuedBuilds + startedBuilds, emptyList(),
                Settings.DEFAULT.notificationsFrequencyInMinutes))
    }

    @Test
    fun `Save credentials in repository on login`() {
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        verify(loginRepository).authData = AuthData(TEAMCITY_ADDRESS, CREDENTIALS)
    }

    @Test
    fun `Set api credentials on login`() {
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        verify(api).credentials = "Basic $CREDENTIALS"
    }

    @Test
    fun `Clear credentials in repository on login error`() {
        whenever(api.getBuilds()).thenError(UnknownHostException)
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        verify(loginRepository).authData = null
    }

    @Test
    fun `Clear selected projects in repository on login error`() {
        whenever(api.getBuilds()).thenError(UnknownHostException)
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        verify(buildsRepository).selectedProjects = emptyList()
    }

    @Test
    fun `Start loading if credentials are available in repository on app start`() {
        stubLoginRepositoryToReturnAuthData()
        model.perform(StartApp)
        observer.assertLastValue(LoadingBuildsState)
    }

    @Test
    fun `Display login if credentials are not available in repository on app start`() {
        whenever(loginRepository.authData).thenReturn(null)
        model.perform(StartApp)
        observer.assertLastValue(LoginState())
    }

    @Test
    fun `Set api credentials if auth data available in repository on app start`() {
        stubLoginRepositoryToReturnAuthData()
        model.perform(StartApp)
        verify(api).credentials = "Basic $CREDENTIALS"
    }

    @Test
    fun `Display builds if auth data available in repository on app start`() {
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
        observer.assertLastValue(LoadingBuildsState)
    }

    @Test
    fun `Display select projects dialog on select projects action`() {
        val allProjects = listOf(
                createProject(id = "Project1"),
                createProject(id = "Project2"))
        whenever(api.getProjects()).thenJust(allProjects)
        whenever(api.getBuilds()).thenJust(emptyList())
        whenever(api.getQueuedBuilds()).thenJust(emptyList())
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
        whenever(api.getQueuedBuilds()).thenJust(emptyList())
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
        whenever(api.getQueuedBuilds()).thenJust(emptyList())
        stubLoginRepositoryToReturnAuthData()
        whenever(buildsRepository.selectedProjects).thenReturn(selectedProjects)
        model.perform(StartApp)
        model.perform(SelectProjects)
        model.perform(SubmitProjects(selectedProjects))
        observer.assertLastValue(BuildsState(listOf(allBuilds[0], allBuilds[1]), allProjects,
                Settings.DEFAULT.notificationsFrequencyInMinutes))
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
    fun `Load build details on build selected`() {
        val build = createBuild(id = 7)
        model.perform(SelectBuild(build))
        observer.assertLastValue(LoadingDetailsState(build))
    }

    @Test
    fun `Display build details on changes and tests loaded`() {
        val build = createBuild(id = 7)
        val changes = listOf(createChange("Changes", "user"))
        val tests = listOf(createTestDetails(name = "Test 1 name"))
        whenever(api.getChanges(build.id)).thenJust(changes)
        whenever(api.getTests(build.id)).thenJust(tests)
        whenever(api.getProblemOccurrences(build.id)).thenJust(emptyList())
        model.perform(SelectBuild(build))
        observer.assertLastValue(DetailsState(build, changes, tests, emptyList()))
    }

    @Test
    fun `Display ignored tests before passed tests`() {
        val build = createBuild(id = 7)
        val passedTest = createTestDetails(status = Status.SUCCESS)
        val ignoredTest = createTestDetails(status = Status.UNKNOWN)
        whenever(api.getChanges(build.id)).thenJust(emptyList())
        whenever(api.getTests(build.id)).thenJust(listOf(passedTest, ignoredTest))
        whenever(api.getProblemOccurrences(build.id)).thenJust(emptyList())
        model.perform(SelectBuild(build))
        observer.assertLastValue(DetailsState(build, emptyList(), listOf(ignoredTest, passedTest), emptyList()))
    }

    @Test
    fun `Display failed tests before ignored tests`() {
        val build = createBuild(id = 7)
        val ignoredTest = createTestDetails(status = Status.UNKNOWN)
        val failedTest = createTestDetails(status = Status.FAILURE)
        whenever(api.getChanges(build.id)).thenJust(emptyList())
        whenever(api.getTests(build.id)).thenJust(listOf(ignoredTest, failedTest))
        whenever(api.getProblemOccurrences(build.id)).thenJust(emptyList())
        model.perform(SelectBuild(build))
        observer.assertLastValue(DetailsState(build, emptyList(), listOf(failedTest, ignoredTest), emptyList()))
    }

    @Test
    fun `Display problems in failed build details`() {
        val build = createBuild(id = 8, status = Status.FAILURE)
        val problem = createProblemOccurrence(details = "Task :app:build failed")
        whenever(api.getChanges(build.id)).thenJust(emptyList())
        whenever(api.getTests(build.id)).thenJust(emptyList())
        whenever(api.getProblemOccurrences(build.id)).thenJust(listOf(problem))
        model.perform(SelectBuild(build))
        observer.assertLastValue(DetailsState(build, emptyList(), emptyList(), listOf(problem)))
    }

    @Test
    fun `Do not call api for problem occurrences in not failing build details`() {
        val build = createBuild(id = 10, status = Status.SUCCESS)
        whenever(api.getChanges(build.id)).thenJust(emptyList())
        whenever(api.getTests(build.id)).thenJust(emptyList())
        model.perform(SelectBuild(build))
        verify(api, never()).getProblemOccurrences(build.id)
    }

    @Test
    fun `Display url in web browser on open in web browser action`() {
        val build = createBuild(webUrl = "http://teamcity/buildUrl")
        model.perform(SelectBuild(build))
        model.perform(OpenInWebBrowser)
        observer.assertLastValue(WebBrowserState(build))
    }

    @Test
    fun `Load builds list on return to list`() {
        stubLoginRepositoryToReturnAuthData()
        model.perform(ReturnToList)
        observer.assertLastValue(LoadingBuildsState)
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
    fun `Restore default settings on logout`() {
        model.perform(Logout)
        verify(settingsRepository).settings = Settings.DEFAULT
    }

    @Test
    fun `Display login without error on login error accepted`() {
        whenever(api.getBuilds()).thenError(UnknownHostException)
        model.perform(SubmitCredentials(TEAMCITY_ADDRESS, CREDENTIALS))
        model.perform(AcceptLoginError)
        observer.assertLastValue(LoginState(error = null))
    }

    @Test
    fun `Display settings on open settings action`() {
        model.perform(OpenSettings)
        observer.assertLastValue(SettingsState(Settings.DEFAULT))
    }

    @Test
    fun `Display settings saved in repository`() {
        whenever(settingsRepository.settings).thenReturn(Settings(
                notificationsFrequencyInMinutes = 15))
        model.perform(OpenSettings)
        observer.assertLastValue(SettingsState(Settings(
                notificationsFrequencyInMinutes = 15)))
    }

    @Test
    fun `Update recap duration on return to list`() {
        whenever(settingsRepository.settings).thenReturn(Settings(
                notificationsFrequencyInMinutes = 60))
        whenever(api.getBuilds()).thenJust(emptyList())
        whenever(api.getQueuedBuilds()).thenJust(emptyList())
        whenever(api.getProjects()).thenJust(emptyList())
        stubLoginRepositoryToReturnAuthData()
        model.perform(ReturnToList)
        observer.assertLastValue(BuildsState(emptyList(), emptyList(), recapDurationInMinutes = 60))
    }

    private fun stubLoginRepositoryToReturnAuthData() {
        whenever(loginRepository.authData).thenReturn(AuthData(TEAMCITY_ADDRESS, CREDENTIALS))
    }

    companion object {
        const val TEAMCITY_ADDRESS = "http://teamcity:8111"
        const val CREDENTIALS = "user:pass"
    }
}