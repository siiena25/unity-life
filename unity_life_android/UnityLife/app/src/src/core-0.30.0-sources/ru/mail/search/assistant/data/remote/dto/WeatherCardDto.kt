package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherCardDto(
    @SerializedName("temperature") val temperature: Int?,
    @SerializedName("city") val city: String?,
    @SerializedName("city_str") val cityFormed: String?,
    @SerializedName("date_str") val dateFormed: String?,
    @SerializedName("pressure") val pressure: Int?,
    @SerializedName("wind_speed") val windSpeed: Int?,
    @SerializedName("humidity") val humidity: Int?,
    @SerializedName("icon_type") val iconType: String?,
    @SerializedName("icon_url") val iconUrl: String?,
    @SerializedName("periods") val periods: WeatherCardPeriodsDto?,
    @SerializedName("url") val url: String?
)