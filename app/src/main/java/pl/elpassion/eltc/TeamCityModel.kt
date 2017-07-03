package pl.elpassion.eltc

import io.reactivex.Observable

interface TeamCityModel {

    val state: Observable<AppState>

    fun perform(action: UserAction)
}