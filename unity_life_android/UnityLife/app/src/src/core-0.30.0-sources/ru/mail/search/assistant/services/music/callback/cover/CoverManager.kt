package ru.mail.search.assistant.services.music.callback.cover

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.core.content.ContextCompat
import ru.mail.search.assistant.services.notification.PlayerNotificationResourcesProvider

class CoverManager(
    private val context: Context,
    private val loader: CoverLoader,
    private val notificationResourcesProvider: PlayerNotificationResourcesProvider,
    private val invalidateCallback: () -> Unit
) {

    private val emptyCover = Cover(null, null)

    private var currentCover = emptyCover
    private var isActive = false
    var stage = Stage.IDLE
        private set
    private var placeholder: Bitmap? = null

    fun activate() {
        isActive = true
        val cover = currentCover
        // repeat last request
        if (cover.uri != null && cover.bitmap == null && stage == Stage.IDLE) {
            requestCover(cover.uri)?.let { invalidateMetadata() }
        }
    }

    fun deactivate() {
        isActive = false
        currentCover = emptyCover
        placeholder = null
        loader.cancel()
        stage = Stage.IDLE
    }

    fun requestCover(uri: Uri?): CoverState? {
        // if uri is null, then unconditionally return placeholder
        if (uri == null) {
            loader.cancel()
            return CoverState(requestPlaceholder(), Stage.IDLE)
        }

        // check if cover with given url is already requested
        if (currentCover.uri == uri && (currentCover.bitmap != null || stage != Stage.IDLE)) {
            return CoverState(currentCover.bitmap ?: requestPlaceholder(), stage)
        }

        currentCover = Cover(uri, null)
        if (!isActive) return null
        loader.cancel()
        var isImmediate = true
        stage = Stage.CACHE
        loader.loadFromCache(uri) { loadedUri, loadedBitmap ->
            stage = Stage.IDLE
            val cover = currentCover
            if (isActive && cover.uri == loadedUri && cover.bitmap == null) {
                if (loadedBitmap != null) {
                    currentCover = cover.copy(bitmap = loadedBitmap)
                    if (!isImmediate) {
                        invalidateMetadata()
                    }
                } else {
                    loadFromRemote(uri)
                    if (!isImmediate) {
                        invalidateMetadata()
                    }
                }
            }
        }
        isImmediate = false
        return CoverState(currentCover.bitmap ?: requestPlaceholder(), stage)
    }

    private fun loadFromRemote(uri: Uri) {
        stage = Stage.REMOTE
        loader.loadFromRemote(uri) { loadedUri, loadedBitmap ->
            stage = Stage.IDLE
            val cover = currentCover
            if (isActive && cover.uri == loadedUri && cover.bitmap == null) {
                if (loadedBitmap != null) {
                    currentCover = cover.copy(bitmap = loadedBitmap)
                    invalidateMetadata()
                }
            }
        }
    }

    private fun invalidateMetadata() {
        invalidateCallback()
    }

    private fun requestPlaceholder(): Bitmap {
        val current = placeholder
        return if (current == null) {
            val newPlaceholder = createPlaceholder()
            placeholder = newPlaceholder
            newPlaceholder
        } else {
            current
        }
    }

    private fun createPlaceholder(): Bitmap {
        val size = dip(384)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444)
        ContextCompat.getDrawable(
            context,
            notificationResourcesProvider.playerCoverPlaceholderRes
        )?.let { drawable ->
            drawable.setBounds(0, 0, bitmap.height, bitmap.width)
            drawable.draw(Canvas(bitmap))
        }
        return bitmap
    }

    private fun dip(value: Int): Int {
        return (value / Resources.getSystem().displayMetrics.density).toInt()
    }

    data class Cover(
        val uri: Uri?,
        val bitmap: Bitmap?
    )

    data class CoverState(
        val bitmap: Bitmap,
        val stage: Stage
    )

    enum class Stage {

        IDLE, CACHE, REMOTE
    }
}