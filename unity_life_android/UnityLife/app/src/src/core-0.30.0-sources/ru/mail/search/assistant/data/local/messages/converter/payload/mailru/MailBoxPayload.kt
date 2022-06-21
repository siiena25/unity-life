package ru.mail.search.assistant.data.local.messages.converter.payload.mailru

import com.google.gson.annotations.SerializedName

internal data class MailBoxPayload(
    @SerializedName("header")
    val header: String?,
    @SerializedName("message_count")
    val messageCount: Int,
    @SerializedName("user_email")
    val userEmail: String?,
    @SerializedName("items")
    val senders: List<SenderPayload>,
    @SerializedName("more_senders")
    val moreSenders: MailBoxMoreSendersPayload?
)