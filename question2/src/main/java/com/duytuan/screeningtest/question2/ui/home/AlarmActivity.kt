package com.duytuan.screeningtest.question2.ui.home

import android.animation.AnimatorInflater
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.duytuan.screeningtest.core.base.BaseActivity
import com.duytuan.screeningtest.core.delegate.contentView
import com.duytuan.screeningtest.core.model.ActionType
import com.duytuan.screeningtest.core.util.ScreenUtil
import com.duytuan.screeningtest.question2.R
import com.duytuan.screeningtest.question2.databinding.ActivityAlarmBinding
import com.duytuan.screeningtest.question2.model.Alarm
import com.duytuan.screeningtest.question2.model.AlarmAction
import com.duytuan.screeningtest.question2.ui.detail.AlarmDetailActivity
import com.duytuan.screeningtest.question2.util.AlarmUtil
import com.duytuan.screeningtest.question2.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : BaseActivity() {
    private val binding by contentView<AlarmActivity, ActivityAlarmBinding>(R.layout.activity_alarm)
    private val viewModel by viewModels<AlarmViewModel>()

    private var emptyView: View? = null

    override fun setupViews() {
        setTheme(R.style.Theme_App)
        ScreenUtil.forceDarkMode()

        setupAlarmListView()
        setupAddButton()
        setupActionListener()
    }

    private fun setupActionListener() {
        viewModel.actionListener.observe(this) {
            when (it.first) {
                AlarmAction.ENABLE -> AlarmUtil.setReminderAlarm(this, it.second)
                AlarmAction.DISABLE -> AlarmUtil.cancelReminderAlarm(this, it.second)
                else -> {
                }
            }
        }
    }

    private fun setupAddButton() {
        try {
            // press scale effect
            // crash on API 21
            binding.btnAdd.stateListAnimator = AnimatorInflater.loadStateListAnimator(baseContext, R.animator.btn_scale_state)
        } catch (e: Exception) {

        }
        binding.btnAdd.setOnClickListener {
            navigateToCreateAlarm()
        }
    }

    private fun setupAlarmListView() {
        val listAdapter = AlarmAdapter { actionType, item, position ->
            when (actionType) {
                ActionType.VIEW -> navigateToAlarmDetail(item)
                ActionType.TOGGLE -> setToggleEnableAlarm(item)
                else -> {
                }
            }
        }
        binding.rvAlarms.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = listAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewModel.alarms.observe(this) {
            listAdapter.submitList(it)
            if (it.isEmpty()) {
                showAlarmEmptyView()
            } else {
                hideAlarmEmptyView()
            }
        }
    }

    private fun setToggleEnableAlarm(alarm: Alarm) {
        with(!alarm.isEnabled) {
            viewModel.updateAlarm(alarm, this)
        }

    }

    private fun showAlarmEmptyView() {
        if (!binding.vEmptyStub.isInflated) {
            emptyView = binding.vEmptyStub.viewStub?.inflate()
        } else {
            emptyView?.isVisible = true
        }
    }

    private fun hideAlarmEmptyView() {
        emptyView?.isVisible = false
    }

    private fun navigateToAlarmDetail(alarm: Alarm? = null) {
        startActivity(Intent(this, AlarmDetailActivity::class.java).apply {
            putExtra(Constants.KEY_ALARM, alarm)
        })
    }

    private fun navigateToCreateAlarm() {
        navigateToAlarmDetail()
    }

}