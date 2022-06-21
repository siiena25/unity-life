package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class TaleMsgPayload(
    @SerializedName("title")
    val title: String,
    @SerializedName("cover_url")
    val coverUrl: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("audio_source")
    val audioSource: AudioSourcePayload?,
    @SerializedName("seek")
    val seek: Float?,
    @SerializedName("kws_skip_intervals")
    val kwsSkipIntervals: List<KwsSkipIntervalPayload>?
)