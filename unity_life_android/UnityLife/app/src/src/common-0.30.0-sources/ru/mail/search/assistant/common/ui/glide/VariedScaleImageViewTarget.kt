package ru.mail.search.assistant.common.ui.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition

class VariedScaleImageViewTarget(
    view: ImageView,
    private val scaleType: ImageView.ScaleType,
    private val placeholderScaleType: ImageView.ScaleType
) : DrawableImageViewTarget(view) {

    override fun onLoadCleared(placeholder: Drawable?) {
        view.scaleType = placeholderScaleType
        super.onLoadCleared(placeholder)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        view.scaleType = placeholderScaleType
        super.onLoadFailed(errorDrawable)
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        view.scaleType = scaleType
        super.onResourceReady(resource, transition)
    }
}