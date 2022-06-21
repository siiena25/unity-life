package ru.mail.search.assistant.services.music

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import kotlinx.coroutines.*
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.coroutines.requireJob
import ru.mail.search.assistant.interactor.PlayerLimitInteractor
import ru.mail.search.assistant.services.music.extension.MEDIA_BUNDLE_TRACK_NUMBER
import ru.mail.search.assistant.services.music.extension.MEDIA_BUNDLE_TRACK_POSITION
import ru.mail.search.assistant.services.music.extension.customDescription
import ru.mail.search.assistant.services.music.extension.mediaUri
import ru.mail.search.assistant.util.analytics.event.MediaPlayerError
import kotlin.coroutines.CoroutineContext

internal class MusicPlaybackPreparer(
    private val exoPlayer: ExoPlayer,
    private val dataSource: PlayBackDataSource,
    private val mediaSourceFactory: MusicMediaSourceFactory,
    private val playerLimitInteractor: PlayerLimitInteractor,
    private val analytics: Analytics?,
    private val logger: Logger?,
    parentContext: CoroutineContext
) : MediaSessionConnector.PlaybackPreparer {

    companion object {
        private const val TAG = "MusicPlaybackPreparer"
    }

    private val coroutineContext = SupervisorJob(parent = parentContext.requireJob())
    private val preparerScope = CoroutineScope(coroutineContext + Dispatchers.Main)

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
    }

    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean = false

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        preparerScope.launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    playerLimitInteractor.prepareLimits()
                    dataSource.loadByMediaId(mediaId)
                }
            }
                .onFailure { error ->
                    logger?.e(TAG, error)
                    analytics?.log(MediaPlayerError("Error while preparing media"))
                }
                .getOrNull()?.let { state ->
                    val trackNumber = extras?.getInt(MEDIA_BUNDLE_TRACK_NUMBER, 0) ?: 0
                    val trackPosition = extras?.getLong(MEDIA_BUNDLE_TRACK_POSITION, 0) ?: 0
                    exoPlayer.stop()
                    exoPlayer.playWhenReady =
                        playWhenReady && playerLimitInteractor.isPlayingAvailable()
                    exoPlayer.prepare(getMediaSource(state.playlist))
                    exoPlayer.seekTo(trackNumber, trackPosition)
                }
        }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
    }

    override fun onPrepare(playWhenReady: Boolean) {
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.prepare()
    }

    private fun getMediaSource(playlist: List<MediaMetadataCompat>): MediaSource {
        val sources = playlist.map { meta ->
            MediaDescriptionCompat.Builder()
            mediaSourceFactory.fromUri(Uri.parse(meta.mediaUri), meta.customDescription)
        }
        return ConcatenatingMediaSource(*sources.toTypedArray())
    }
}