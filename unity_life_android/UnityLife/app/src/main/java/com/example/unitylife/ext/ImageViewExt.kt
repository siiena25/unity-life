package com.example.unitylife.ext

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadAvatar(url: String?) {
    Glide.with(this)
            .load(url)
            .into(this)
}

fun ImageView.loadBanner(url:String?) {
    Glide.with(this)
            .load(url)
            .into(this)
}