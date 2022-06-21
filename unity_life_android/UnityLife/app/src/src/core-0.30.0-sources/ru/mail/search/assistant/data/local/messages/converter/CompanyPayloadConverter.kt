package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.CompanyPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class CompanyPayloadConverter : PayloadGsonConverter<MessageData.CompanyCard, CompanyPayload>() {
    override val type: String get() = MessageTypes.COMPANY_CARD


    override fun payloadToPojo(payload: String): MessageData.CompanyCard {
        return fromJson<CompanyPayload>(payload) {
            MessageData.CompanyCard(
                title = title,
                address = address,
                phones = phones,
                imageUrl = imageUrl,
                schedule = schedule,
                metro = metro,
                mapUrl = mapUrl,
                routeUrl = routeUrl,
                siteUrl = siteUrl,
                category = category,
                description = description,
                distance = distance,
                rating = rating
            )
        }
    }

    override fun pojoToPayload(data: MessageData.CompanyCard): String {
        return toJson(data) {
            CompanyPayload(
                title = title,
                address = address,
                phones = phones,
                imageUrl = imageUrl,
                schedule = schedule,
                metro = metro,
                mapUrl = mapUrl,
                routeUrl = routeUrl,
                siteUrl = siteUrl,
                category = category,
                description = description,
                distance = distance,
                rating = rating
            )
        }
    }
}