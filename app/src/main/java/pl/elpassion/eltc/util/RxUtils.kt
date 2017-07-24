package pl.elpassion.eltc.util

import android.util.Log
import io.reactivex.Single
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

fun setupRxJavaErrorHandler() {
    RxJavaPlugins.setErrorHandler { throwable ->
        if (throwable is UndeliverableException) {
            Log.d("RxJavaPlugins", "error handler got: $throwable")
        } else throw throwable
    }
}

@Suppress("UNCHECKED_CAST")
fun <T, R : Comparable<R>> zipSingles(singles: List<Single<List<T>>>, sortDescBy: (T) -> R): Single<List<T>> =
        Single.zip(singles, {
            it.map { it as List<T> }.flatten().sortedByDescending { sortDescBy(it) }
        })