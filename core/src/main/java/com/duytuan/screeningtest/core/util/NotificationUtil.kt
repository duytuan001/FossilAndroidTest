package com.duytuan.screeningtest.core.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationUtil {
    companion object {
        fun createNotificationChannel(
            context: Context,
            channelId: String
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mgr: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (mgr.getNotificationChannel(channelId) == null) {
                    val channel = NotificationChannel(
                        channelId,
                        channelId,
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        enableVibration(true)
                        vibrationPattern = getVibrationPatten()
                        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                        setBypassDnd(true)
                    }

                    mgr.createNotificationChannel(channel)
                }
            }
        }

        fun getVibrationPatten() = longArrayOf(1000, 500, 1000, 500, 1000, 500)
    }
}