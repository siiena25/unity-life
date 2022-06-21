package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.EventListItemPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.EventListMsgPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.SuggestPayload
import ru.mail.search.assistant.api.suggests.SkillIcons
import ru.mail.search.assistant.api.suggests.Suggest
import ru.mail.search.assistant.entities.message.EventItemDescription
import ru.mail.search.assistant.entities.message.MessageData

internal class EventListMsgPayloadConverter :
    PayloadGsonConverter<MessageData.EventListCard, EventListMsgPayload>() {
    override val type: String
        get() = MessageTypes.EVENT_LIST_CARD

    override fun payloadToPojo(payload: String): MessageData.EventListCard {
        return fromJson<EventListMsgPayload>(payload) {
            MessageData.EventListCard(items = this.items.map {
                EventItemDescription(
                    title = it.title,
                    subtitle = it.subtitle,
                    suggest = it.suggest?.let { suggest ->
                        if (it.suggest.payload?.isNotEmpty() == true)
                            Suggest.Text(
                                it.suggest.title,
                                it.suggest.payload,
                                it.suggest.callbackData
                            ) else
                            Suggest.Event(
                                it.suggest.title,
                                it.suggest.event!!,
                                it.suggest.callbackData
                            )
                    },
                    icon = it.icon,
                    icons = SkillIcons(it.iconLight, it.iconDark)
                )
            })
        }
    }

    override fun pojoToPayload(data: MessageData.EventListCard): String {
        return toJson(data) {
            EventListMsgPayload(items = this.items.map {
                EventListItemPayload(
                    title = it.title,
                    subtitle = it.subtitle,
                    icon = it.icon,
                    iconLight = it.icons.light,
                    iconDark = it.icons.dark,
                    suggest = when (it.suggest) {
                        is Suggest.Text -> SuggestPayload(
                            it.suggest.text,
                            null,
                            it.suggest.payload,
                            it.suggest.callbackData
                        )
                        is Suggest.Event -> SuggestPayload(
                            it.suggest.text,
                            it.suggest.event,
                            null,
                            it.suggest.callbackData
                        )
                        else -> null
                    }
                )
            })
        }
    }
}