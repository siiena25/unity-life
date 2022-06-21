package ru.mail.search.assistant.commands.main.skipkws

import androidx.annotation.MainThread
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.audio.KwsSkipInterval
import ru.mail.search.assistant.entities.message.MessageData

internal class MusicKwsSkipRepository(
    private val messagesRepository: MessagesRepository,
    private val poolDispatcher: PoolDispatcher
) {

    private var messageId: Long? = null
    private var messageData: MessageData.Player? = null
    private var trackIndex: Int? = null
    private var intervals: List<KwsSkipInterval>? = null

    @MainThread
    suspend fun getSkipKwsIntervals(messageId: Long, trackIndex: Int): List<KwsSkipInterval>? {
        if (this.messageId != messageId) {
            this.messageId = null
            this.trackIndex = null
            messageData = getPlayerMessage(messageId)
            this.messageId = messageId
            intervals = null
        }
        if (this.trackIndex != trackIndex) {
            intervals = messageData?.let { data -> getTrackIntervals(data, trackIndex) }
            this.trackIndex = trackIndex
        }
        return intervals
    }

    private suspend fun getPlayerMessage(messageId: Long): MessageData.Player? {
        return withContext(poolDispatcher.io) {
            runCatching { messagesRepository.getMessage(messageId)?.data as? MessageData.Player }
                .getOrNull()
        }
    }

    private fun getTrackIntervals(
        messageData: MessageData.Player,
        trackIndex: Int
    ): List<KwsSkipInterval>? {
        return when (val data = messageData) {
            is MessageData.Player.TracksMsg ->
                data.playlist.getOrNull(trackIndex)?.kwsSkipIntervals
            is MessageData.Player.RadioMsg ->
                data.playlist.getOrNull(trackIndex)?.kwsSkipIntervals
            is MessageData.Player.TaleMsg ->
                data.playlist.getOrNull(trackIndex)?.kwsSkipIntervals
            is MessageData.Player.SoundMsg ->
                data.playlist.getOrNull(trackIndex)?.kwsSkipIntervals
            is MessageData.Player.PodcastMsg ->
                data.podcast.kwsSkipIntervals
            is MessageData.Player.PodcastsMsg ->
                data.playlist.getOrNull(trackIndex)?.kwsSkipIntervals
        }
    }
}