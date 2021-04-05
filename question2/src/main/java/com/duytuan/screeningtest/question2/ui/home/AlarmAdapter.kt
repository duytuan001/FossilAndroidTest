package com.duytuan.screeningtest.question2.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.duytuan.screeningtest.core.model.ActionType
import com.duytuan.screeningtest.question2.databinding.ItemAlarmBinding
import com.duytuan.screeningtest.question2.model.Alarm

class AlarmAdapter(
    private val actionListener: (ActionType, Alarm, Int) -> Unit
) : PagedListAdapter<Alarm, AlarmViewHolder>(DIFF_CALLBACK) {
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item: Alarm? = getItem(position)
        holder.bindTo(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            actionListener
        )
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Alarm>() {
            override fun areItemsTheSame(
                oldItem: Alarm,
                newItem: Alarm
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Alarm,
                newItem: Alarm
            ) = oldItem.time == newItem.time &&
                oldItem.isEnabled == newItem.isEnabled &&
                oldItem.label == newItem.label &&
                oldItem.allDays == newItem.allDays
        }
    }
}