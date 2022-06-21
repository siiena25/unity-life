package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class RecommendationsCardPayload(
    @SerializedName("text")
    val text: String,
    @SerializedName("link_label")
    val linkLabel: String?
)