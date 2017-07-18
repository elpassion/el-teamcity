package pl.elpassion.eltc

import android.app.Application
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins


class TeamCityApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DI.provideApplication = { this }
    }

    companion object {
        init {
            RxJavaPlugins.setErrorHandler { throwable ->
                if (throwable is UndeliverableException) {
                    Log.d("RxJavaPlugins", "error handler got: $throwable")
                }
                else throw throwable
            }
        }
    }
}