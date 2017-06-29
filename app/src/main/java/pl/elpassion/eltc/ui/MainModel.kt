package pl.elpassion.eltc.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import io.reactivex.disposables.CompositeDisposable
import pl.elpassion.eltc.*


class MainModel(application: Application) : AndroidViewModel(application) {

    private val model = TeamCityModel(TeamCityApiImpl, RepositoryImpl(application))

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