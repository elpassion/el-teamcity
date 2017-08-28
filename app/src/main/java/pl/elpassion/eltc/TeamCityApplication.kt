package pl.elpassion.eltc

import android.app.Application
import pl.elpassion.eltc.util.setupRxJavaErrorHandler

class TeamCityApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DI.provideApplication = { this }
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            DI.Recap.provideNotifier().createRecapChannel()
        }
    }

    companion object {
        init {
            setupRxJavaErrorHandler()
        }
    }
}