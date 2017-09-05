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

    private val currentSettings get() = settings ?: Settings.DEFAULT

    override var settings: Settings?
        get() = repository.read(SETTINGS_KEY)
        set(value) {
            repository.write(SETTINGS_KEY, value)
        }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean = when (key) {
        NOTIFICATIONS_KEY -> currentSettings.areNotificationsEnabled
        else -> throw IllegalArgumentException()
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == NOTIFICATIONS_KEY) {
            settings = currentSettings.copy(areNotificationsEnabled = value)
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        val value = when (key) {
            NOTIFICATIONS_FREQUENCY_KEY -> currentSettings.notificationsFrequencyInMinutes
            else -> throw IllegalArgumentException()
        }
        return value.toString()
    }

    override fun putString(key: String?, value: String?) {
        if (key == NOTIFICATIONS_FREQUENCY_KEY && value != null) {
            settings = currentSettings.copy(notificationsFrequencyInMinutes = value.toInt())
        }
    }

    companion object {
        private const val SETTINGS_KEY = "settings"
        private const val NOTIFICATIONS_KEY = "notifications"
        private const val NOTIFICATIONS_FREQUENCY_KEY = "notifications_frequency"
    }
}