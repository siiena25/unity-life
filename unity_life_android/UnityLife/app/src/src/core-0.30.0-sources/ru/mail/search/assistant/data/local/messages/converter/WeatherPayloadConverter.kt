package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.WeatherPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.WeatherPeriodPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.WeatherPeriod

internal class WeatherPayloadConverter : PayloadGsonConverter<MessageData.Weather, WeatherPayload>() {

    override val type: String get() = MessageTypes.WEATHER

    override fun payloadToPojo(payload: String): MessageData.Weather {
        return fromJson<WeatherPayload>(payload) {
            MessageData.Weather(
                temperature,
                city,
                cityFormed,
                dateFormed,
                pressure,
                windSpeed,
                humidity,
                iconType,
                iconUrl,
                morning?.let { period ->
                    WeatherPeriod(
                        period.temperature,
                        period.iconType,
                        period.iconUrl
                    )
                },
                daytime?.let { period ->
                    WeatherPeriod(
                        period.temperature,
                        period.iconType,
                        period.iconUrl
                    )
                },
                night?.let { period ->
                    WeatherPeriod(
                        period.temperature,
                        period.iconType,
                        period.iconUrl
                    )
                },
                url
            )
        }
    }

    override fun pojoToPayload(data: MessageData.Weather): String {
        return toJson(data) {
            WeatherPayload(
                temperature,
                city,
                cityFormed,
                dateFormed,
                pressure,
                windSpeed,
                humidity,
                iconType,
                iconUrl,
                morning?.let { period ->
                    WeatherPeriodPayload(
                        period.temperature,
                        period.iconType,
                        period.iconUrl
                    )
                },
                daytime?.let { period ->
                    WeatherPeriodPayload(
                        period.temperature,
                        period.iconType,
                        period.iconUrl
                    )
                },
                night?.let { period ->
                    WeatherPeriodPayload(
                        period.temperature,
                        period.iconType,
                        period.iconUrl
                    )
                },
                url
            )
        }
    }
}