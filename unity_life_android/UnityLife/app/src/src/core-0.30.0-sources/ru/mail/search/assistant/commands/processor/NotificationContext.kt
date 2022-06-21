package ru.mail.search.assistant.commands.processor

import ru.mail.search.assistant.commands.processor.model.AssistantContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.commands.processor.model.PhraseContext

class NotificationContext(
    val assistant: AssistantContext,
    val phrase: PhraseContext,
    val cause: CommandNotification
)