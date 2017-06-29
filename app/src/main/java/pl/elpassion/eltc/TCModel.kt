package pl.elpassion.eltc

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TCModel(private val api: TCApi) {

    private val stateSubject = BehaviorSubject.createDefault<AppState>(NoCredentials)
    val state: Observable<AppState> = stateSubject

    fun perform(action: UserAction) {
        val onNext: (List<String>) -> Unit = { list ->
            stateSubject.onNext(Builds(list))
        }
        val onError: (Throwable) -> Unit = { error ->
            if (error is TCApiException) {
                stateSubject.onNext(error.toState())
            } else {
                stateSubject.onError(error)
            }
        }
        api.getBuilds().subscribe(onNext, onError)
    }

    private fun TCApiException.toState() = when (this) {
        is UnknownHostException -> UnknownHost
        is InvalidCredentialsException -> InvalidCredentials
    }
}