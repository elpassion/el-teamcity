package pl.elpassion.eltc

import io.reactivex.Single

interface TCApi {
    fun getBuildList(): Single<List<String>>
}