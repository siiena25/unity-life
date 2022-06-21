package ru.mail.search.assistant.data.local.messages.converter.payload.mailru

import com.google.gson.annotations.SerializedName

internal class MailPayload(
    @SerializedName("type")
    val type: Int,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("sender_name")
    val senderName: String?,
    @SerializedName("subject")
    val subject: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("has_attach")
    val hasAttach: Boolean,
    @SerializedName("event")
    val event: String?,
    @SerializedName("payload")
    val payload: String?,
    @SerializedName("date")
    val date: Long
)