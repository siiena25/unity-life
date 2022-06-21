package ru.mail.search.assistant.commands.external

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.entities.ServerCommand

interface ExternalCommandDataProvider {

    @Deprecated("use provideExecutableCommand instead", ReplaceWith("provideExecutableCommand"))
    fun provideExecutableCommandData(
        serverCommand: ServerCommand.ExternalUserDefined
    ): ExecutableCommandData? {
        return null
    }

    fun provideExecutableCommand(
        serverCommand: ServerCommand.ExternalUserDefined
    ): ExecutableCommand<*>? {
        return provideExecutableCommandData(serverCommand)?.command
    }
}