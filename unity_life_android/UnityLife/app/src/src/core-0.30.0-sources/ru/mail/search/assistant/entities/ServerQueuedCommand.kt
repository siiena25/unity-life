package ru.mail.search.assistant.entities

import ru.mail.search.assistant.commands.processor.QueueType

data class ServerQueuedCommand(
    val queueType: QueueType,
    val command: ServerCommand
)
