package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.message.WeatherPeriod

data class WeatherCardPeriodDto(
    @SerializedName("temperature") val temperature: Int?,
    @SerializedName("icon_type") val iconType: String?,
    @SerializedName("icon_url") val iconUrl: String?
) : DataEntity<WeatherPeriod> {
    override fun toDomain() = WeatherPeriod(temperature, iconType, iconUrl)
}