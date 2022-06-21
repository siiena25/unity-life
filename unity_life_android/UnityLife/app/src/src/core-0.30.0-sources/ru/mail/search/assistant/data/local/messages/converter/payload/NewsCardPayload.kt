package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class NewsCardPayload(
    @SerializedName("title")
    val title: String?,
    @SerializedName("news")
    val news: List<NewsPayload>
)