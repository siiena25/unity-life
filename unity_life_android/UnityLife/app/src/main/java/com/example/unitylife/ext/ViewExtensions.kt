package com.example.unitylife.ext

import android.animation.ValueAnimator
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.animation.doOnEnd
import androidx.core.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Potentially animate showing a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot of the view, and animate this in, only changing the visibility (and
 * thus layout) when the animation completes.
 */
fun BottomNavigationView.show() {
    if (visibility == VISIBLE) return

    val parent = parent as ViewGroup
    // View needs to be laid out to create a snapshot & know position to animate. If view isn't
    // laid out yet, need to do this manually.
    if (!isLaidOut) {
        measure(
            MeasureSpec.makeMeasureSpec(parent.width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parent.height, MeasureSpec.AT_MOST)
        )
        layout(parent.left, parent.height - measuredHeight, parent.right, parent.height)
    }


    ValueAnimator.ofInt(parent.height, top).apply {
        startDelay = 100L
        duration = 300L
        interpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.linear_out_slow_in
        )
        doOnEnd {
            visibility = VISIBLE
        }
        start()
    }
}

/**
 * Potentially animate hiding a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot, instantly hide the view (so content lays out to fill), then animate
 * out the snapshot.
 */
fun BottomNavigationView.hide() {
    if (visibility == GONE) return

    val parent = parent as ViewGroup
    if (!isLaidOut) {
        measure(
            MeasureSpec.makeMeasureSpec(parent.width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parent.height, MeasureSpec.AT_MOST)
        )
        layout(parent.left, parent.height - measuredHeight, parent.right, parent.height)
    }
    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    drawable.setBounds(left, top, right, bottom)
    parent.overlay.add(drawable)

    visibility = GONE

    ValueAnimator.ofInt(top, parent.height).apply {
        startDelay = 100L
        duration = 200L
        interpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.fast_out_linear_in
        )
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
        }
        start()
    }
}

fun View.updateMargin(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom,
) = updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMargins(left, top, right, bottom) }

fun View.hide() {
    visibility = INVISIBLE
}

fun View.show() {
    visibility = VISIBLE
}

fun View.remove() {
    visibility = GONE
}

fun View.deleteIf(condition: Boolean) {
    if (condition) {
        remove()
    } else {
        show()
    }
}