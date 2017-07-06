package pl.elpassion.eltc

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs

class RepositoryImpl(private val application: Application) : Repository {

    private val repository = createSharedPrefs<AuthData?>({
        PreferenceManager.getDefaultSharedPreferences(application)
    })

    override var authData: AuthData?
        get() = repository.read(AUTH_DATA_KEY)
        set(value) {
            repository.write(AUTH_DATA_KEY, value)
        }

    companion object {
        private const val AUTH_DATA_KEY = "auth_data"
    }
}