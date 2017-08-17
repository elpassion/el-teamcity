package pl.elpassion.eltc.recap

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import pl.elpassion.eltc.Build
import pl.elpassion.eltc.R
import pl.elpassion.eltc.builds.BuildsActivity
import pl.elpassion.eltc.util.notificationManager

class RecapNotifierImpl(private val application: Application) : RecapNotifier {

    private val notificationManager get() = application.notificationManager

    override fun showFailureNotifications(failedBuilds: List<Build>) {
        failedBuilds.forEach { notify(it) }
        if (failedBuilds.count() > 1) {
            notificationManager.notify(0, application.createGroupSummary())
        }
    }

    private fun notify(build: Build) {
        val title = "Build #${build.number} in ${build.buildType.projectName} failed"
        val text = build.statusText
        val notification = application.createNotification(title, text, getResultIntent(build))
        notificationManager.notify(build.id, notification)
    }

    private fun getResultIntent(build: Build): PendingIntent? {
        val intent = Intent(application, BuildsActivity::class.java).apply {
            putExtra(BuildsActivity.BUILD_KEY, build)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getActivity(application, 0, intent, flags)
    }

    private fun Context.createNotification(title: String, text: String?, intent: PendingIntent?) =
            NotificationCompat.Builder(this, RECAP_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_failure_recap)
                    .setColor(ContextCompat.getColor(this, R.color.failure))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(intent)
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_STATUS)
                    .setGroup(FAILURES_GROUP_KEY)
                    .build()

    private fun Application.createGroupSummary() =
            NotificationCompat.Builder(this, RECAP_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_failure_recap)
                    .setColor(ContextCompat.getColor(this, R.color.failure))
                    .setCategory(Notification.CATEGORY_STATUS)
                    .setGroup(FAILURES_GROUP_KEY)
                    .setGroupSummary(true)
                    .build()

    companion object {
        const val RECAP_CHANNEL_ID = "recap_channel_id"
        const val FAILURES_GROUP_KEY = "failures_group_key"
    }
}