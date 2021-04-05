package com.duytuan.screeningtest.question2.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

class ReceiverUtil {
    companion object {
        fun enableReceiver(
            context: Context,
            receiverClass: Class<*>,
            isEnable: Boolean
        ) {
            val receiver = ComponentName(context, receiverClass)
            val newState = if (isEnable) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            context.packageManager.setComponentEnabledSetting(
                receiver,
                newState,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}