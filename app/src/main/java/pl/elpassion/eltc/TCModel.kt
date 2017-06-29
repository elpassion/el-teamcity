package pl.elpassion.eltc

import io.reactivex.Observable

class TCModel {

    val state: Observable<AppState> = Observable.just(NoCredentials)

}