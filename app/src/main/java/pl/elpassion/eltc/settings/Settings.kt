package pl.elpassion.eltc.settings

data class Settings(val notificationsFrequencyInMinutes: Int) {

    companion object {
        val DEFAULT = Settings(notificationsFrequencyInMinutes = 15)
    }
}