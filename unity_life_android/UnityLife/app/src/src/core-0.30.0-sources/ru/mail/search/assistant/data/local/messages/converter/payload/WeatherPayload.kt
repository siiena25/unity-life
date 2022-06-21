package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class WeatherPayload(
    @SerializedName("temperature")
    val temperature: Int?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("city_formed")
    val cityFormed: String?,
    @SerializedName("date_formed")
    val dateFormed: String?,
    @SerializedName("pressure")
    val pressure: Int?,
    @SerializedName("wind_speed")
    val windSpeed: Int?,
    @SerializedName("humidity")
    val humidity: Int?,
    @SerializedName("icon_type")
    val iconType: String?,
    @SerializedName("icon_url")
    val iconUrl: String?,
    @SerializedName("morning")
    val morning: WeatherPeriodPayload?,
    @SerializedName("daytime")
    val daytime: WeatherPeriodPayload?,
    @SerializedName("night")
    val night: WeatherPeriodPayload?,
    @SerializedName("url")
    val url: String?
)