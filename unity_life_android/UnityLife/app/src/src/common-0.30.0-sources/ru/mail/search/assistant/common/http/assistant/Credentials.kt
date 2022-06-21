package ru.mail.search.assistant.common.http.assistant

import ru.mail.search.assistant.common.util.SecureString

data class Credentials(
    val session: SecureString,
    val secret: SecureString?,
    val sessionType: SessionType
)