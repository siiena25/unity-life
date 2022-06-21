package ru.mail.search.assistant.entities.external

import ru.mail.search.assistant.entities.ServerCommand

data class SetAlarmServerCommand(
    val timestamp: Long
) : ServerCommand.ExternalUserDefined()
