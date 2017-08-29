package pl.elpassion.eltc.settings

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.moshiadapter.moshiConverterAdapter

class SettingsRepositoryImpl(private val application: Application) : SettingsRepository {

    private val repository = createSharedPrefs<Settings>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, moshiConverterAdapter())

    override var settings: Settings?
        get() = repository.read(SETTINGS_KEY)
        set(value) {
            repository.write(SETTINGS_KEY, value)
        }

    companion object {
        private const val SETTINGS_KEY = "settings"
    }
}