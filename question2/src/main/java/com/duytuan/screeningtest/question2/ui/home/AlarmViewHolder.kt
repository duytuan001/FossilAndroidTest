package com.duytuan.screeningtest.question2.ui.home

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.duytuan.screeningtest.core.model.ActionType
import com.duytuan.screeningtest.question2.R
import com.duytuan.screeningtest.question2.databinding.ItemAlarmBinding
import com.duytuan.screeningtest.question2.model.Alarm

class AlarmViewHolder(
    private val binding: ItemAlarmBinding,
    val actionListener: (ActionType, Alarm, Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(item: Alarm?) {
        item?.also {
            binding.item = it
            binding.handlers = View.OnClickListener {
                val actionType = when (it.id) {
                    R.id.vContainer -> ActionType.VIEW
                    R.id.swEnable -> {
                        binding.swEnable.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        ActionType.TOGGLE
                    }
                    else -> ActionType.VIEW
                }
                actionListener(actionType, item, adapterPosition)
            }
            binding.swEnable.jumpDrawablesToCurrentState()
            binding.enabledAlpha = if (it.isEnabled) 1F else 0.4F
        } ?: run {
            binding.tvTime.setText(R.string.alarm_item_empty_time)
            binding.tvLabelAndRepeat.text = ""
            binding.handlers = null
        }
        binding.executePendingBindings()
    }
}