package pl.elpassion.eltc

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TeamCityModel(private val api: TeamCityApi,
                    private val repository: Repository) {

    private val stateSubject = BehaviorSubject.createDefault<AppState>(InitialState)
    val state: Observable<AppState> = stateSubject

    fun perform(action: UserAction) {
        when (action) {
            is StartApp -> loadBuilds()
            is SubmitCredentials -> performSubmitCredentials(action)
            is RefreshList -> loadBuilds()
        }
    }

    private fun loadBuilds() {
        val authData = repository.authData
        if (authData != null) {
            getBuilds(authData.credentials)
        } else {
            stateSubject.onNext(LoginState)
        }
    }

    private fun performSubmitCredentials(action: SubmitCredentials) = with(action) {
        repository.authData = AuthData(address, credentials)
        getBuilds(credentials)
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
        stateSubject.onNext(LoadingState)
        api.getBuilds(credentials).subscribe(onNext, onError)
    }

    private fun TeamCityApiException.toState() = when (this) {
        is UnknownHostException -> UnknownHostState
        is InvalidCredentialsException -> InvalidCredentialsState
        is NetworkTimeoutException -> NetworkProblemState
    }
}