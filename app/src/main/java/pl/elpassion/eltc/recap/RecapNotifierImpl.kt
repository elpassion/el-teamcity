package pl.elpassion.eltc.recap

import pl.elpassion.eltc.Build

class RecapNotifierImpl : RecapNotifier {

    override fun showFailureNotification(failedBuilds: List<Build>) = Unit
}