package ru.mail.search.assistant.commands.processor

data class ExecutableCommandData(
    val queueType: QueueType,
    val command: ExecutableCommand<*>
)