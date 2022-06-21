package ru.mail.search.assistant.util

import ru.mail.search.assistant.Assistant
import ru.mail.search.assistant.AssistantCore
import ru.mail.search.assistant.dependencies.CoreModule

internal fun requireCoreModule(): CoreModule {
    return Assistant.getCurrentCore()!!.module
}

@Deprecated("Use DI instead")
fun requireAssistantCore() = Assistant.getCurrentCore()!!

@Deprecated("Use DI instead")
fun AssistantCore.requireAssistantSession() = getCurrentSession()!!