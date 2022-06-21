package ru.mail.search.assistant.services.music.callback.limit

import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClient

internal class BackgroundMusicDataSource(
    private val httpClient: AssistantHttpClient,
    private val credentialsProvider: SessionCredentialsProvider,
) {

    suspend fun sendLimitReachedEvent() {
        httpClient.post("device/background_music", credentialsProvider.getCredentials())
    }
}