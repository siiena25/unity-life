package ru.mail.search.assistant.data.remote.dto.dashboard

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

internal data class DashboardItemsDto(
    @SerializedName("greeting")
    val greeting: DashboardGreetingDto?,
    @SerializedName("weather")
    val weather: DashboardWeatherDto?,
    @SerializedName("currencies")
    val currencies: DashboardCurrenciesDto?,
    @SerializedName("shortcuts")
    val shortcuts: List<JsonObject>?
)