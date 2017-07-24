package pl.elpassion.eltc

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import pl.elpassion.eltc.api.*
import pl.elpassion.eltc.builds.BuildsRepository
import pl.elpassion.eltc.builds.SelectableProject
import pl.elpassion.eltc.login.AuthData
import pl.elpassion.eltc.login.LoginRepository
import java.util.concurrent.TimeUnit

class TeamCityModelImpl(private val api: TeamCityApi,
                        private val loginRepository: LoginRepository,
                        private val buildsRepository: BuildsRepository) : TeamCityModel {

    private val stateSubject = BehaviorSubject.createDefault<AppState>(InitialState)
    override val state: Observable<AppState> = stateSubject

    private fun goTo(state: AppState) = stateSubject.onNext(state)

    private val refreshInterval = Observable.interval(3, TimeUnit.SECONDS)

    private val refreshDisposable = CompositeDisposable()

    override fun perform(action: UserAction) {
        when (action) {
            is StartApp -> startApp()
            is SubmitCredentials -> submitCredentials(action)
            is AcceptLoginError -> goTo(LoginState())
            is RefreshList -> loadBuilds()
            is AutoRefresh -> performAutoRefresh(action.isEnabled)
            is SelectProjects -> selectProjects()
            is SubmitProjects -> submitProjects(action.projects)
            is Logout -> logout()
        }
    }

    private fun startApp() {
        loginRepository.authData?.let {
            api.setAddress(it.address)
            api.credentials = it.credentials
        }
        loadBuilds()
    }

    private fun submitCredentials(action: SubmitCredentials) = with(action) {
        val authData = AuthData(address, credentials)
        api.setAddress(address)
        api.credentials = credentials
        loginRepository.authData = authData
        getBuildsAndProjects(authData)
    }

    private fun loadBuilds() {
        val authData = loginRepository.authData
        if (authData != null) {
            getBuildsAndProjects(authData)
        } else {
            goTo(LoginState())
        }
    }

    private fun getBuildsAndProjects(authData: AuthData) {
        val onNext: (Pair<List<Build>, List<Project>>) -> Unit = { (builds, projects) ->
            goTo(BuildsState(builds, projects))
        }
        val onError: (Throwable) -> Unit = { error ->
            loginRepository.authData = null
            if (error is TeamCityApiException) {
                goTo(error.toState())
            } else {
                stateSubject.onError(error)
            }
        }
        goTo(LoadingState)
        with(authData) {
            Single.zip<List<Build>, List<Project>, Pair<List<Build>, List<Project>>>(
                    if (buildsRepository.selectedProjects.isNotEmpty()) {
                        api.getBuildsForProjects(buildsRepository.selectedProjects.map { it.id })
                    } else {
                        api.getBuilds()
                    },
                    api.getProjects(),
                    BiFunction { builds, projects ->
                        builds to projects
                    })
                    .subscribe(onNext, onError)
        }
    }

    private fun performAutoRefresh(isEnabled: Boolean) {
        refreshDisposable.clear()
        if (isEnabled) {
            refreshInterval.subscribe { perform(RefreshList) }.let { refreshDisposable.add(it) }
        }
    }

    private fun selectProjects() {
        state.firstElement().subscribe {
            (it as? BuildsState)?.let {
                val selectedProjects = buildsRepository.selectedProjects
                goTo(SelectProjectsDialogState(it.projects.map {
                    SelectableProject(it, isSelected = selectedProjects.contains(it))
                }))
            }
        }
    }

    private fun submitProjects(projects: List<Project>) {
        buildsRepository.selectedProjects = projects
        loadBuilds()
    }

    private fun logout() {
        loginRepository.authData = null
        buildsRepository.selectedProjects = emptyList()
        goTo(LoginState())
    }

    private fun TeamCityApiException.toState() = LoginState(error = when (this) {
        is UnknownHostException -> LoginState.Error.UNKNOWN_HOST
        is InvalidCredentialsException -> LoginState.Error.INVALID_CREDENTIALS
        is NetworkTimeoutException -> LoginState.Error.NETWORK_PROBLEM
    })
}