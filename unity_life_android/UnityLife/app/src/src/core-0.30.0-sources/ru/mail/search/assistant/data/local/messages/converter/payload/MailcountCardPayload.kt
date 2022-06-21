package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

@Deprecated("Backward compatibility, use ActionCardPayload")
data class MailcountCardPayload(
    @SerializedName("text")
    val text: String,
    @SerializedName("link_label")
    val linkLabel: String?,
    @SerializedName("link_url")
    val linkUrl: String
)