package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class PodcastsMsgPayload(
    @SerializedName("podcasts")
    val podcasts: List<AudioTrackPayload>
)