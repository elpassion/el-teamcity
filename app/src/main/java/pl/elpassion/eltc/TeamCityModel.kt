package pl.elpassion.eltc

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TeamCityModel(private val api: TeamCityApi,
                    private val repository: Repository) {

    private val stateSubject = BehaviorSubject.createDefault<AppState>(InitialState)
    val state: Observable<AppState> = stateSubject

    fun perform(action: UserAction) {
        when (action) {
            is StartApp -> performStartApp()
            is SubmitCredentials -> performSubmitCredentials(action)
        }
    }

    private fun performStartApp() {
        val authData = repository.authData
        if (authData != null) {
            stateSubject.onNext(LoadingState)
            getBuilds(authData.credentials)
        } else {
            stateSubject.onNext(LoginState)
        }
    }

    private fun performSubmitCredentials(action: SubmitCredentials) {
        repository.authData = AuthData(action.address, action.credentials)
        getBuilds(action.credentials)
    }

    private fun getBuilds(credentials: String) {
        val onNext: (List<Build>) -> Unit = {
            stateSubject.onNext(BuildsState(it))
        }
        val onError: (Throwable) -> Unit = { error ->
            if (error is TeamCityApiException) {
                stateSubject.onNext(error.toState())
            } else {
                stateSubject.onError(error)
            }
        }
        api.getBuilds(credentials).subscribe(onNext, onError)
    }

    private fun TeamCityApiException.toState() = when (this) {
        is UnknownHostException -> UnknownHostState
        is InvalidCredentialsException -> InvalidCredentialsState
        is NetworkTimeoutException -> NetworkProblemState
    }
}