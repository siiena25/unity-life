package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class WeatherPeriodPayload(
    @SerializedName("temperature")
    val temperature: Int?,
    @SerializedName("icon_type")
    val iconType: String?,
    @SerializedName("icon_url")
    val iconUrl: String?
)