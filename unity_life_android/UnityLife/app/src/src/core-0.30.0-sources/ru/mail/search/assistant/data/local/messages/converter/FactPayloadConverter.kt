package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.AppRefPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.FactPayload
import ru.mail.search.assistant.entities.message.AppRef
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.apprefs.AppRefsSpan

internal class FactPayloadConverter : PayloadGsonConverter<MessageData.Fact, FactPayload>() {

    override val type: String get() = MessageTypes.FACT

    override fun payloadToPojo(payload: String): MessageData.Fact {
        return fromJson<FactPayload>(payload) {
            MessageData.Fact(
                title,
                text,
                fullText,
                link,
                linkTitle,
                imageUrl,
                searchUrl,
                searchTitle,
                appRefs?.map { appRef ->
                    AppRefsSpan(
                        AppRef(appRef.app, appRef.market, appRef.name),
                        appRef.start,
                        appRef.end
                    )
                }.orEmpty()
            )
        }
    }

    override fun pojoToPayload(data: MessageData.Fact): String {
        return toJson(data) {
            FactPayload(
                title,
                text,
                fullText,
                link,
                linkTitle,
                imageUrl,
                searchUrl,
                searchTitle,
                appRefs.map { appRef ->
                    AppRefPayload(
                        appRef.payload.app,
                        appRef.payload.market,
                        appRef.payload.name,
                        appRef.start,
                        appRef.end
                    )
                }
            )
        }
    }
}