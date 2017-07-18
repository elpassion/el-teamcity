package pl.elpassion.eltc

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.moshiadapter.moshiConverterAdapter

class RepositoryImpl(private val application: Application) : Repository {

    private val authDataRepository = createSharedPrefs<AuthData?>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, moshiConverterAdapter())

    private val selectedProjectsRepository = createSharedPrefs<List<Project>>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, moshiConverterAdapter())

    override var authData: AuthData?
        get() = authDataRepository.read(AUTH_DATA_KEY)
        set(value) {
            authDataRepository.write(AUTH_DATA_KEY, value)
        }

    override var selectedProjects: List<Project>
        get() = selectedProjectsRepository.read(SELECTED_PROJECTS_KEY) ?: emptyList()
        set(value) {
            selectedProjectsRepository.write(SELECTED_PROJECTS_KEY, value)
        }

    companion object {
        private const val AUTH_DATA_KEY = "auth_data"
        private const val SELECTED_PROJECTS_KEY = "selected_projects"
    }
}