package pl.elpassion.eltc.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import pl.elpassion.eltc.*


class MainModel(application: Application) : AndroidViewModel(application) {

    private val model = TeamCityModel(TeamCityApiImpl, RepositoryImpl(application))

    fun perform(action: UserAction) = model.perform(action)

    val state: LiveData<AppState> = model.state.toLiveData()
}