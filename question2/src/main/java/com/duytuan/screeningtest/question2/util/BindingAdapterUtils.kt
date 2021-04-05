package com.duytuan.screeningtest.question2.util

import android.media.RingtoneManager
import android.view.View
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.duytuan.screeningtest.question2.R

@BindingAdapter("isVisible")
fun setIsVisible(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("textNumber")
fun setTextNum(view: TextView, number: Int?) {
    view.text = number?.toString()
}

@BindingAdapter("soundName")
fun setSoundName(view: TextView, uriStr: String?) {
    view.text = uriStr?.let {
        RingtoneManager.getRingtone(view.context, it.toUri()).getTitle(view.context)
    } ?: view.context.getString(R.string.alarm_detail_sound_name_default)
}