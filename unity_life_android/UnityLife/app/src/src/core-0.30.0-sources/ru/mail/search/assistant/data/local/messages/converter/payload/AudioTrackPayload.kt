package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AudioTrackPayload(
    @SerializedName("id")
    val id: Long,
    @SerializedName("artist_name")
    val artistName: String,
    @SerializedName("track_name")
    val trackName: String,
    @SerializedName("cover_url")
    val coverUrl: String,
    @SerializedName("seek")
    val seek: Float?,
    @SerializedName("url")
    val url: String,
    @SerializedName("audio_source")
    val audioSource: AudioSourcePayload?,
    @SerializedName("kws_skip_intervals")
    val kwsSkipIntervals: List<KwsSkipIntervalPayload>?,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("hq")
    val isHq: Boolean = false,
    @SerializedName("playbackLimit")
    val playbackLimit: PlaybackLimitPayload? = null,

    @Deprecated("moved to AudioSource")
    @Expose(serialize = false, deserialize = true)
    @SerializedName("source_type")
    val sourceType: String? = null,

    @SerializedName("stat_flags")
    val statFlags: String?
)

data class PlaybackLimitPayload(
    @SerializedName("type") val type: String?,
    @SerializedName("count") val count: Long?
)