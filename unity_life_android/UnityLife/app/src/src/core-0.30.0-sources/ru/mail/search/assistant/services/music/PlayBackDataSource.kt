package ru.mail.search.assistant.services.music

import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.services.music.model.PlaybackState

private const val PATH_DIVIDER = '/'

internal class PlayBackDataSource(
    private val messagesRepository: MessagesRepository,
    private val messageMapper: MessageMapper
) {

    fun loadByMediaId(mediaId: String): PlaybackState? {
        val split = mediaId.split(PATH_DIVIDER)
        require(split.size in 1..2) { "Invalid media ID" }
        val messageId = split[0].toLong()
        return messagesRepository.getMessage(messageId)?.let { message ->
            PlaybackState(
                position = split.getOrNull(1)?.toInt() ?: 0,
                playlist = messageMapper.map(message)
            )
        }
    }
}