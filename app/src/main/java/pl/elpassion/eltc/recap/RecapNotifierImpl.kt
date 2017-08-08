package pl.elpassion.eltc.recap

import android.app.Application
import android.widget.Toast
import pl.elpassion.eltc.Build

class RecapNotifierImpl(private val application: Application) : RecapNotifier {

    override fun showFailureNotification(failedBuilds: List<Build>) {
        //TODO: post notifications in status bar
        val message = "Failed ${failedBuilds.count()} builds!"
        Toast.makeText(application, message, Toast.LENGTH_LONG).show()
    }
}