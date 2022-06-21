package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherCardPeriodsDto(
    @SerializedName("morning") val morning: WeatherCardPeriodDto?,
    @SerializedName("daytime") val daytime: WeatherCardPeriodDto?,
    @SerializedName("night") val night: WeatherCardPeriodDto?
)