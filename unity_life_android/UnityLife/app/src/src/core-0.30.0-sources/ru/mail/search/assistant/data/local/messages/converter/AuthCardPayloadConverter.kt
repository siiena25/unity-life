package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.auth.common.domain.model.AuthCompleteAction
import ru.mail.search.assistant.auth.common.domain.model.AuthContext
import ru.mail.search.assistant.auth.common.domain.model.AuthType
import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.AuthCardPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class AuthCardPayloadConverter :
    PayloadGsonConverter<MessageData.AuthorizeCard, AuthCardPayload>() {

    override val type: String get() = MessageTypes.AUTHORIZATION_CARD

    override fun payloadToPojo(payload: String): MessageData.AuthorizeCard {
        return fromJson<AuthCardPayload>(payload) {
            val authType: AuthType? = when (authType) {
                "vk" -> AuthType.VK
                "mail" -> AuthType.MAIL
                "registration" -> AuthType.REGISTRATION
                else -> null
            }
            val context = AuthContext(
                authType = authType,
                completeAction = idToCompleteAction(completeAction),
                repeatText = repeatText
            )
            MessageData.AuthorizeCard(text, context)
        }
    }

    override fun pojoToPayload(data: MessageData.AuthorizeCard): String {
        return toJson(data) {
            val authContext = data.authContext
            val authTypeString = when (authContext.authType) {
                AuthType.VK -> "vk"
                AuthType.MAIL -> "mail"
                AuthType.REGISTRATION -> "registration"
                else -> null
            }

            AuthCardPayload(
                text = text,
                authType = authTypeString,
                completeAction = authContext.completeAction.id,
                repeatText = authContext.repeatText
            )
        }
    }

    private fun idToCompleteAction(actionId: String?): AuthCompleteAction {
        return actionId
            ?.let { AuthCompleteAction.values().find { action -> action.id == actionId } }
            ?: AuthCompleteAction.SEND_TEXT
    }
}