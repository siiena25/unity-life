package ru.mail.search.assistant.util.analytics

import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.getErrorDescription
import ru.mail.search.assistant.util.analytics.event.AssistantError

fun Analytics.logAssistantError(module: AssistantError.Module, error: Throwable) {
    logAssistantError(module, getErrorDescription(error))
}

fun Analytics.logAssistantError(
    module: AssistantError.Module,
    causeFormat: String,
    error: Throwable
) {
    logAssistantError(module, String.format(causeFormat, getErrorDescription(error)))
}

fun Analytics.logAssistantError(module: AssistantError.Module, cause: String) {
    log(AssistantError(module, cause))
}
