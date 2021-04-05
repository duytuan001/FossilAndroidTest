package com.duytuan.screeningtest.question2.util

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.duytuan.screeningtest.question2.model.Alarm
import com.duytuan.screeningtest.question2.receiver.AlarmReceiver
import com.duytuan.screeningtest.question2.ui.home.AlarmActivity


class AlarmUtil {
    companion object {
        fun setReminderAlarm(context: Context, alarm: Alarm) {

            // Check whether the alarm is set to run on any days or disable
            if (!alarm.isEnabled || !alarm.hasDayAlarm()) {
                // If alarm not set to run on any days, cancel any existing notifications for this alarm
                cancelReminderAlarm(context, alarm)
                return
            }

            val reminderDateTime = alarm.getTimeForNextAlarm()
            Log.d(Constants.LOG_TAG, "create alarm: $alarm $reminderDateTime")

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(Constants.KEY_BUNDLE_ALARM, Bundle().apply {
                    putParcelable(Constants.KEY_ALARM, alarm)
                })
            }
            val pIntent = PendingIntent.getBroadcast(
                    context,
                    alarm.getNotificationId(),
                    intent,
                    FLAG_UPDATE_CURRENT
            )

            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setAlarmClock(AlarmClockInfo(reminderDateTime.timeInMillis, getLaunchPendingIntent(context, alarm)), pIntent)
//            am.setExactAndAllowWhileIdle()
//            am.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                alarm.time,
//                1000 * 60 * 20,
//                pIntent
//            )
        }

        fun cancelReminderAlarm(context: Context, alarm: Alarm) {
            Log.d(Constants.LOG_TAG, "cancel alarm: $alarm")
            val intent = Intent(context, AlarmReceiver::class.java)
            val pIntent = PendingIntent.getBroadcast(
                    context,
                    alarm.getNotificationId(),
                    intent,
                    FLAG_UPDATE_CURRENT
            )
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(pIntent)
        }

        fun setReminderAlarms(context: Context, alarms: List<Alarm>) {
            for (alarm in alarms) {
                setReminderAlarm(context, alarm)
            }
        }

        fun getLaunchPendingIntent(ctx: Context, alarm: Alarm): PendingIntent? {
            return PendingIntent.getActivity(
                    ctx, alarm.getNotificationId(), getLaunchIntent(ctx), FLAG_UPDATE_CURRENT
            )
        }

        private fun getLaunchIntent(context: Context?): Intent {
            return Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}