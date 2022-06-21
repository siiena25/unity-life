package ru.mail.search.assistant.entities.external

import ru.mail.search.assistant.entities.ServerCommand

data class SetTimerServerCommand(
    val timestampDelta: Long
) : ServerCommand.ExternalUserDefined()
