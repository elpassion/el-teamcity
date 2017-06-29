package pl.elpassion.eltc.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pl.elpassion.eltc.AppState
import pl.elpassion.eltc.TeamCityApiImpl
import pl.elpassion.eltc.TeamCityModel
import pl.elpassion.eltc.UserAction


class MainModel : ViewModel() {

    private val model = TeamCityModel(TeamCityApiImpl)

    fun perform(action: UserAction) = model.perform(action)

    val state: LiveData<AppState> = object : LiveData<AppState>() {

        val disposable = CompositeDisposable()

        override fun onActive() {
            val onNext = { state: AppState -> postValue(state) }
            val onError = { exception: Throwable -> throw exception }
            disposable.add(model.state.subscribe(onNext, onError))
        }

        override fun onInactive() {
            disposable.clear()
        }
    }
}