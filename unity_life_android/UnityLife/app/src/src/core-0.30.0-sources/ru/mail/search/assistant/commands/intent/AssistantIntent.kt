package ru.mail.search.assistant.commands.intent

import ru.mail.search.assistant.api.phrase.ActivationType
import ru.mail.search.assistant.entities.message.MessageData

sealed class AssistantIntent {

    sealed class UserInput : AssistantIntent() {

        data class Voice(
            val startedManually: Boolean,
            val minWaitingTime: Int?,
            val activationType: ActivationType? = null,
            val callbackData: String? = null
        ) : UserInput()

        data class FlowMode(
            val flowModeModel: String
        ) : UserInput()

        data class Text(
            val text: String,
            val callbackData: String? = null,
            val clientData: String? = null,
            val showMessage: Boolean = true,
            val silentMode: Boolean = false
        ) : UserInput()

        data class Event(
            val event: String,
            val text: String? = null,
            val callbackData: String? = null,
            val clientData: String? = null
        ) : UserInput()
    }

    data class SilentEvent(
        val event: String,
        val callbackData: String? = null,
        val clientData: String? = null,
        val params: Map<String, String> = emptyMap()
    ) : AssistantIntent()

    data class PushPayload(
        val pushId: String?,
        val callbackData: String
    ) : AssistantIntent()

    class OpenUrl(val url: String) : AssistantIntent()

    data class AddMessage(
        val message: MessageData
    ) : AssistantIntent()

    data class StartApp(
        val isResultIgnored: Boolean = false,
        val isStartAppListenEnabled: Boolean? = null
    ) : AssistantIntent()

    abstract class Custom : AssistantIntent()
}