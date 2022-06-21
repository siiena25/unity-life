package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.*
import ru.mail.search.assistant.entities.ServerCommand

internal class PlaylistParser(
    private val audioTrackParser: AudioTrackParser,
    private val radioParser: RadioParser,
    private val noiseParser: NoiseParser,
    private val taleParser: TaleParser
) {

    companion object {

        const val MEDIA_TYPE = "media"
        const val MEDIA_TYPE_MUSIC = 1
        const val MEDIA_TYPE_RADIO = 2
        const val MEDIA_TYPE_NOISE = 3
        const val MEDIA_TYPE_TALE = 4
        const val MEDIA_TYPE_PODCAST = 5
    }

    fun parse(jsonObject: JsonObject): ServerCommand {
        val tracksType = jsonObject.getString("tracks_type")
        if (tracksType != MEDIA_TYPE) {
            throw ResultParsingException("Unsupported tracks type: $tracksType")
        }
        val autoPlay = jsonObject.getBoolean("autoplay", true)
        val trackNumber = jsonObject.getInt("seek_track", 0)
        val trackPosition = jsonObject.getFloat("seek_second", 0f)
        return when (jsonObject.getInt("media_type", -1)) {
            MEDIA_TYPE_MUSIC -> {
                val playlist = parseTrack(jsonObject, audioTrackParser::parse)
                ServerCommand.PlaylistMusic(playlist, autoPlay, trackNumber, trackPosition)
            }
            MEDIA_TYPE_RADIO -> {
                val playlist = parseTrack(jsonObject, radioParser::parse)
                ServerCommand.PlaylistRadio(playlist, autoPlay, trackNumber)
            }
            MEDIA_TYPE_NOISE -> {
                val playlist = parseTrack(jsonObject, noiseParser::parse)
                ServerCommand.PlaylistNoise(playlist, autoPlay, trackNumber, trackPosition)
            }
            MEDIA_TYPE_TALE -> {
                val playlist = parseTrack(jsonObject, taleParser::parse)
                ServerCommand.PlaylistTale(playlist, autoPlay, trackNumber, trackPosition)
            }
            MEDIA_TYPE_PODCAST -> {
                val playlist = parseTrack(jsonObject, audioTrackParser::parse)
                ServerCommand.PlaylistPodcast(playlist, autoPlay, trackNumber, trackPosition)
            }
            else -> {
                val playlist = parseTrack(jsonObject, taleParser::parse)
                ServerCommand.PlaylistTale(playlist, autoPlay, trackNumber, trackPosition)
            }
        }
    }

    private inline fun <T> parseTrack(jsonObject: JsonObject, parser: (JsonObject) -> T): List<T> {
        return jsonObject.getArray("tracks")?.map { jsonElement ->
            val trackJson = jsonElement.toObject()
                ?: throw ResultParsingException("Wrong track json format")
            parser(trackJson)
        }
            ?.takeIf { playlist -> playlist.isNotEmpty() }
            ?: throw ResultParsingException("Empty playlist")
    }
}