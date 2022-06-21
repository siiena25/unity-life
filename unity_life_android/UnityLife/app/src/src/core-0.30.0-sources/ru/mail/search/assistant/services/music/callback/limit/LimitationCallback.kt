package ru.mail.search.assistant.services.music.callback.limit

import android.support.v4.media.session.MediaControllerCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mail.search.assistant.api.statistics.devicestat.player.PlayerDeviceStatTrigger
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.isCausedByPoorNetworkConnection
import ru.mail.search.assistant.data.PlayerEventRepository
import ru.mail.search.assistant.interactor.PlayerLimitInteractor
import ru.mail.search.assistant.services.music.callback.BaseMediaControllerCallback
import ru.mail.search.assistant.services.music.extension.limit

internal class LimitationCallback(
    private val mediaController: MediaControllerCompat,
    private val limitInteractor: PlayerLimitInteractor,
    private val playerEventRepository: PlayerEventRepository,
    private val backgroundMusicDataSource: BackgroundMusicDataSource,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?,
) : BaseMediaControllerCallback() {

    private companion object {

        const val TAG = "LimitationCallback"
    }

    private var isPlaying = false
    private var trackLimit = -1L
    private var isLimitationActive = false

    override fun onPlay() {
        isPlaying = true
        checkLimitation()
    }

    override fun onPause() {
        isPlaying = false
        checkLimitation()
    }

    override fun onStop() {
        isPlaying = false
        checkLimitation()
    }

    override fun onMetadataChanged() {
        trackLimit = mediaController.metadata.limit
        checkLimitation()
    }

    private fun checkLimitation() {
        val enableLimit = isPlaying && (trackLimit >= 0)
        limitInteractor.setLimit(trackLimit)
        if (isLimitationActive == enableLimit) return
        isLimitationActive = enableLimit
        if (enableLimit) {
            limitInteractor.onStartPlaying {
                if (isPlaying) {
                    mediaController.transportControls.pause()
                    playerEventRepository.onTrackPause(
                        PlayerDeviceStatTrigger.PLAYBACK_LIMIT,
                        mediaController
                    )
                    sendLimitReachedEvent()
                }
            }
        } else {
            limitInteractor.onStopPlaying()
        }
    }

    private fun sendLimitReachedEvent() {
        GlobalScope.launch(poolDispatcher.io) {
            runCatching {
                backgroundMusicDataSource.sendLimitReachedEvent()
            }.onFailure { error ->
                if (error !is CancellationException && !error.isCausedByPoorNetworkConnection()) {
                    logger?.e(TAG, error, "Failed to send music limit reached event")
                }
            }
        }
    }
}