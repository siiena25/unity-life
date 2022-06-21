package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class SerpItemPayload(
    @SerializedName("title")
    val title: String,
    @SerializedName("text")
    val text: String?,
    @SerializedName("url_short")
    val shortUrl: String,
    @SerializedName("url_full")
    val fullUrl: String
)