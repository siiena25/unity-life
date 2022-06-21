package ru.mail.search.assistant.vk.auth.operation

import ru.mail.search.assistant.vk.auth.VkAuthorization

interface VkAuthCallback {

    fun getAuthData(): VkAuthorization

    fun isAuthValid(userId: Long): Boolean
}