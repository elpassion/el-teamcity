package pl.elpassion.eltc

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TCModel {

    private val stateSubject = BehaviorSubject.createDefault<AppState>(NoCredentials)
    val state: Observable<AppState> = stateSubject

    fun perform(action: UserAction) = stateSubject.onNext(UnknownHost)
}