package ru.mail.search.assistant.data.remote.dto.dashboard

import com.google.gson.annotations.SerializedName

internal data class DashboardWeatherDto(
    @SerializedName("temperature")
    val temperature: Int?,
    @SerializedName("icon_url")
    val iconUrl: String?,
    @SerializedName("conditions")
    val conditions: String?
)