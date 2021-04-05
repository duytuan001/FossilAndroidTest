package com.duytuan.screeningtest.core.widget.scroll

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

class SpringHorizontalRecyclerView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attr, defStyle) {

    private var startDragX: Float = 0f
    private val springAnim: SpringAnimation =
        SpringAnimation(this, SpringAnimation.TRANSLATION_X, 0.0f)

    var callback: ((Float, Boolean) -> Unit)? = null

    init {
        springAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
        springAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        overScrollMode = OVER_SCROLL_NEVER
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_MOVE ->
                if (scrollX <= 0) {
                    if (startDragX == 0f) {
                        startDragX = ev.rawX
                    }
                    if (ev.rawX - startDragX >= 0) {
                        translationX = (ev.rawX - startDragX) / 3
                        callback?.invoke(translationX, false)
                        return true
                    } else {
                        startDragX = 0f
                        springAnim.cancel()
                        translationX = 0f
                    }
                    callback?.invoke(translationX, false)
                } else if (scrollX + width >= getChildAt(0).measuredWidth) {
                    if (startDragX == 0f) {
                        startDragX = ev.rawX
                    }
                    if (ev.rawX - startDragX <= 0) {
                        translationX = (ev.rawX - startDragX) / 3
                        callback?.invoke(translationX, false)
                        return true
                    } else {
                        startDragX = 0f
                        springAnim.cancel()
                        translationX = 0f
                    }
                    callback?.invoke(translationX, false)
                }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (translationX != 0f) {
                    springAnim.start()
                }
                callback?.invoke(translationX, true)
                startDragX = 0f
            }
        }
        return super.onTouchEvent(ev)
    }
}