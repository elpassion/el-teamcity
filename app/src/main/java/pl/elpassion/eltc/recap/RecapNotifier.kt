package pl.elpassion.eltc.recap

import pl.elpassion.eltc.Build

interface RecapNotifier {
    fun showFailureNotification(failedBuilds: List<Build>)
}