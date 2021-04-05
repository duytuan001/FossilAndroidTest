package com.duytuan.screeningtest.question2.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.duytuan.screeningtest.question2.model.Alarm

@Database(entities = [Alarm::class], version = 1)
@TypeConverters(AlarmConverters::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}