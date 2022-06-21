package ru.mail.search.assistant.services.music.callback.cover

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

class CoverLoader(context: Context) {

    private val glide = Glide.with(context)
    private var lastTarget: Target<Bitmap>? = null

    fun loadFromCache(uri: Uri, callback: (uri: Uri, bitmap: Bitmap?) -> Unit) {
        val target = object : CustomTarget<Bitmap>() {

            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                if (lastTarget == this) {
                    lastTarget = null
                    callback(uri, null)
                }
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                if (lastTarget == this) {
                    lastTarget = null
                    callback(uri, resource)
                }
            }
        }
        lastTarget = target
        glide.asBitmap().load(uri).onlyRetrieveFromCache(true).into(target)
    }

    fun loadFromRemote(uri: Uri, callback: (uri: Uri, bitmap: Bitmap?) -> Unit) {
        val target = object : CustomTarget<Bitmap>() {

            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                if (lastTarget == this) {
                    lastTarget = null
                    callback(uri, null)
                }
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                if (lastTarget == this) {
                    lastTarget = null
                    callback(uri, resource)
                }
            }
        }
        lastTarget = target
        glide.asBitmap().load(uri).into(target)
    }

    fun cancel() {
        glide.clear(lastTarget)
        lastTarget = null
    }
}