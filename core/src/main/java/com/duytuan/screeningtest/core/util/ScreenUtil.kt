package com.duytuan.screeningtest.core.util

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.duytuan.screeningtest.core.R
import com.duytuan.screeningtest.core.widget.scroll.SpringScrollView

@Suppress("DEPRECATION")
class ScreenUtil {

    companion object {

        private const val MAX_BRIGHTNESS = 1F

        fun forceDarkMode() =
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        @JvmStatic
        fun lockScreen(activity: Activity?, isLock: Boolean) {
            if (isLock) {
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

        @JvmStatic
        fun setStatusBarColor(activity: Activity?, color: Int? = null, colorRes: Int? = null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity?.window?.let { window ->
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    val c = color ?: colorRes?.let { ContextCompat.getColor(activity, colorRes) }
                    ?: Color.WHITE
                    window.statusBarColor = c
                }
            }
        }

        @Suppress("DEPRECATION")
        fun makeStatusBarTransparent(activity: Activity?, isHideNavBar: Boolean = false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity?.window?.apply {
//                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        var systemUiVisibility =
                            (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            systemUiVisibility = systemUiVisibility or if (isHideNavBar) {
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
                            } else {
                                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                            }
                        }
                        decorView.systemUiVisibility = systemUiVisibility
                    } else {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                    statusBarColor = Color.TRANSPARENT
                }
            }
        }

        @Suppress("DEPRECATION")
        fun hideNavigationbar(activity: Activity?) {
            activity?.window?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setDecorFitsSystemWindows(true)
                } else {
                    decorView.systemUiVisibility =
                        decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }
            }
        }

        fun setScreenBright(activity: Activity?, bright: Float) {
            activity?.let {
                val attributes = it.window.attributes
                attributes.screenBrightness = bright
                it.window.attributes = attributes
            }
        }

        fun hideSystemUI(activity: Activity) {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            activity.window.decorView.systemUiVisibility = (
//                    View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        fun showSystemUI(activity: Activity) {
            // Shows the system bars by removing all the flags
            // except for the ones that make the content appear under the system bars.
            activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        fun displayKeyboard(view: View, isShow: Boolean, isClearFocus: Boolean = false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && view.windowInsetsController != null) {
                val controller = view.windowInsetsController
                if (isShow) {
                    controller?.show(WindowInsets.Type.ime())
                } else {
                    controller?.hide(WindowInsets.Type.ime())
                }
            } else {
                val imm =
                    view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (isShow) {
                    view.requestFocus()
                    imm.showSoftInput(view, 0)
                } else {
                    if (isClearFocus) {
                        view.clearFocus()
                    }
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }

        fun setupDragScreen(
            activity: Activity?,
            scrollview: SpringScrollView,
            isHideNarBar: Boolean = true
        ) {
            activity?.apply {
                //makeStatusBarTransparent(this, isHideNarBar)
                val defaultDim = 0.8F
                val minDim = 0.1F
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                window.attributes.dimAmount = defaultDim
                val spaceDismiss = resources.getDimensionPixelOffset(R.dimen.space_54)
                val spaceDim = resources.getDimensionPixelOffset(R.dimen.space_100) * 3
                scrollview.callback = object : (Float, Boolean) -> Unit {

                    override fun invoke(dragY: Float, finish: Boolean) {
                        window.attributes = window.attributes.apply {
                            val offset = defaultDim - dragY / spaceDim
                            dimAmount = if (offset >= minDim) offset else minDim
                        }
                        if (finish) {
                            if ((dragY > spaceDismiss)) {
                                finishAfterTransition()
                            } else {
                                window.attributes = window.attributes.apply {
                                    dimAmount = defaultDim
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}