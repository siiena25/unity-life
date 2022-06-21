package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class TracksMsgPayload(
    @SerializedName("tracks")
    val tracks: List<AudioTrackPayload>
)