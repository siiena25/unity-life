package ru.mail.search.assistant.data.local.messages.converter.payload.mailru

import com.google.gson.annotations.SerializedName

internal data class LetterPayload(
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
    @SerializedName("date")
    val date: Long
)