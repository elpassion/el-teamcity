package pl.elpassion.eltc

import android.arch.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


fun <T> Observable<T>.toLiveData() = object : LiveData<T>() {

    val disposable = CompositeDisposable()

    override fun onActive() {
        val onNext = { item: T -> postValue(item) }
        val onError = { throwable: Throwable -> throw throwable }
        subscribe(onNext, onError).addTo(disposable)
    }

    override fun onInactive() {
        disposable.clear()
    }
}

fun Disposable.addTo(disposable: CompositeDisposable) = disposable.add(this)

