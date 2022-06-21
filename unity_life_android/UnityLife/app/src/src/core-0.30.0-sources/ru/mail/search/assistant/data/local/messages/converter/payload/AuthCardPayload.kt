package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class AuthCardPayload(
    @SerializedName("text")
    val text: String,
    @SerializedName("link_label")
    val authType: String?,
    @SerializedName("complete_action")
    val completeAction: String?,
    @SerializedName("repeat_text")
    val repeatText: String?
)