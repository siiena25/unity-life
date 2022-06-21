package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal class SuggestPayload(
    @SerializedName("text") val title: String?,
    @SerializedName("event") val event: String?,
    @SerializedName("payload") val payload: String?,
    @SerializedName("callbackData") val callbackData: String?
)