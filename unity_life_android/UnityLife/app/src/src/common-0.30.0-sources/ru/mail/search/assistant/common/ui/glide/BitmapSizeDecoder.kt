package ru.mail.search.assistant.common.ui.glide

import android.graphics.BitmapFactory
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import java.io.File

class BitmapSizeDecoder : ResourceDecoder<File, BitmapFactory.Options> {

    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    override fun handles(file: File, options: Options): Boolean = true

    override fun decode(
        file: File,
        width: Int,
        height: Int,
        options: Options
    ): Resource<BitmapFactory.Options>? {
        BitmapFactory.decodeFile(file.absolutePath, bitmapOptions)
        return SimpleResource(bitmapOptions)
    }
}