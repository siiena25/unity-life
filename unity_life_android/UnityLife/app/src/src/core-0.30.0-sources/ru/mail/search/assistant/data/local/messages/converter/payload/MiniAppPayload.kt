package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class MiniAppPayload(
    @SerializedName("title")
    val title: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("url")
    val url: String
)