package com.duytuan.screeningtest.question2.ui.detail

import android.net.Uri
import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.core.util.set
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.duytuan.screeningtest.core.base.BaseViewModel
import com.duytuan.screeningtest.core.model.TResult
import com.duytuan.screeningtest.question2.model.Alarm
import com.duytuan.screeningtest.question2.model.AlarmAction
import com.duytuan.screeningtest.question2.repository.AlarmRepository
import com.duytuan.screeningtest.question2.util.ActionLiveData
import com.duytuan.screeningtest.question2.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AlarmDetailViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), LifecycleObserver {

    var alarm: Alarm? = null
    var isCreateMode: Boolean = true

    val actionListener by lazy { ActionLiveData<Pair<AlarmAction, TResult<Boolean>>>() }
    val bedTime = MutableLiveData<LocalTime>()
    val wakeTime = MutableLiveData<LocalTime>()
    val label = MutableLiveData<String>()
    val soundUri = MutableLiveData<String?>(null)
    val isVibrationEnabled = MutableLiveData(true)
    val weekDays = MutableLiveData<SparseBooleanArray>()

    init {
        // set data from intent
        savedStateHandle.get<Alarm>(Constants.KEY_ALARM)?.let {
            alarm = it
            isCreateMode = false

            bedTime.value = LocalTime.ofSecondOfDay(it.bedTime)
            wakeTime.value = LocalTime.ofSecondOfDay(it.time)
            soundUri.value = it.soundUri
            isVibrationEnabled.value = it.isVibrationEnabled
            label.value = it.label ?: ""
            weekDays.value = initWeekDays(it.allDays)
        } ?: run {
            isCreateMode = true

            //default
            bedTime.value = LocalTime.of(22, 0)
            wakeTime.value = LocalTime.of(7, 0)
            soundUri.value = null
            isVibrationEnabled.value = true
            label.value = ""
            weekDays.value = initWeekDays()
        }
    }

    fun setBedTime(time: LocalTime) {
        bedTime.value = time
    }

    fun setWakeTime(time: LocalTime) {
        wakeTime.value = time
    }

    fun setVibrationEnable(isEnabled: Boolean) {
        isVibrationEnabled.value = isEnabled
    }

    fun setSoundUri(uri: Uri) {
        soundUri.value = uri.toString()
    }

    fun updateOrCreateAlarm() {
        if (isCreateMode) {
            createAlarm()
        } else {
            updateAlarm()
        }
    }

    fun setSelectedDay(dayIndex: Int, isSelected: Boolean) {
        weekDays.value?.let { it[dayIndex] = isSelected }
    }

    private fun createAlarm() {
        viewModelScope.launch {
            alarm = generateAlarm()
            alarmRepository.addAlarms(alarm!!).collect { result ->
                actionListener.postAction(Pair(AlarmAction.CREATE, result))
            }
        }
    }

    private fun updateAlarm() {
        viewModelScope.launch {
            alarm?.let {
                alarm = generateAlarm(it.id)
                alarmRepository.updateAlarms(alarm!!).collect { result ->
                    actionListener.postAction(Pair(AlarmAction.UPDATE, result))
                }
            }
        }
    }

    fun deleteAlarm() {
        viewModelScope.launch {
            alarm?.let {
                alarmRepository.deleteAlarms(it).collect { result ->
                    actionListener.postAction(Pair(AlarmAction.DELETE, result))
                }
            }
        }
    }

    private fun generateAlarm(id: Long? = null) = Alarm(
        id = id ?: System.currentTimeMillis(),
        label = label.value,
        time = wakeTime.value!!.toSecondOfDay().toLong(),
        bedTime = bedTime.value!!.toSecondOfDay().toLong(),
        isEnabled = true,
        isVibrationEnabled = isVibrationEnabled.value == true,
        allDays = weekDays.value,
        soundUri = soundUri.value
    )

    private fun initWeekDays(array: SparseBooleanArray? = null): SparseBooleanArray {
        return array?.apply {
            (Calendar.SUNDAY..Calendar.SATURDAY).forEach {
                if (!contains(it)) {
                    put(it, false)
                }
            }
        } ?: run {
            SparseBooleanArray().apply {
                (Calendar.SUNDAY..Calendar.SATURDAY).forEach {
                    put(it, true)
                }
            }
        }
    }
}