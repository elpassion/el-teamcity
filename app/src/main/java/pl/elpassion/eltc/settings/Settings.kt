package pl.elpassion.eltc.settings

data class Settings(val notificationsFrequency: Int) {

    companion object {
        const val EVERY_15_MIN = 15
        const val EVERY_30_MIN = 30
        val DEFAULT = Settings(notificationsFrequency = EVERY_15_MIN)
    }
}