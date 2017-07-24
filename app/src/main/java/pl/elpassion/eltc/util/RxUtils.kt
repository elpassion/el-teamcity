package pl.elpassion.eltc.util

import io.reactivex.Single

@Suppress("UNCHECKED_CAST")
fun <T, R : Comparable<R>> zipSingles(singles: List<Single<List<T>>>, sortDescBy: (T) -> R): Single<List<T>> =
        Single.zip(singles, {
            it.map { it as List<T> }.flatten().sortedByDescending { sortDescBy(it) }
        })