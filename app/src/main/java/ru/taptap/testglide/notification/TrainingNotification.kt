package ru.taptap.testglide.notification


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import ru.taptap.testglide.MainActivity
import ru.taptap.testglide.R


class TrainingNotification(
    private val context: Context
) : NotificationCreator {

    override val channelId: String = "training channel"

    override val notificationCompatHelper: NotificationCompatHelper by lazy {
        val channel = getChannel()
        NotificationCompatHelper(context, channel)
    }

    override fun getChannel(): NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return NotificationChannel(
                channelId,
                context.getString(R.string.training_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.training_channel_description)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        }
        return null
    }

    override fun getBuilder(): Notification.Builder {
        val fullScreenIntent = Intent(context, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return notificationCompatHelper.getNotificationBuilder()
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setCategory(android.app.Notification.CATEGORY_REMINDER)
            .setColor(Color.MAGENTA)
            .setOnlyAlertOnce(true)
//            .setChronometerCountDown(true)
//            .setUsesChronometer(true)
//            .setContent(remoteCollapsedViews)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            //custom style

            // Add media control buttons that invoke intents in your media service
            .addAction(android.R.drawable.ic_media_previous, "Previous", fullScreenPendingIntent) // #0
            .addAction(android.R.drawable.ic_media_previous, "Hi", fullScreenPendingIntent) // #1
            .addAction(android.R.drawable.ic_media_previous, "Next", fullScreenPendingIntent) // #2
                // Apply the media style template
                .setStyle(Notification.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2 /* #1: pause button \*/)
                    /*.setMediaSession(mediaSession.getSessionToken())*/)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(remoteCollapsedViews)
//            .setCustomBigContentView(remoteExpandedViews)

    }

}