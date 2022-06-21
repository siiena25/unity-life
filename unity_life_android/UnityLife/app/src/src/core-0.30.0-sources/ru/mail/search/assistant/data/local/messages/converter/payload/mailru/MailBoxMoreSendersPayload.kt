package ru.mail.search.assistant.data.local.messages.converter.payload.mailru

import com.google.gson.annotations.SerializedName

internal data class MailBoxMoreSendersPayload(
    @SerializedName("event")
    val event: String?,
    @SerializedName("text")
    val text: String,
    @SerializedName("payload")
    val payload: String
)