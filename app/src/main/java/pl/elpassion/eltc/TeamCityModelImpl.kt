package pl.elpassion.eltc

import io.reactivex.Observable
import io.reactivex.rxkotlin.Singles
import io.reactivex.subjects.BehaviorSubject
import pl.elpassion.eltc.api.*
import pl.elpassion.eltc.builds.BuildsRepository
import pl.elpassion.eltc.builds.SelectableProject
import pl.elpassion.eltc.login.AuthData
import pl.elpassion.eltc.login.LoginRepository

class TeamCityModelImpl(private val api: TeamCityApi,
                        private val loginRepository: LoginRepository,
                        private val buildsRepository: BuildsRepository) : TeamCityModel {

    private val stateSubject = BehaviorSubject.createDefault<AppState>(InitialState)
    override val state: Observable<AppState> = stateSubject

    private fun goTo(state: AppState) = stateSubject.onNext(state)

    override fun perform(action: UserAction) = when (action) {
        is StartApp -> startApp()
        is SubmitCredentials -> submitCredentials(action)
        is AcceptLoginError -> goTo(LoginState())
        is RefreshList -> loadBuilds()
        is SelectProjects -> selectProjects()
        is SubmitProjects -> submitProjects(action.projects)
        is SelectBuild -> loadDetails(action.build)
        is ReturnToList -> loadBuilds()
        is OpenInWebBrowser -> openWebBrowser()
        is Logout -> logout()
    }

    private fun startApp() {
        loginRepository.authData?.let { setupApi(it) }
        loadBuilds()
    }

    private fun submitCredentials(action: SubmitCredentials) = with(action) {
        val authData = AuthData(address, credentials)
        setupApi(authData)
        loginRepository.authData = authData
        getBuildsAndProjects()
    }

    private fun setupApi(authData: AuthData) = with(authData) {
        api.credentials = fullCredentials
    }

    private fun loadBuilds() {
        val authData = loginRepository.authData
        if (authData != null) {
            getBuildsAndProjects()
        } else {
            goTo(LoginState())
        }
    }

    private fun getBuildsAndProjects() {
        goTo(LoadingBuildsState)
        Singles.zip(getAllBuilds(), api.getProjects())
                .subscribe(onBuildsAndProjects, onError)
    }

    private val onBuildsAndProjects: (Pair<List<Build>, List<Project>>) -> Unit =
            { (builds, projects) ->
                goTo(BuildsState(builds, projects))
            }

    private val onError: (Throwable) -> Unit = { error ->
        clearRepository()
        if (error is TeamCityApiException) {
            goTo(error.toState())
        } else {
            stateSubject.onError(error)
        }
    }

    private fun getAllBuilds() = Singles.zip(
            api.getQueuedBuilds(),
            getStartedBuilds(),
            zipper = { queued, finished -> queued + finished })

    private fun getStartedBuilds() =
            if (isAnyProjectSelected()) api.getBuildsForProjects(getSelectedProjectsIds())
            else api.getBuilds()

    private fun isAnyProjectSelected() = buildsRepository.selectedProjects.isNotEmpty()

    private fun getSelectedProjectsIds() = buildsRepository.selectedProjects.map { it.id }

    private fun selectProjects() {
        state.firstElement().subscribe {
            if (it is BuildsState) {
                val selectedProjects = buildsRepository.selectedProjects
                goTo(SelectProjectsDialogState(it.projects.toSelectable(selectedProjects)))
            }
        }
    }

    private fun List<Project>.toSelectable(selectedProjects: List<Project>) = map {
        SelectableProject(it, isSelected = selectedProjects.contains(it))
    }

    private fun submitProjects(projects: List<Project>) {
        buildsRepository.selectedProjects = projects
        loadBuilds()
    }

    private fun loadDetails(build: Build) {
        goTo(LoadingDetailsState(build))
        Singles.zip(
                api.getChanges(build.id),
                api.getTests(build.id)) { changes, tests -> changes to tests }
                .subscribe(onBuildDetails, onError)
    }

    private val onBuildDetails: (Pair<List<Change>, List<TestDetails>>) -> Unit = { (changes, tests) ->
        state.firstElement().subscribe {
            if (it is LoadingDetailsState) {
                goTo(DetailsState(it.build, changes, tests.sorted()))
            }
        }
    }

    private fun openWebBrowser() {
        state.firstElement().subscribe {
            if (it is WithBuild) {
                goTo(WebBrowserState(it.build))
            }
        }
    }

    private fun logout() {
        clearRepository()
        goTo(LoginState())
    }

    private fun clearRepository() {
        loginRepository.authData = null
        buildsRepository.selectedProjects = emptyList()
    }

    private fun TeamCityApiException.toState() = LoginState(error = when (this) {
        is UnknownHostException -> LoginState.Error.UNKNOWN_HOST
        is InvalidCredentialsException -> LoginState.Error.INVALID_CREDENTIALS
        is NetworkTimeoutException -> LoginState.Error.NETWORK_PROBLEM
    })
}