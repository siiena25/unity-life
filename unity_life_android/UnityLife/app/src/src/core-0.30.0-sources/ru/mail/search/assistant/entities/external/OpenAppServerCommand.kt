package ru.mail.search.assistant.entities.external

import ru.mail.search.assistant.data.remote.dto.external.Intents
import ru.mail.search.assistant.entities.ServerCommand

data class OpenAppServerCommand(
    val appId: String,
    val path: String?,
    val fallbackUrl: String,
    val intents: List<Intents>
) : ServerCommand.ExternalUserDefined()