package pl.elpassion.eltc

import io.reactivex.Single

interface TCApi {
    fun getBuilds(): Single<List<String>>
}