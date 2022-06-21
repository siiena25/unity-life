package ru.mail.search.assistant.commands.main

import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.entities.ServerCommand

interface ExternalCommandFactory {

    fun provideCommandData(
        queueType: QueueType,
        serverCommand: ServerCommand.ExternalUserDefined
    ): ExecutableCommandData?
}