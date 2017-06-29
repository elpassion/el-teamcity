package pl.elpassion.eltc

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson

class RepositoryImpl(private val application: Application) : Repository {

    private val repository = createSharedPrefs<String?>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, { Gson() })

    override var address: String?
        get() = repository.read(ADDRESS_KEY)
        set(value) {
            repository.write(ADDRESS_KEY, value)
        }

    override var credentials: String?
        get() = repository.read(CREDENTIALS_KEY)
        set(value) {
            repository.write(CREDENTIALS_KEY, value)
        }

    companion object {
        private const val ADDRESS_KEY = "address"
        private const val CREDENTIALS_KEY = "credentials"
    }
}