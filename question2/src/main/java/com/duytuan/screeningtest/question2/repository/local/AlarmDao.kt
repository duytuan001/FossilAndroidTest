package com.duytuan.screeningtest.question2.repository.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.duytuan.screeningtest.question2.base.BaseDao
import com.duytuan.screeningtest.question2.model.Alarm

@Dao
interface AlarmDao : BaseDao<Alarm> {

    @Query("SELECT * FROM alarm ORDER BY id DESC")
    fun getPaging(): DataSource.Factory<Int, Alarm>

    @Query("SELECT * FROM alarm WHERE isEnabled")
    fun getAllEnabled(): List<Alarm>
}