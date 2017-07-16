package pl.elpassion.eltc

import android.app.Application

object DI {

    private val model by lazy { TeamCityModelImpl(provideTeamCityApi(), provideRepository()) }

    var provideTeamCityModel: () -> TeamCityModel = { model }

    var provideTeamCityApi = { TeamCityApiImpl }

    var provideRepository = { RepositoryImpl(provideApplication()) }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }
}