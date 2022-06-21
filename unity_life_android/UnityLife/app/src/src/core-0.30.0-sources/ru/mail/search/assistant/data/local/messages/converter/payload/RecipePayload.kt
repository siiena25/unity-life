package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class RecipePayload(
    @SerializedName("title")
    val title: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("url_short")
    val urlShort: String?,
    @SerializedName("image_url")
    val imageUrl: String?
)