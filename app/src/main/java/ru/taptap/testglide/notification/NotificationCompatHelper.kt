package ru.taptap.testglide.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationCompatHelper( //todo вынестив в di
    private val context: Context,
    private val channel: NotificationChannel? = null
) { // TODO переделай все на interface взаимодействие

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (channel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNotificationBuilder(): Notification.Builder {
        return if (channel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channel.id)
        } else {
            Notification.Builder(context)
        }
    }

}