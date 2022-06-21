package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal class SoundPlaylistMsgPayload(
    @SerializedName("playlist")
    val playlist: List<SoundMsgPayload>
)