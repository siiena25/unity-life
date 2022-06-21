package ru.mail.search.assistant.services.music

import android.support.v4.media.MediaMetadataCompat
import ru.mail.search.assistant.entities.audio.AudioSource
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.services.music.extension.*
import java.util.concurrent.TimeUnit

internal class MessageMapper {

    private companion object {

        private const val PATH_DIVIDER = '/'
        private const val UNLIMITED = -1L
        private const val LIMIT_TYPE_VK = "vk"
        private val DEFAULT_LIMIT = TimeUnit.MINUTES.toMillis(26) + TimeUnit.SECONDS.toMillis(40)
    }

    fun map(message: DialogMessage): List<MediaMetadataCompat> {
        return when (val data = message.data) {
            is MessageData.Player.TracksMsg ->
                mapTracksData(message.id, message.phraseId, data)
            is MessageData.Player.RadioMsg ->
                mapRadioMessage(message.id, message.phraseId, data)
            is MessageData.Player.TaleMsg ->
                mapTaleMessage(message.id, message.phraseId, data)
            is MessageData.Player.SoundMsg ->
                mapSoundMessage(message.id, message.phraseId, data)
            is MessageData.Player.PodcastMsg ->
                mapPodcastMessage(message.id, message.phraseId, data)
            is MessageData.Player.PodcastsMsg ->
                mapPodcastsData(message.id, message.phraseId, data)
            else ->
                throw IllegalStateException("Unsupported message data")
        }
    }

    private fun mapRadioMessage(
        messageId: Long,
        phraseId: String,
        data: MessageData.Player.RadioMsg
    ): List<MediaMetadataCompat> {
        return data.playlist.mapIndexed { index, track ->
            buildMetadata(
                mediaUri = track.url,
                messageId = messageId,
                displayTitle = track.title.let { if (it.isNotBlank()) it else track.artist },
                index = index,
                trackCount = data.playlist.size,
                audioSource = track.audioSource
            ) {
                setDisplaySubtitle(if (track.title.isNotBlank()) track.artist else "")
                setDisplayIconUri(track.coverUrl)
                setPhraseId(phraseId)
                setArtist(track.artist)
            }
        }
    }

    private fun mapTracksData(
        messageId: Long,
        phraseId: String,
        data: MessageData.Player.TracksMsg
    ): List<MediaMetadataCompat> {
        return data.playlist.mapIndexed { index, track ->
            val playbackLimit = track.playbackLimit
            val limit = if (playbackLimit != null && playbackLimit.type == LIMIT_TYPE_VK) {
                playbackLimit.limit?.let { limitS -> TimeUnit.SECONDS.toMillis(limitS) }
                    ?: DEFAULT_LIMIT
            } else {
                UNLIMITED
            }
            buildMetadata(
                mediaUri = track.url,
                messageId = messageId,
                displayTitle = track.trackName,
                index = index,
                trackCount = data.playlist.size,
                limitMs = limit,
                isMusic = true,
                isVk = isVk(track.audioSource),
                audioSource = track.audioSource
            ) {
                setDisplaySubtitle(track.artistName)
                setDisplayIconUri(track.coverUrl)
                setArtist(track.artistName)
                setAlbumArtist(track.artistName)
                setDuration(track.duration)
                setTrackId(track.id)
                setPhraseId(phraseId)
                if (!track.statFlags.isNullOrBlank()) {
                    setStatFlags(track.statFlags)
                }
                setSource(track.audioSource?.source)
            }
        }
    }

    private fun mapPodcastsData(
        messageId: Long,
        phraseId: String,
        data: MessageData.Player.PodcastsMsg
    ): List<MediaMetadataCompat> {
        return data.playlist.mapIndexed { index, track ->
            buildMetadata(
                mediaUri = track.url,
                messageId = messageId,
                displayTitle = track.trackName,
                index = index,
                trackCount = data.playlist.size,
                isVk = isVk(track.audioSource),
                audioSource = track.audioSource
            ) {
                setDisplaySubtitle(track.artistName)
                setDisplayIconUri(track.coverUrl)
                setArtist(track.artistName)
                setAlbumArtist(track.artistName)
                setDuration(track.duration)
                setPhraseId(phraseId)
            }
        }
    }

    private fun mapTaleMessage(
        messageId: Long,
        phraseId: String,
        data: MessageData.Player.TaleMsg
    ): List<MediaMetadataCompat> {
        return data.playlist.mapIndexed { index, track ->
            buildMetadata(
                mediaUri = track.url,
                messageId = messageId,
                displayTitle = track.title,
                index = index,
                trackCount = data.playlist.size,
                audioSource = track.audioSource
            ) {
                setDisplayIconUri(track.coverUrl)
                setPhraseId(phraseId)
            }
        }
    }

    private fun mapSoundMessage(
        messageId: Long,
        phraseId: String,
        data: MessageData.Player.SoundMsg
    ): List<MediaMetadataCompat> {
        return data.playlist.mapIndexed { index, track ->
            buildMetadata(
                mediaUri = track.url,
                messageId = messageId,
                displayTitle = track.name,
                index = index,
                trackCount = data.playlist.size,
                audioSource = track.audioSource
            ) {
                setPhraseId(phraseId)
            }
        }
    }

    private fun mapPodcastMessage(
        messageId: Long,
        phraseId: String,
        data: MessageData.Player.PodcastMsg
    ): List<MediaMetadataCompat> {
        return listOf(
            buildMetadata(
                mediaUri = data.podcast.url,
                messageId = messageId,
                displayTitle = data.podcast.title,
                index = 0,
                trackCount = 1,
                audioSource = data.podcast.audioSource
            ) {
                setDisplayIconUri(data.podcast.coverUrl)
                setPhraseId(phraseId)
            }
        )
    }

    private fun buildMetadata(
        mediaUri: String,
        messageId: Long,
        displayTitle: String,
        index: Int,
        trackCount: Int,
        audioSource: AudioSource?,
        limitMs: Long = UNLIMITED,
        isMusic: Boolean = false,
        isVk: Boolean = false,
        optional: (MediaMetadataCompat.Builder.() -> Unit)? = null
    ): MediaMetadataCompat {
        val builder = MediaMetadataCompat.Builder()
            .setMediaId("$messageId$PATH_DIVIDER$index")
            .setMediaUri(mediaUri)
            .setMessageId(messageId)
            .setDisplayTitle(displayTitle)
            .setTrackIndex(index)
            .setTrackNumber(index + 1)
            .setTrackCount(trackCount)
            .setLimit(limitMs)
            .setIsMusic(isMusic)
            .setIsVk(isVk)
        audioSource?.let { builder.setAudioSource(audioSource) }
        optional?.let { builder.optional() }
        return builder.build()
    }

    private fun isVk(audioSource: AudioSource?): Boolean {
        return audioSource?.isVk ?: false
    }
}