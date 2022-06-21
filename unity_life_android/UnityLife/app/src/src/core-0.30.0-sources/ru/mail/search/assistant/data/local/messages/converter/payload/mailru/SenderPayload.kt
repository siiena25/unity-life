package ru.mail.search.assistant.data.local.messages.converter.payload.mailru

import com.google.gson.annotations.SerializedName

internal data class SenderPayload(
    @SerializedName("type")
    val type: Int,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("message_count")
    val messageCount: Int,
    @SerializedName("payload")
    val payload: String?,
    @SerializedName("event")
    val event: String?
)