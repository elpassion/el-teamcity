package pl.elpassion.eltc

import android.app.Application
import pl.elpassion.eltc.util.setupRxJavaErrorHandler

class TeamCityApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DI.provideApplication = { this }
    }

    companion object {
        init {
            setupRxJavaErrorHandler()
        }
    }
}