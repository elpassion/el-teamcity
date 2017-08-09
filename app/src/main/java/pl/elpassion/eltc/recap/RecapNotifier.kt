package pl.elpassion.eltc.recap

import pl.elpassion.eltc.Build

interface RecapNotifier {
    fun showFailureNotifications(failedBuilds: List<Build>)
}