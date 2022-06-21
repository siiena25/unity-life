package ru.mail.search.assistant.data.local.messages.converter.payload.photo

import com.google.gson.annotations.SerializedName

data class ImageDataPayload(
    @SerializedName("image")
    val image: ImagePayload?,
    @SerializedName("thumbnail")
    val thumbnail: ThumbnailPayload?
)