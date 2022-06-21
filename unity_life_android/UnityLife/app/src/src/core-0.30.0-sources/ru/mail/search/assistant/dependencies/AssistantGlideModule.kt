package ru.mail.search.assistant.dependencies

import android.content.Context
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.LibraryGlideModule
import ru.mail.search.assistant.common.ui.glide.BitmapSizeDecoder
import java.io.File

@GlideModule
internal class AssistantGlideModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(
            File::class.java,
            BitmapFactory.Options::class.java,
            BitmapSizeDecoder()
        )
    }
}