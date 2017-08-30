package pl.elpassion.eltc.settings

import android.app.Application
import android.preference.PreferenceManager
import android.support.v7.preference.PreferenceDataStore
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.moshiadapter.moshiConverterAdapter

class SettingsRepositoryImpl(private val application: Application) : PreferenceDataStore(), SettingsRepository {

    private val repository = createSharedPrefs<Settings>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, moshiConverterAdapter())

    override var settings: Settings?
        get() = repository.read(SETTINGS_KEY)
        set(value) {
            repository.write(SETTINGS_KEY, value)
        }

    override fun getString(key: String?, defValue: String?): String? {
        val value = when (key) {
            NOTIFICATIONS_FREQUENCY_KEY -> {
                settings?.notificationsFrequency ?: Settings.DEFAULT.notificationsFrequency
            }
            else -> throw IllegalArgumentException()
        }
        return value.toString()
    }

    override fun putString(key: String?, value: String?) {
        when (key) {
            NOTIFICATIONS_FREQUENCY_KEY -> {
                if (value != null) {
                    settings = (settings ?: Settings.DEFAULT).copy(notificationsFrequency = value.toInt())
                }
            }
        }
    }

    companion object {
        private const val SETTINGS_KEY = "settings"
        private const val NOTIFICATIONS_FREQUENCY_KEY = "notifications_frequency"
    }
}