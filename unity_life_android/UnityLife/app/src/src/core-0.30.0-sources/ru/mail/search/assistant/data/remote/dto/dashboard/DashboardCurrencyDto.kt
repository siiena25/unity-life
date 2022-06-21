package ru.mail.search.assistant.data.remote.dto.dashboard

import com.google.gson.annotations.SerializedName

internal data class DashboardCurrencyDto(
    @SerializedName("code")
    val code: String?,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("rate")
    val rate: Float?,
    @SerializedName("movement")
    val movement: String?
)