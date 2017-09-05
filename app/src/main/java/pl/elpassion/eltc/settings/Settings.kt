package pl.elpassion.eltc.settings

data class Settings(val areNotificationsEnabled: Boolean,
                    val notificationsFrequencyInMinutes: Int) {

    companion object {
        val DEFAULT = Settings(
                areNotificationsEnabled = true,
                notificationsFrequencyInMinutes = 15)
    }
}