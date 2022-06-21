package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class KwsSkipIntervalPayload(
    @SerializedName("start")
    val start: Long,
    @SerializedName("end")
    val end: Long
)