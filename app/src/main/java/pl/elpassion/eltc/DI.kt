package pl.elpassion.eltc

import android.app.Application

object DI {

    var provideTeamCityModel = { TeamCityModel(provideTeamCityApi(), provideRepository()) }

    var provideTeamCityApi = { TeamCityApiImpl }

    var provideRepository = { RepositoryImpl(provideApplication()) }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }
}