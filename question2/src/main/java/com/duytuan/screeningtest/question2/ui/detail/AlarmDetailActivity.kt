package com.duytuan.screeningtest.question2.ui.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.text.SpannableStringBuilder
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.forEachIndexed
import com.duytuan.screeningtest.core.base.BaseActivity
import com.duytuan.screeningtest.core.delegate.contentView
import com.duytuan.screeningtest.core.extension.setOnClickListeners
import com.duytuan.screeningtest.core.model.TResult
import com.duytuan.screeningtest.core.util.ScreenUtil
import com.duytuan.screeningtest.question2.R
import com.duytuan.screeningtest.question2.databinding.ActivityAlarmDetailBinding
import com.duytuan.screeningtest.question2.model.AlarmAction
import com.duytuan.screeningtest.question2.util.AlarmUtil
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class AlarmDetailActivity : BaseActivity(), View.OnClickListener {

    private val binding by contentView<AlarmDetailActivity, ActivityAlarmDetailBinding>(R.layout.activity_alarm_detail)
    private val viewModel by viewModels<AlarmDetailViewModel>()

    private val getSound = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.extras?.getParcelable<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)?.let {
                viewModel.setSoundUri(it)
            }
        }
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

    override fun setupViews() {
        // drag to dismiss effect
        ScreenUtil.setupDragScreen(this, binding.vScrollView, false)

        binding.viewModel = viewModel

        setupTimePicker()
        setupWeekDaysView()
        setupVibration()
        setupLabelView()

        setOnClickListeners(binding.btnCancel, binding.btnDelete, binding.btnSave, binding.tvSoundLabel, binding.tvWakeTime)
        setupActionListeners()
    }

    private fun setupTimePicker() {
        viewModel.wakeTime.value?.let { wakeTime ->
            viewModel.bedTime.value?.let { bedTime ->
                binding.vTimePicker.setTime(bedTime, wakeTime)
                handleUpdate(bedTime, wakeTime)
            }
        }
        binding.vTimePicker.listener = { bedTime: LocalTime, wakeTime: LocalTime ->
            viewModel.setBedTime(bedTime)
            viewModel.setWakeTime(wakeTime)

            handleUpdate(bedTime, wakeTime)
        }
    }

    private fun setupActionListeners() {
        viewModel.actionListener.observe(this) {
            if (it.second is TResult.Success) {
                when (it.first) {
                    AlarmAction.CREATE -> {
                        viewModel.alarm?.let { alarm ->
                            AlarmUtil.setReminderAlarm(this, alarm)
                        }
                    }
                    AlarmAction.UPDATE -> {
                        viewModel.alarm?.let { alarm ->
                            AlarmUtil.setReminderAlarm(this, alarm)
                        }
                    }
                    AlarmAction.DELETE -> {
                        viewModel.alarm?.let { alarm ->
                            AlarmUtil.cancelReminderAlarm(this, alarm)
                        }
                    }
                    else -> {
                    }
                }
                finishAfterTransition()
            }
        }
    }

    private fun setupVibration() {
        binding.swVibrate.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setVibrationEnable(isChecked)
        }
    }

    private fun setupWeekDaysView() {
        val days = DateFormatSymbols().shortWeekdays
        binding.gbWeekday.bgWeekdays.forEachIndexed { index, view ->
            (view as TextView).text = days[index + 1]
        }

        viewModel.weekDays.observe(this) { arr ->
            if (arr != null) {
                (Calendar.SUNDAY..Calendar.SATURDAY).forEach {
                    (binding.gbWeekday.bgWeekdays.getChildAt(it - 1) as MaterialButton?)?.isChecked = arr[it]
                }
            }
        }

        binding.gbWeekday.bgWeekdays.addOnButtonCheckedListener { _, checkedId, isChecked ->
            val dayIndex = when (checkedId) {
                R.id.btnDay1 -> Calendar.SUNDAY
                R.id.btnDay2 -> Calendar.MONDAY
                R.id.btnDay3 -> Calendar.TUESDAY
                R.id.btnDay4 -> Calendar.WEDNESDAY
                R.id.btnDay5 -> Calendar.THURSDAY
                R.id.btnDay6 -> Calendar.FRIDAY
                R.id.btnDay7 -> Calendar.SATURDAY
                else -> 0
            }
            if (dayIndex > 0) {
                viewModel.setSelectedDay(dayIndex, isChecked)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLabelView() {
        binding.vContainer.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && binding.etLabel.hasFocus()) {
                ScreenUtil.displayKeyboard(binding.etLabel, isShow = false, isClearFocus = true)
            }
            false
        }
    }

    private fun handleUpdate(bedTime: LocalTime, wakeTime: LocalTime) {

        binding.tvBedTime.text = bedTime.format(timeFormatter)
        binding.tvWakeTime.text = wakeTime.format(timeFormatter)

        // display sleep duration -->
        val bedDate = bedTime.atDate(LocalDate.now())
        var wakeDate = wakeTime.atDate(LocalDate.now())

        if (bedDate >= wakeDate) wakeDate = wakeDate.plusDays(1)

        val duration = Duration.between(bedDate, wakeDate)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        val stringBuilder = StringBuilder()
        val builder = SpannableStringBuilder()
        if (hours > 0) {
            builder.append()
            stringBuilder.append(getString(R.string.sleep_schedule_hr, hours))
        }
        if (minutes > 0) {
            stringBuilder.append(" ").append(getString(R.string.sleep_schedule_min, minutes))
        }

        binding.tvDuration.text = stringBuilder.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> viewModel.updateOrCreateAlarm()
            R.id.btnCancel -> finishAfterTransition()
            R.id.btnDelete -> viewModel.deleteAlarm()
            R.id.tvSoundLabel -> displaySoundPicker()
            R.id.tvWakeTime -> showTimePicker()
        }
    }

    private fun displaySoundPicker() {
        getSound.launch(Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM or RingtoneManager.TYPE_NOTIFICATION)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.alarm_detail_select_sound))
//            viewModel.soundUri.value?.let {
//                putExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, it)
//                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, it)
//            }
        })
    }

    private fun showTimePicker() {
        val hours = viewModel.wakeTime.value?.hour ?: 0
        val minutes = viewModel.wakeTime.value?.minute ?: 0
        TimePickerDialog(this, { _, hourOfDay, minute ->
            val localTime = LocalTime.of(hourOfDay, minute)
            viewModel.setWakeTime(localTime)
            binding.vTimePicker.setWakeTime(localTime)
            handleUpdate(viewModel.bedTime.value ?: localTime, localTime)
        }, hours, minutes, true).show()
    }
}