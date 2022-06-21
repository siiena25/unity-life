package ru.mail.search.assistant.commands.main.skipkws

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.MainThread
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ru.mail.search.assistant.commands.intent.AssistantIntent
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.event.AssistantEvent
import ru.mail.search.assistant.interactor.PendingIntentsInteractor
import ru.mail.search.assistant.services.music.extension.audioSource
import ru.mail.search.assistant.services.music.extension.isPrepared
import ru.mail.search.assistant.services.music.extension.mediaId
import ru.mail.search.assistant.util.Tag

class MusicPlayerMediaFailureHandler(
    private val pendingIntentsInteractor: PendingIntentsInteractor,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    private companion object {

        private const val MEDIA_FAILURE_INTENT_ID = "media_failure"
    }

    private val coroutineContext = SupervisorJob() + poolDispatcher.main
    private val coroutineScope = CoroutineScope(coroutineContext)

    private var isReleased = false
    private var wasPlaying = false
    private var currentMediaId: String? = null
    private var currentMetadata: MediaMetadataCompat? = null
    private var currentState: PlaybackStateCompat? = null

    @MainThread
    fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        if (isReleased) return
        val mediaId = metadata?.mediaId
        if (currentMediaId != mediaId) {
            if (currentState?.state != PlaybackStateCompat.STATE_PLAYING) {
                wasPlaying = false
            }
            currentMediaId = mediaId
        }
        currentMetadata = metadata
    }

    @MainThread
    fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        if (isReleased) return
        if (currentState?.state != state?.state) {
            if (state != null && state.state == PlaybackStateCompat.STATE_ERROR) {
                onMediaFailure()
            }
            if (currentState?.state == PlaybackStateCompat.STATE_ERROR) {
                dropMediaFailureEvents()
            }
        }
        if (state?.state == PlaybackStateCompat.STATE_PLAYING) {
            wasPlaying = true
        } else if (state == null || !state.isPrepared) {
            wasPlaying = false
        }
        currentState = state
    }

    suspend fun release() {
        withContext(coroutineContext) {
            releaseInternal()
        }
        coroutineContext.cancel()
    }

    @MainThread
    private fun releaseInternal() {
        if (isReleased) return
        dropMediaFailureEvents()
        isReleased = true
    }

    private fun onMediaFailure() {
        currentMetadata?.audioSource
            ?.takeIf { it.isRadio }
            ?.skillName
            ?.let { skillName ->
                val event = if (wasPlaying) {
                    logger?.i(Tag.ASSISTANT_MEDIA, "Media interrupted ($skillName)")
                    AssistantEvent.MEDIA_INTERRUPTED
                } else {
                    logger?.i(Tag.ASSISTANT_MEDIA, "Media start failed ($skillName)")
                    AssistantEvent.MEDIA_START_FAILED
                }
                sendMediaFailureEvent(event, skillName)
            }
    }

    private fun sendMediaFailureEvent(event: String, skillName: String) {
        val clientData = JsonObject()
            .apply { addProperty("origin_skill_name", skillName) }
            .toString()
        val intent = AssistantIntent.SilentEvent(
            event = event,
            clientData = clientData
        )
        sendMediaFailureEvent(intent)
    }

    private fun sendMediaFailureEvent(intent: AssistantIntent) {
        coroutineScope.launch(poolDispatcher.work) {
            runCatching {
                pendingIntentsInteractor.addPendingIntent(intent, MEDIA_FAILURE_INTENT_ID)
            }
                .onFailure { error ->
                    if (error !is CancellationException) {
                        logger?.e(Tag.ASSISTANT_MEDIA, error, "Failed to add pending intent")
                    }
                }
        }
    }

    private fun dropMediaFailureEvents() {
        pendingIntentsInteractor.dropIntentsById(MEDIA_FAILURE_INTENT_ID)
    }
}