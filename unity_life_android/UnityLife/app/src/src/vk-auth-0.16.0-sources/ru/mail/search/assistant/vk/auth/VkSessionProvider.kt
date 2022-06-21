package ru.mail.search.assistant.vk.auth

import ru.mail.search.assistant.auth.common.SessionCredentialsProvider
import ru.mail.search.assistant.auth.common.domain.model.Credentials
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

    suspend fun compareAndClear(expiredSession: String) {
        singleLoadOperation.compareAndClear { currentSession ->
            expiredSession == currentSession.credentials.session.raw
        }
    }
}