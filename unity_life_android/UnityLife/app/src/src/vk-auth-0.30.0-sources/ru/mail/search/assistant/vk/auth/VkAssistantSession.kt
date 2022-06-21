package ru.mail.search.assistant.vk.auth

import ru.mail.search.assistant.common.http.assistant.Credentials

internal data class VkAssistantSession(
    val userId: Long,
    val credentials: Credentials
)