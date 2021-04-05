package com.duytuan.screeningtest.question2.di

import android.content.Context
import androidx.room.Room
import com.duytuan.screeningtest.question2.repository.local.AlarmDao
import com.duytuan.screeningtest.question2.repository.local.AlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAlarmDatabase(
        @ApplicationContext appContext: Context
    ): AlarmDatabase {
        return Room.databaseBuilder(
            appContext,
            AlarmDatabase::class.java,
            "alarm.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(
        appDatabase: AlarmDatabase
    ): AlarmDao {
        return appDatabase.alarmDao()
    }
}