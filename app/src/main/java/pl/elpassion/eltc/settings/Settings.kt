package pl.elpassion.eltc.settings

data class Settings(val areNotificationsEnabled: Boolean,
                    val areNotificationsFilteredToSelectedProjects: Boolean,
                    val notificationsFrequencyInMinutes: Int) {

    companion object {
        val DEFAULT = Settings(
                areNotificationsEnabled = true,
                areNotificationsFilteredToSelectedProjects = false,
                notificationsFrequencyInMinutes = 15)
    }
}