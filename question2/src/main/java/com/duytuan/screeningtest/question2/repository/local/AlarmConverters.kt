package com.duytuan.screeningtest.question2.repository.local

import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.room.TypeConverter

class AlarmConverters {
    @TypeConverter
    fun fromSparseBooleanArray(value: SparseBooleanArray?): String {
        val stringBuilder = StringBuilder()
        value?.forEach { k, v -> if (v) stringBuilder.append(k).append(",") }
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.setLength(stringBuilder.length - 1)
        }
        return stringBuilder.toString()
    }

    @TypeConverter
    fun toSparseBooleanArray(value: String?): SparseBooleanArray {
        val array = SparseBooleanArray()
        try {
            value?.split(",")?.forEach {
                array.append(it.toInt(), true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return array
    }
}