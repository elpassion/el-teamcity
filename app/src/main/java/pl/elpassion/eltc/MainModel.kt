package pl.elpassion.eltc

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins


class MainModel(application: Application) : AndroidViewModel(application) {

    companion object {
        init {
            // strange but correct: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
            RxJavaPlugins.setErrorHandler { throwable ->
                if (throwable is UndeliverableException) {
                    Log.d("RxJavaPlugins", "error handler got: $throwable")
                }
                else throw throwable
            }
        }
    }

    init {
        DI.provideApplication = { application }
    }

    private val model get() = DI.provideTeamCityModel()

    fun perform(action: UserAction) = model.perform(action)

    val state get() = model.state

}