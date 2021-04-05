package com.duytuan.screeningtest.core.widget.scroll

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

class SpringRecyclerView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attr, defStyle) {

    private var startDragY: Float = 0f
    private val springAnim: SpringAnimation =
        SpringAnimation(this, SpringAnimation.TRANSLATION_Y, 0.0f)

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
                if (scrollY <= 0) {
                    if (startDragY == 0f) {
                        startDragY = ev.rawY
                    }
                    if (ev.rawY - startDragY >= 0) {
                        translationY = (ev.rawY - startDragY) / 3
                        callback?.invoke(translationY, false)
                        return true
                    } else {
                        startDragY = 0f
                        springAnim.cancel()
                        translationY = 0f
                    }
                    callback?.invoke(translationY, false)
                } else if (scrollY + height >= getChildAt(0).measuredHeight) {
                    if (startDragY == 0f) {
                        startDragY = ev.rawY
                    }
                    if (ev.rawY - startDragY <= 0) {
                        translationY = (ev.rawY - startDragY) / 3
                        callback?.invoke(translationY, false)
                        return true
                    } else {
                        startDragY = 0f
                        springAnim.cancel()
                        translationY = 0f
                    }
                    callback?.invoke(translationY, false)
                }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (translationY != 0f) {
                    springAnim.start()
                }
                callback?.invoke(translationY, true)
                startDragY = 0f
            }
        }
        return super.onTouchEvent(ev)
    }
}