package ru.mail.search.assistant.services.music

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsCollector
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.EventLogger
import kotlinx.coroutines.*
import ru.mail.search.assistant.media.AssistantBandwidthMeter
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatTrigger
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.data.PlayerEventRepository
import ru.mail.search.assistant.data.player.PlayerStatusRepository
import ru.mail.search.assistant.interactor.PlayerLimitInteractor
import ru.mail.search.assistant.services.music.action.*
import ru.mail.search.assistant.services.music.callback.MediaControllerCallback
import ru.mail.search.assistant.services.music.callback.cover.CoverCallback
import ru.mail.search.assistant.services.music.callback.cover.CoverLoader
import ru.mail.search.assistant.services.music.callback.cover.CoverManager
import ru.mail.search.assistant.services.music.callback.limit.BackgroundMusicDataSource
import ru.mail.search.assistant.services.music.callback.limit.LimitationCallback
import ru.mail.search.assistant.services.music.callback.noisy.BecomingNoisyCallback
import ru.mail.search.assistant.services.music.callback.noisy.BecomingNoisyReceiver
import ru.mail.search.assistant.services.music.callback.notification.PlayerNotificationCallback
import ru.mail.search.assistant.services.notification.PlayerNotificationManager
import ru.mail.search.assistant.services.notification.PlayerNotificationResourcesProvider
import ru.mail.search.assistant.util.requireCoreModule

class MusicPlayerService : MediaBrowserServiceCompat() {

    private lateinit var mediaBrowserDataSource: MediaBrowserDataSource
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var playerEventRepository: PlayerEventRepository
    private lateinit var playerStatusRepository: PlayerStatusRepository
    private lateinit var exoPlayer: ExoPlayer

    private val coroutineContext = SupervisorJob()
    private val serviceScope = CoroutineScope(coroutineContext + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        val module = requireCoreModule()
        val notificationManager = module.playerNotificationManager
        val playerNotificationResourcesProvider = module.notificationResourcesProvider
        val messageRepository = module.messageRepository
        val limitInteractor = module.playerLimitInteractor
        val cacheProvider = module.musicMediaSourceCacheProvider
        val analytics = module.analytics
        playerEventRepository = module.playerEventRepository
        playerStatusRepository = module.playerStatusRepository
        val messageMapper = MessageMapper()
        val logger = module.logger

        mediaBrowserDataSource = MediaBrowserDataSource(messageRepository, messageMapper)
        mediaSession = createMediaSession()
        sessionToken = mediaSession.sessionToken

        val coverManager = createCoverManager(playerNotificationResourcesProvider)
        mediaController = createMediaControllerCompat(
            notificationManager,
            mediaSession,
            coverManager,
            limitInteractor,
            analytics,
            playerEventRepository,
            playerStatusRepository,
            module.backgroundMusicDataSource,
            module.poolDispatcher,
            logger,
        )
        exoPlayer = createExoPlayer(
            playerEventRepository,
            playerStatusRepository,
            mediaController,
            module.bandwidthMeter
        )
        val cache = createCache(cacheProvider)
        mediaSessionConnector = createMediaSessionConnector(
            mediaSession,
            mediaController,
            exoPlayer,
            messageRepository,
            limitInteractor,
            analytics,
            logger,
            messageMapper,
            coverManager,
            playerEventRepository,
            cache
        )
        mediaSession.isActive = true
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        playerEventRepository.onTrackStop(
            PlayerDeviceStatTrigger.OS_UI,
            mediaController
        )
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    override fun onDestroy() {
        mediaSession.isActive = false
        mediaSession.release()
        serviceScope.cancel()
        GlobalScope.launch { ExoPlayerCacheHolder.release() }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot = BrowserRoot("root", null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == "root") {
            result.sendResult(mutableListOf())
            return
        }
        serviceScope.launch {
            result.detach()
            val state = withContext(Dispatchers.IO) {
                runCatching { mediaBrowserDataSource.loadChildrenByParentId(parentId) }.getOrNull()
            }
            result.sendResult(state)
        }
    }

    private fun createMediaSession(): MediaSessionCompat {
        val activityPendingIntent = packageManager
            ?.getLaunchIntentForPackage(packageName)
            ?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }
        return MediaSessionCompat(baseContext, "MusicService").apply {
            setSessionActivity(activityPendingIntent)
        }
    }

    private fun createCache(cacheProvider: MusicMediaSourceCacheProvider): Cache? {
        return ExoPlayerCacheHolder.getCache { cacheProvider.createCache() }
    }

    private fun createMediaControllerCompat(
        notificationManager: PlayerNotificationManager?,
        mediaSession: MediaSessionCompat,
        coverManager: CoverManager,
        limitInteractor: PlayerLimitInteractor,
        analytics: Analytics?,
        playerEventRepository: PlayerEventRepository,
        playerStatusRepository: PlayerStatusRepository,
        backgroundMusicDataSource: BackgroundMusicDataSource,
        poolDispatcher: PoolDispatcher,
        logger: Logger?,
    ): MediaControllerCompat {
        return MediaControllerCompat(this, mediaSession).apply {
            val becomingNoisyCallback = createBecomingNoisyCallback(this, playerEventRepository)
            val playerNotificationCallback =
                createPlayerNotificationCallback(notificationManager, this)
            val limitationCallback = LimitationCallback(
                this,
                limitInteractor,
                playerEventRepository,
                backgroundMusicDataSource,
                poolDispatcher,
                logger,
            )
            val coverCallback = CoverCallback(coverManager)
            val playerEventsCallback = playerEventRepository.PlayerEventsCallback(this)
            val playerStatusCallback = playerStatusRepository.PlayerStatusCallback(this)
            registerCallback(
                MediaControllerCallback(
                    listOf(
                        becomingNoisyCallback,
                        playerNotificationCallback,
                        limitationCallback,
                        coverCallback,
                        playerEventsCallback,
                        playerStatusCallback,
                    ),
                    analytics
                )
            )
        }
    }

    private fun createCoverManager(
        notificationResourcesProvider: PlayerNotificationResourcesProvider
    ): CoverManager {
        return CoverManager(this, CoverLoader(this), notificationResourcesProvider) {
            mediaSessionConnector.invalidateMediaSessionMetadata()
        }
    }

    private fun createBecomingNoisyCallback(
        mediaControllerCompat: MediaControllerCompat,
        playerEventRepository: PlayerEventRepository
    ): BecomingNoisyCallback {
        return BecomingNoisyCallback(
            BecomingNoisyReceiver(
                this,
                mediaControllerCompat,
                playerEventRepository
            )
        )
    }

    private fun createPlayerNotificationCallback(
        notificationManager: PlayerNotificationManager?,
        mediaControllerCompat: MediaControllerCompat
    ): PlayerNotificationCallback {
        return PlayerNotificationCallback(this, mediaControllerCompat, notificationManager)
    }

    private fun createMediaSessionConnector(
        mediaSession: MediaSessionCompat,
        mediaController: MediaControllerCompat,
        exoPlayer: ExoPlayer,
        messagesRepository: MessagesRepository,
        limitInteractor: PlayerLimitInteractor,
        analytics: Analytics?,
        logger: Logger?,
        messageMapper: MessageMapper,
        coverManager: CoverManager,
        playerEventRepository: PlayerEventRepository,
        cache: Cache?
    ): MediaSessionConnector {
        return MediaSessionConnector(mediaSession).apply {
            setPlayer(exoPlayer)
            setControlDispatcher(MusicControlDispatcher(limitInteractor))
            setPlaybackPreparer(
                createMusicPlaybackPreparer(
                    exoPlayer,
                    messagesRepository,
                    messageMapper,
                    limitInteractor,
                    analytics,
                    logger,
                    cache
                )
            )
            val volumeManager = VolumeManager(this)
            val metadataProvider =
                CustomMediaMetadataProvider(mediaController, coverManager, volumeManager)
            setMediaMetadataProvider(metadataProvider)
            setQueueNavigator(PlayerQueueNavigator(mediaSession))
            setCustomActionProviders(
                FastForwardActionProvider(),
                FastBackwardActionProvider(),
                SkipToPreviousDefinitely(),
                SetUserVolumeActionProvider(volumeManager),
                DuckVolumeActionProvider(volumeManager),
                UnduckVolumeActionProvider(volumeManager),
                RewindActionProvider()
            )
            setMediaButtonEventHandler { _, _, mediaButtonEvent ->
                val keycodeExtra =
                    mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                if (mediaButtonEvent.action == Intent.ACTION_MEDIA_BUTTON &&
                    keycodeExtra != null &&
                    keycodeExtra.action == KeyEvent.ACTION_DOWN
                ) {
                    when (keycodeExtra.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PLAY -> {
                            playerEventRepository.onTrackResume(
                                PlayerDeviceStatTrigger.OS_UI,
                                mediaController
                            )
                        }
                        KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                            playerEventRepository.onTrackPause(
                                PlayerDeviceStatTrigger.OS_UI,
                                mediaController
                            )
                        }
                        KeyEvent.KEYCODE_MEDIA_NEXT,
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            playerEventRepository.onSkipToNextOrPrevious(
                                PlayerDeviceStatTrigger.OS_UI,
                                mediaController
                            )
                        }
                        KeyEvent.KEYCODE_HEADSETHOOK,
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                            playerEventRepository.onTrackPlayPause(
                                PlayerDeviceStatTrigger.OS_UI,
                                mediaController
                            )
                        }
                        KeyEvent.KEYCODE_MEDIA_STOP -> {
                            playerEventRepository.onTrackStop(
                                PlayerDeviceStatTrigger.OS_UI,
                                mediaController
                            )
                        }
                    }
                }
                false
            }
        }
    }

    private fun createMusicPlaybackPreparer(
        exoPlayer: ExoPlayer,
        messagesRepository: MessagesRepository,
        messageMapper: MessageMapper,
        limitInteractor: PlayerLimitInteractor,
        analytics: Analytics?,
        logger: Logger?,
        cache: Cache?
    ): MusicPlaybackPreparer {
        return MusicPlaybackPreparer(
            exoPlayer,
            PlayBackDataSource(messagesRepository, messageMapper),
            MusicMediaSourceFactory(cache),
            limitInteractor,
            analytics,
            logger,
            coroutineContext
        )
    }

    private fun createExoPlayer(
        playerEventRepository: PlayerEventRepository,
        playerStatusRepository: PlayerStatusRepository,
        mediaController: MediaControllerCompat,
        bandwidthMeter: AssistantBandwidthMeter
    ): ExoPlayer {
        val trackSelector = DefaultTrackSelector(this)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val analyticsCollector = AnalyticsCollector(Clock.DEFAULT)
        analyticsCollector.addListener(
            playerEventRepository.PlayerEventsTrackChangedCallback(mediaController)
        )
        analyticsCollector.addListener(
            playerStatusRepository.PlayerStatusTrackChangedCallback(mediaController)
        )
        analyticsCollector.addListener(EventLogger(trackSelector))

        return SimpleExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setBandwidthMeter(bandwidthMeter)
            .setAnalyticsCollector(analyticsCollector)
            .build()
    }
}