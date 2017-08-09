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

    override fun showFailureNotification(failedBuilds: List<Build>) {
        notify("${failedBuilds.count()} new builds failed.")
    }

    private fun notify(message: String) {
        val resultIntent = PendingIntent.getActivity(application,
                0, Intent(application, BuildsActivity::class.java), 0)
        val notification = NotificationCompat.Builder(application)
                .setSmallIcon(R.drawable.ic_failure_recap)
                .setColor(ContextCompat.getColor(application, R.color.failure))
                .setContentTitle(application.getString(R.string.teamcity_recap))
                .setContentText(message)
                .setContentIntent(resultIntent)
                .setAutoCancel(true)
                .build()
        val notificationManager = application
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}