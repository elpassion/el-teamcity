package pl.elpassion.eltc.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import io.reactivex.plugins.RxJavaPlugins
import pl.elpassion.eltc.*


class MainModel(application: Application) : AndroidViewModel(application) {

    private val model = TeamCityModel(TeamCityApiImpl, RepositoryImpl(application))

    fun perform(action: UserAction) = model.perform(action)

    val state = RxLiveData(model.state)

    companion object {
        init {
            // strange but correct: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
            RxJavaPlugins.setErrorHandler {
                Log.d("RxJavaPlugins", "error handler got: $it")
            }
        }
    }

}