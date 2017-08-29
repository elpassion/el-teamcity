package pl.elpassion.eltc.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import pl.elpassion.eltc.R

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == NOTIFICATIONS_FREQUENCY_KEY) {
            val preference = findPreference(key) as ListPreference
            preference.summary = preference.entry
        }
    }

    companion object {
        const val NOTIFICATIONS_FREQUENCY_KEY = "notifications_frequency"
    }
}