package ru.mail.search.assistant.services.music

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import ru.mail.search.assistant.data.local.LocalSettingsDataSource
import java.io.File

class MusicMediaSourceCacheProvider(
    context: Context,
    private val settingsDataSource: LocalSettingsDataSource
) {

    val cacheDir = File(context.cacheDir, "music_player")

    private val exoDatabaseProvider = ExoDatabaseProvider(context)
    private val maxCacheSizeBytes get() = settingsDataSource.maxPlayerCacheSizeMb * 1024L * 1024L

    fun createCache(): Cache? {
        return if (maxCacheSizeBytes != 0L) {
            val evictor = LeastRecentlyUsedCacheEvictor(maxCacheSizeBytes)
            SimpleCache(cacheDir, evictor, exoDatabaseProvider)
        } else {
            null
        }
    }
}