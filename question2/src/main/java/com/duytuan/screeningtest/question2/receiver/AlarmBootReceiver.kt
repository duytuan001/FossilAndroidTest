package com.duytuan.screeningtest.question2.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.duytuan.screeningtest.question2.repository.AlarmRepository
import com.duytuan.screeningtest.question2.util.AlarmUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Re-schedules all stored alarms. This is necessary as {@link AlarmManager} does not persist alarms
 * between reboots.
 */
@AndroidEntryPoint
class AlarmBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // fetch all enable alarms then set reminders for them
            GlobalScope.launch(Dispatchers.IO) {
                alarmRepository.getAllEnabledAlarms().collect {
                    AlarmUtil.setReminderAlarms(context, it)
                }
            }
        }
    }
}