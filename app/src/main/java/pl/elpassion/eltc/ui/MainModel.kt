package pl.elpassion.eltc.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pl.elpassion.eltc.AppState
import pl.elpassion.eltc.TCApiImpl
import pl.elpassion.eltc.TCModel


class MainModel : ViewModel() {

    private val tcmodel = TCModel(TCApiImpl)

    val state: LiveData<AppState> = object : LiveData<AppState>() {

        val disposable = CompositeDisposable()

        override fun onActive() {
            val onNext = { state: AppState -> setValue(state) }
            val onError = { exception: Throwable -> throw exception }
            disposable.add(tcmodel.state.subscribe(onNext, onError))
        }

        override fun onInactive() {
            disposable.clear()
        }
    }
}