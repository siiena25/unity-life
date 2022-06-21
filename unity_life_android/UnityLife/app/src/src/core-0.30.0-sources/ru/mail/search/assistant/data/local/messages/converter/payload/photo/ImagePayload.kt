package ru.mail.search.assistant.data.local.messages.converter.payload.photo

import com.google.gson.annotations.SerializedName

data class ImagePayload(
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("ext")
    val ext: String?,
    @SerializedName("size")
    val size: Long?
)