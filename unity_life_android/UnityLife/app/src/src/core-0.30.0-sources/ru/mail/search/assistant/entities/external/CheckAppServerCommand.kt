package ru.mail.search.assistant.entities.external

import ru.mail.search.assistant.data.remote.dto.external.Intents
import ru.mail.search.assistant.entities.ServerCommand

data class CheckAppServerCommand(
    val appName: String,
    val responseYes: String,
    val responseNo: String,
    val intents: List<Intents>
) : ServerCommand.ExternalUserDefined()