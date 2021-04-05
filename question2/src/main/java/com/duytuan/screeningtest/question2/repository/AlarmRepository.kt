package com.duytuan.screeningtest.question2.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.duytuan.screeningtest.core.model.TResult
import com.duytuan.screeningtest.question2.model.Alarm
import com.duytuan.screeningtest.question2.repository.local.AlarmDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val alarmDao: AlarmDao) {

    fun getAlarms(pageSize: Int = 10): LiveData<PagedList<Alarm>> =
        alarmDao.getPaging().toLiveData(pageSize)

    fun getAllEnabledAlarms(): Flow<List<Alarm>> = flow {
        emit(alarmDao.getAllEnabled())
    }.flowOn(Dispatchers.IO)

    suspend fun updateAlarms(vararg alarms: Alarm): Flow<TResult<Boolean>> = flow {
        alarmDao.update(*alarms)
        emit(TResult.Success(true))
    }.flowOn(Dispatchers.IO)

    suspend fun deleteAlarms(vararg alarms: Alarm): Flow<TResult<Boolean>> = flow {
        alarmDao.delete(*alarms)
        emit(TResult.Success(true))
    }.flowOn(Dispatchers.IO)

    suspend fun addAlarms(vararg alarms: Alarm): Flow<TResult<Boolean>> = flow {
        alarmDao.insert(*alarms)
        emit(TResult.Success(true))
    }.flowOn(Dispatchers.IO)

}