package pl.elpassion.eltc.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pl.elpassion.eltc.AppState
import pl.elpassion.eltc.TCApiImpl
import pl.elpassion.eltc.TCModel
import pl.elpassion.eltc.UserAction


class MainModel : ViewModel() {

    private val tcmodel = TCModel(TCApiImpl)

    fun perform(action: UserAction) = tcmodel.perform(action)

    val state: LiveData<AppState> = object : LiveData<AppState>() {

        val disposable = CompositeDisposable()

        override fun onActive() {
            val onNext = { state: AppState -> postValue(state) }
            val onError = { exception: Throwable -> throw exception }
            disposable.add(tcmodel.state.subscribe(onNext, onError))
        }

        override fun onInactive() {
            disposable.clear()
        }
    }
}