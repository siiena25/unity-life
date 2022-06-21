package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class AppRefPayload(
    @SerializedName("app") val app: String,
    @SerializedName("market") val market: String,
    @SerializedName("name") val name: String,
    @SerializedName("start") val start: Int,
    @SerializedName("end") val end: Int
)