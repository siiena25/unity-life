package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.SliderItemPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.SliderPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.SliderItem

internal class SliderPayloadConverter :
    PayloadGsonConverter<MessageData.Slider, SliderPayload>() {

    override val type: String get() = MessageTypes.SLIDER

    override fun payloadToPojo(payload: String): MessageData.Slider {
        return fromJson<SliderPayload>(payload) {
            MessageData.Slider(
                items = items.map { card ->
                    SliderItem(
                        title = card.title,
                        subtitle = card.subtitle,
                        imageUrl = card.imageUrl,
                        url = card.url,
                        eventType = card.eventType,
                        searchQueryText = card.searchQueryText,
                        searchQueryParam = card.searchQueryParam,
                        callbackData = card.callbackData
                    )
                },
                extensionItem = extensionItem?.let { card ->
                    SliderItem(
                        title = card.title,
                        subtitle = card.subtitle,
                        imageUrl = card.imageUrl,
                        url = card.url,
                        eventType = card.eventType,
                        searchQueryText = card.searchQueryText,
                        searchQueryParam = card.searchQueryParam,
                        callbackData = card.callbackData
                    )
                }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.Slider): String {
        return toJson(data) {
            SliderPayload(
                items = items.map { card ->
                    SliderItemPayload(
                        title = card.title,
                        subtitle = card.subtitle,
                        imageUrl = card.imageUrl,
                        url = card.url,
                        eventType = card.eventType,
                        searchQueryText = card.searchQueryText,
                        searchQueryParam = card.searchQueryParam,
                        callbackData = card.callbackData
                    )
                },
                extensionItem = extensionItem?.let { card ->
                    SliderItemPayload(
                        title = card.title,
                        subtitle = card.subtitle,
                        imageUrl = card.imageUrl,
                        url = card.url,
                        eventType = card.eventType,
                        searchQueryText = card.searchQueryText,
                        searchQueryParam = card.searchQueryParam,
                        callbackData = card.callbackData
                    )
                }
            )
        }
    }
}