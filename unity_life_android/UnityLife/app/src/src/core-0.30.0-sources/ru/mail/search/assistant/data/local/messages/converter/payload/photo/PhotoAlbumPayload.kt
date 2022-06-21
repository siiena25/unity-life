package ru.mail.search.assistant.data.local.messages.converter.payload.photo

import com.google.gson.annotations.SerializedName

data class PhotoAlbumPayload(
    @SerializedName("image_data")
    val imageData: List<ImageDataPayload>,
    @SerializedName("more_url")
    val morePhotosUrl: String?
)