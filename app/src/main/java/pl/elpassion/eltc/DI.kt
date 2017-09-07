package pl.elpassion.eltc

import android.app.Application
import android.support.v7.preference.PreferenceDataStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.elpassion.eltc.api.TeamCityApiImpl
import pl.elpassion.eltc.builds.BuildsRepositoryImpl
import pl.elpassion.eltc.login.LoginRepositoryImpl
import pl.elpassion.eltc.recap.RecapNotifier
import pl.elpassion.eltc.recap.RecapNotifierImpl
import pl.elpassion.eltc.recap.RecapRepository
import pl.elpassion.eltc.recap.RecapRepositoryImpl
import pl.elpassion.eltc.settings.SettingsRepositoryImpl
import pl.elpassion.eltc.util.SchedulersSupplier

object DI {

    private val model by lazy {
        TeamCityModelImpl(
                provideTeamCityApi(),
                provideLoginRepository(),
                provideBuildsRepository(),
                provideSettingsRepository(),
                SchedulersSupplier(
                        subscribeOn = Schedulers.io(),
                        observeOn = AndroidSchedulers.mainThread()))
    }

    var provideTeamCityModel: () -> TeamCityModel = { model }

    var provideTeamCityApi = { TeamCityApiImpl(provideLoginRepository()) }

    var provideLoginRepository = { LoginRepositoryImpl(provideApplication()) }

    var provideBuildsRepository = { BuildsRepositoryImpl(provideApplication()) }

    var provideSettingsRepository = { SettingsRepositoryImpl(provideApplication()) }

    var provideSettingsDataStore: () -> PreferenceDataStore = { SettingsRepositoryImpl(provideApplication()) }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    object Recap {

        var provideRepository: () -> RecapRepository = { RecapRepositoryImpl(provideApplication()) }

        var provideNotifier: () -> RecapNotifier = { RecapNotifierImpl(provideApplication()) }
    }
}