package pl.elpassion.eltc

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TeamCityModel(private val api: TeamCityApi) {

    private val stateSubject = BehaviorSubject.createDefault<AppState>(NoCredentials)
    val state: Observable<AppState> = stateSubject

    fun perform(action: UserAction) {
        if (action is SubmitCredentials) {
            performSubmitCredentials(action)
        }
    }

    private fun performSubmitCredentials(action: SubmitCredentials) {
        val onNext: (List<Build>) -> Unit = {
            stateSubject.onNext(Builds(it))
        }
        val onError: (Throwable) -> Unit = { error ->
            if (error is TeamCityApiException) {
                stateSubject.onNext(error.toState())
            } else {
                stateSubject.onError(error)
            }
        }
        api.getBuilds(action.credentials).subscribe(onNext, onError)
    }

    private fun TeamCityApiException.toState() = when (this) {
        is UnknownHostException -> UnknownHost
        is InvalidCredentialsException -> InvalidCredentials
    }
}