package pl.elpassion.eltc.util

import android.app.NotificationManager
import android.content.Context

inline val Context.notificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager