package ru.mail.search.assistant.services.music

import android.support.v4.media.MediaBrowserCompat
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.message.DialogMessage

internal class MediaBrowserDataSource(
    private val repository: MessagesRepository,
    private val mapper: MessageMapper
) {

    fun loadChildrenByParentId(parentId: String): List<MediaBrowserCompat.MediaItem>? {
        return repository.getMessage(parentId.toLong())?.let { mapMessage(it) }
    }

    private fun mapMessage(message: DialogMessage): MutableList<MediaBrowserCompat.MediaItem> {
        val result = ArrayList<MediaBrowserCompat.MediaItem>()
        mapper.map(message).forEach { metadata ->
            val item = MediaBrowserCompat.MediaItem(
                metadata.description,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
            result.add(item)
        }
        return result
    }
}