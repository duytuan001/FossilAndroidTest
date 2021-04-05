package com.duytuan.screeningtest.question2.receiver

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.duytuan.screeningtest.core.util.NotificationUtil
import com.duytuan.screeningtest.question2.R
import com.duytuan.screeningtest.question2.model.Alarm
import com.duytuan.screeningtest.question2.util.AlarmUtil
import com.duytuan.screeningtest.question2.util.Constants

class AlarmReceiver : BroadcastReceiver() {
    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        val alarm: Alarm = intent.getBundleExtra(Constants.KEY_BUNDLE_ALARM)?.getParcelable(Constants.KEY_ALARM)
            ?: return

        Log.d(Constants.LOG_TAG, alarm.toString())
        val id = alarm.getNotificationId()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = context.getString(R.string.app_name)
        val label = alarm.label ?: context.getString(R.string.alarm_title)
        val soundUri = try {
            alarm.soundUri?.toUri()
        } catch (e: Exception) {
            null
        }

        val builder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_alarm_white_24dp)
            color = ContextCompat.getColor(context, R.color.orange)
            setContentTitle(context.getString(R.string.app_name))
            setContentText(label)
            setTicker(label)
            priority = NotificationCompat.PRIORITY_HIGH
            if (alarm.isVibrationEnabled) {
                setVibrate(NotificationUtil.getVibrationPatten())
            }
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setSound(soundUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))

            setContentIntent(AlarmUtil.getLaunchPendingIntent(context, alarm))
            setAutoCancel(true)
        }

        NotificationUtil.createNotificationChannel(context, channelId)
        manager.notify(id, builder.build().apply {
            flags = flags or Notification.FLAG_INSISTENT
        })

        // Reset Alarm manually
        AlarmUtil.setReminderAlarm(context, alarm)
    }
}