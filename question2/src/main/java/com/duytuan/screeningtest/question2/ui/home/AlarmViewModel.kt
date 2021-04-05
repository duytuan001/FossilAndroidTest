package com.duytuan.screeningtest.question2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.duytuan.screeningtest.core.base.BaseViewModel
import com.duytuan.screeningtest.question2.model.Alarm
import com.duytuan.screeningtest.question2.model.AlarmAction
import com.duytuan.screeningtest.question2.repository.AlarmRepository
import com.duytuan.screeningtest.question2.util.ActionLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : BaseViewModel() {

    val alarms: LiveData<PagedList<Alarm>> = alarmRepository.getAlarms()
    val actionListener by lazy { ActionLiveData<Pair<AlarmAction, Alarm>>() }

    fun updateAlarm(alarm: Alarm, isEnabled: Boolean) {
        viewModelScope.launch {
            val newAlarm = alarm.copy(isEnabled = isEnabled)
            alarmRepository.updateAlarms(newAlarm).collect()
            actionListener.postAction(Pair(if (isEnabled) AlarmAction.ENABLE else AlarmAction.DISABLE, newAlarm))
        }
    }
}