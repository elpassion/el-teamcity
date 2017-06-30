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

    private fun performStartApp() = stateSubject.onNext(LoadingState)

    private fun performSubmitCredentials(action: SubmitCredentials) {
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
        with(action) {
            api.getBuilds(credentials).subscribe(onNext, onError)
            repository.authData = AuthData(address, credentials)
        }
    }

    private fun TeamCityApiException.toState() = when (this) {
        is UnknownHostException -> UnknownHostState
        is InvalidCredentialsException -> InvalidCredentialsState
        is NetworkTimeoutException -> NetworkProblemState
    }
}