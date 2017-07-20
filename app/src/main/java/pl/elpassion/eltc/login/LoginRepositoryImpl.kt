package pl.elpassion.eltc.login

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.moshiadapter.moshiConverterAdapter

class LoginRepositoryImpl(private val application: Application) : LoginRepository {

    private val repository = createSharedPrefs<AuthData?>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, moshiConverterAdapter())

    override var authData: AuthData?
        get() = repository.read(AUTH_DATA_KEY)
        set(value) {
            repository.write(AUTH_DATA_KEY, value)
        }

    companion object {
        private const val AUTH_DATA_KEY = "auth_data"
    }
}