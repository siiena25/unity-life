package ru.mail.search.assistant.data.local.messages.converter.payload.photo

import com.google.gson.annotations.SerializedName

data class ThumbnailPayload(
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int?,
    @SerializedName("height")
    val height: Int?
)