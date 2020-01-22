package ru.taptap.testglide.notification

import android.app.Notification
import android.app.NotificationChannel

interface NotificationCreator {

    val channelId: String

    val notificationCompatHelper: NotificationCompatHelper

    fun getChannel(): NotificationChannel?

    fun getBuilder(): Notification.Builder
}