package pl.elpassion.eltc

import android.app.Application


class TeamCityApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DI.provideApplication = { this }
    }
}