package com.example.unitylife.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

object InsetUtils {
    private var statusBarSize: Int = 84
    private var navigationBarSize: Int = 146

    fun removeSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val desiredBottomInset = calculateDesiredBottomInset(
                view,
                insets.systemWindowInsetTop,
                0,
                listener
            )

            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, insets.stableInsetBottom)
            )
        }
    }

    fun returnSystemInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val system = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, system.top, 0, system.bottom)
            )
        }
    }

    private fun calculateDesiredBottomInset(
        view: View,
        topInset: Int,
        bottomInset: Int,
        listener: OnSystemInsetsChangedListener
    ): Int {
        val hasKeyboard = isKeyboardAppeared(view, bottomInset)
        val desiredBottomInset = if (hasKeyboard) bottomInset else 0
        listener(topInset, if (hasKeyboard) 0 else bottomInset)
        return desiredBottomInset
    }

    private fun isKeyboardAppeared(view: View, bottomInset: Int) =
        bottomInset / view.resources.displayMetrics.heightPixels.toDouble() > .25


    fun setStatusBarSize(size: Int) {
        statusBarSize = size
    }

    fun setNavigationBarSize(size: Int) {
        navigationBarSize = size
    }

    fun getNavigationBarSize() = navigationBarSize

    fun getStatusBarSize(): Int = statusBarSize
}