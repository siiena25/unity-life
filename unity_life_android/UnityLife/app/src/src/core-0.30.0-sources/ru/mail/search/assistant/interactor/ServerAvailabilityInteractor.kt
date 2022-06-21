package ru.mail.search.assistant.interactor

import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.data.remote.RemoteDataSource
import ru.mail.search.assistant.entities.AssistantServerStatus

class ServerAvailabilityInteractor(
    private val remoteDataSource: RemoteDataSource,
    private val sessionProvider: SessionCredentialsProvider
) {

    suspend fun getServerStatus(): AssistantServerStatus {
        return remoteDataSource.checkServerAvailability(sessionProvider.getCredentials())
    }
}