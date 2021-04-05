package com.duytuan.screeningtest.question2.model

import android.os.Parcelable
import android.util.SparseBooleanArray
import androidx.annotation.Keep
import androidx.core.util.containsValue
import androidx.core.util.forEach
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormatSymbols
import java.util.*

@Keep
@Parcelize
@Entity
data class Alarm(
    @PrimaryKey
    val id: Long,
    var time: Long, // second unit

    val label: String? = null,
    var isEnabled: Boolean,
    val isVibrationEnabled: Boolean,
    val soundUri: String? = null,
    val allDays: SparseBooleanArray? = null, // from sunday[1] ... sat[7]

    val bedTime: Long, // second unit
) : Parcelable {
    /**
     * return string. ex. 12:00
     */
    fun getTimeDisplay(): String {
        val minutes = String.format("%02d", getMinutes())
        val hours = String.format("%02d", getHours())
        return "$hours:$minutes"
    }

    fun getNotificationId(): Int {
        return (id % 2147483647).toInt()// (id xor (id ushr 32)).toInt() // Long to Int
    }

    fun hasDayAlarm(): Boolean {
        return allDays?.containsValue(true) == true
    }

    private fun getHours() = (time / 3600).toInt()
    private fun getMinutes() = ((time / 60) % 60).toInt()

    /**
     * return the calendar of next alarm
     */
    fun getTimeForNextAlarm(): Calendar {
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, getHours())
            set(Calendar.MINUTE, getMinutes())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        } // alarm time in today

        // find closest day which enabled
        val currentDayIndex = calendar[Calendar.DAY_OF_WEEK] // 1 -> 7
        if (allDays?.get(currentDayIndex) == true && System.currentTimeMillis() < calendar.timeInMillis) {
            return calendar // today
        }
        // else find next days
        ((currentDayIndex + 1)..(currentDayIndex + Calendar.DAY_OF_WEEK)).forEach {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val dayIndex = if (it > Calendar.SATURDAY) { // it = 8 ...
                it - Calendar.DAY_OF_WEEK
            } else {
                it
            }

            if (allDays?.get(dayIndex) == true) {
                return calendar
            }
        }
        return calendar
    }

    /**
     * return: label, Mon Tue ...
     */
    fun getDescription(): String {
        val stringBuilder = StringBuilder()

        val symbols = DateFormatSymbols().shortWeekdays
        allDays?.forEach { key, value ->
            if (key < symbols.size && value) {
                stringBuilder.append(symbols[key].replace(" ", "")).append(" ")
            }
        }

        label?.let {
            if (stringBuilder.isNotEmpty() && it.isNotEmpty()) {
                stringBuilder.insert(0, ", ")
            }
            stringBuilder.insert(0, it)
        }

        return stringBuilder.toString()
    }
}