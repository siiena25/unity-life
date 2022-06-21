package ru.mail.search.assistant.vk.auth.operation

import ru.mail.search.assistant.common.util.SingleLoadOperation
import ru.mail.search.assistant.vk.auth.VkAssistantSession
import ru.mail.search.assistant.vk.auth.data.VkLoginDataSource

internal class VkAuthOperation(
    private val authCallback: VkAuthCallback,
    private val dataSource: VkLoginDataSource
) : SingleLoadOperation.Operation<VkAssistantSession> {

    override suspend fun load(): VkAssistantSession {
        val vkAuth = authCallback.getAuthData()
        val credentials = dataSource.login(vkAuth)
        return VkAssistantSession(vkAuth.userId, credentials)
    }

    override fun isCacheValid(data: VkAssistantSession): Boolean {
        return authCallback.isAuthValid(data.userId)
    }
}