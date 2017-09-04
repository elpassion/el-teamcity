package pl.elpassion.eltc.settings

import android.os.Bundle
import android.support.v7.preference.ListPreference
import pl.elpassion.eltc.*

class SettingsFragment : BasePreferenceFragmentCompat() {

    private val notificationsFreqPreference
        get() = findPreference(NOTIFICATIONS_FREQUENCY_KEY) as ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        preferenceManager.preferenceDataStore = DI.provideSettingsDataStore()
        initListeners()
        initModel()
    }

    override fun showState(state: AppState?) {
        if (state is SettingsState) showSettings(state.settings)
    }

    private fun showSettings(settings: Settings) {
        notificationsFreqPreference.summary =
                getNotificationsFreqEntry(settings.notificationsFrequencyInMinutes)
        notificationsFreqPreference.value = settings.notificationsFrequencyInMinutes.toString()
    }

    private fun getNotificationsFreqEntry(value: Int) =
            getEntry(notificationsFreqPreference, value)

    private fun getEntry(preference: ListPreference, value: Int): CharSequence {
        val index = preference.entryValues.indexOf(value.toString())
        return preference.entries[index]
    }

    private fun initListeners() {
        notificationsFreqPreference.setOnPreferenceChangeListener { _, _ ->
            model.perform(RefreshSettings); true
        }
    }

    companion object {
        const val NOTIFICATIONS_FREQUENCY_KEY = "notifications_frequency"
    }
}