package ru.mail.search.assistant.vk.auth

import kotlinx.coroutines.runBlocking
import ru.mail.search.assistant.common.data.remote.error.SessionExpiredCallback

class VkSessionExpiredCallback(
    private val vkSessionProvider: VkSessionProvider
) : SessionExpiredCallback {

    override fun onSessionExpired(expiredSession: String?) {
        expiredSession ?: return
        runBlocking { vkSessionProvider.compareAndClear(expiredSession) }
    }
}