package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.*
import ru.mail.search.assistant.entities.audio.AudioTrack
import ru.mail.search.assistant.entities.audio.PlaybackLimit
import java.util.concurrent.TimeUnit

internal class AudioTrackParser(
    private val kwsSkipIntervalsParser: KwsSkipIntervalsParser,
    private val audioSourceParser: AudioSourceParser
) {

    fun parse(jsonObject: JsonObject): AudioTrack {
        val meta = jsonObject.getObject("meta")
        val playbackLimit = meta?.getObject("limit")?.let { limit ->
            PlaybackLimit(
                type = limit.getString("type"),
                limit = limit.getLong("count")
            )
        }
        return parseTrack(meta, jsonObject, playbackLimit)
    }

    private fun parseTrack(
        meta: JsonObject?,
        commandJson: JsonObject,
        playbackLimit: PlaybackLimit?
    ): AudioTrack {
        return AudioTrack(
            id = meta?.getLong("id", -1L) ?: -1,
            artistName = meta?.getString("artist").orEmpty(),
            trackName = meta?.getString("title").orEmpty(),
            coverUrl = parseCoverImage(meta),
            url = commandJson.getString("url")
                ?: throw ResultParsingException("Failed to parse audio track: missing url"),
            audioSource = meta?.let(audioSourceParser::parse),
            duration = TimeUnit.SECONDS.toMillis(
                meta?.getLong("duration", 0) ?: 0
            ),
            isHq = meta?.getBoolean("is_hq", false) ?: false,
            playbackLimit = playbackLimit,
            seek = meta?.getFloat("seek"),
            statFlags = meta?.getString("stat_flags"),
            kwsSkipIntervals = kwsSkipIntervalsParser.parse(commandJson)
        )
    }

    private fun parseCoverImage(meta: JsonObject?): String {
        return meta
            ?.getObject("album")
            ?.getObject("thumb")
            ?.getString("photo_135")
            ?: parseCoverUrl(meta).orEmpty()
    }

    private fun parseCoverUrl(meta: JsonObject?): String? {
        return meta?.getString("coverUrl")
    }
}