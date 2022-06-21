package ru.mail.search.assistant.entities

import ru.mail.search.assistant.api.phrase.ActivationType

sealed class InputMethod {

    object Text : InputMethod()

    data class Voice(
        val startedManually: Boolean = true,
        val minWaitingTime: Int? = null,
        val muteActivationSound: Boolean = false,
        val activationType: ActivationType? = null,
        val callbackData: String? = null
    ) : InputMethod()

    data class FlowMode(
        val flowModeModel: String
    ) : InputMethod()
}