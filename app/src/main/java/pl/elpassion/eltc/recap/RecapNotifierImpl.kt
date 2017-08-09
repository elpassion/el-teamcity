package pl.elpassion.eltc.recap

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import pl.elpassion.eltc.Build
import pl.elpassion.eltc.R
import pl.elpassion.eltc.builds.BuildsActivity

class RecapNotifierImpl(private val application: Application) : RecapNotifier {

    private val notificationManager
        get() = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun showFailureNotifications(failedBuilds: List<Build>) {
        failedBuilds.forEach { notify(it) }
    }

    private fun notify(build: Build) {
        val resultIntent = getResultIntent()
        val message = "Build #${build.number} failed."
        val notification = application.createNotification(message, resultIntent)
        notificationManager.notify(1, notification)
    }

    private fun getResultIntent() = PendingIntent.getActivity(
            application, 0, Intent(application, BuildsActivity::class.java), 0)

    private fun Context.createNotification(message: String, resultIntent: PendingIntent?) =
            NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_failure_recap)
                    .setColor(ContextCompat.getColor(this, R.color.failure))
                    .setContentTitle(getString(R.string.teamcity_recap))
                    .setContentText(message)
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .build()
}