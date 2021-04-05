package com.duytuan.screeningtest.core.extension

import android.view.View
import androidx.lifecycle.LifecycleOwner

fun LifecycleOwner.setOnClickListeners(vararg views: View) {
    (this as View.OnClickListener).apply {
        views.forEach { it.setOnClickListener(this) }
    }
}