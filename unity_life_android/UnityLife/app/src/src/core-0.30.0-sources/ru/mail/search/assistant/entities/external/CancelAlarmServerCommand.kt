package ru.mail.search.assistant.entities.external

import ru.mail.search.assistant.entities.ServerCommand

data class CancelAlarmServerCommand(
    val timestamp: Long
) : ServerCommand.ExternalUserDefined()
