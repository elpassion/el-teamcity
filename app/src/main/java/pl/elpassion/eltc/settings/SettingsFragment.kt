package pl.elpassion.eltc.settings

import android.os.Bundle
import android.preference.PreferenceFragment
import pl.elpassion.eltc.R

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }
}