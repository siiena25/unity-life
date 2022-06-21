package ru.mail.search.assistant.services.music

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import ru.mail.search.assistant.media.MediaPlayerFactory

internal class MusicMediaSourceFactory(private val cache: Cache?) {

    fun fromUri(uri: Uri, tag: Any?): MediaSource {
        val mediaItem = createMediaItem(uri, tag)
        return if (Util.inferContentType(uri) == C.TYPE_HLS) {
            buildHlsMediaSourceFactory().createMediaSource(mediaItem)
        } else {
            buildDefaultMediaSourceFactory().createMediaSource(mediaItem)
        }
    }

    private fun createDataSourceFactory(): DataSource.Factory {
        val httpDsf = DefaultHttpDataSource.Factory()
            .setUserAgent(MediaPlayerFactory.DEFAULT_DATA_SOURCE_TAG)
        return if (cache == null) {
            httpDsf
        } else {
            CacheDataSource.Factory().setCache(cache).setUpstreamDataSourceFactory(httpDsf)
        }
    }

    private fun buildHlsMediaSourceFactory(): HlsMediaSource.Factory {
        return HlsMediaSource.Factory(DefaultHlsDataSourceFactory(createDataSourceFactory()))
    }

    private fun buildDefaultMediaSourceFactory(): ProgressiveMediaSource.Factory {
        return ProgressiveMediaSource.Factory(createDataSourceFactory())
    }

    private fun createMediaItem(uri: Uri, tag: Any?): MediaItem {
        return MediaItem.Builder().setUri(uri).setTag(tag).build()
    }
}
