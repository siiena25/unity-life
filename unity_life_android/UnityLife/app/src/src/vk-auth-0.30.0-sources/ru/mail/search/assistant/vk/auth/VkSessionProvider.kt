package ru.mail.search.assistant.vk.auth

import ru.mail.search.assistant.common.http.assistant.Credentials
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.util.SingleLoadOperation

class VkSessionProvider internal constructor(
    operation: SingleLoadOperation.Operation<VkAssistantSession>
) : SessionCredentialsProvider {

    private val singleLoadOperation = SingleLoadOperation(operation)

    override suspend fun getCredentials(): Credentials {
        return singleLoadOperation.getData().credentials
    }

    suspend fun login() {
        singleLoadOperation.getData()
    }

    override suspend fun onSessionExpired(credentials: Credentials) {
        singleLoadOperation.compareAndClear { currentSession ->
            credentials == currentSession.credentials
        }
    }
}