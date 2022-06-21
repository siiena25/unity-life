package ru.mail.search.assistant.vk.auth

import ru.mail.search.assistant.auth.common.domain.model.Credentials

internal data class VkAssistantSession(
    val userId: Long,
    val credentials: Credentials
)