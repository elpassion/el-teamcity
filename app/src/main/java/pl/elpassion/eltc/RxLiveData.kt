package pl.elpassion.eltc

import android.arch.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


class RxLiveData<T>(private val observable: Observable<T>) : LiveData<T>() {

    val disposable = CompositeDisposable()

    override fun onActive() {
        observable
                .subscribe({ postValue(it) }, { throw it })
                .let { disposable.add(it) }
    }

    override fun onInactive() {
        disposable.clear()
    }
}

