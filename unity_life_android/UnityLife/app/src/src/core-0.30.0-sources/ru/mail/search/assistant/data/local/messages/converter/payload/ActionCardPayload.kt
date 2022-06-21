package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class ActionCardPayload(
    @SerializedName("text")
    val text: String,
    @SerializedName("tag")
    val tag: String,
    @SerializedName("link_label")
    val linkLabel: String?,
    @SerializedName("link_url")
    val linkUrl: String
)