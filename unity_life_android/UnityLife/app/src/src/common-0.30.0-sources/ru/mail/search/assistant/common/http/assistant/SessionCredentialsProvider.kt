package ru.mail.search.assistant.common.http.assistant

interface SessionCredentialsProvider {

    suspend fun getCredentials(): Credentials

    suspend fun onSessionExpired(credentials: Credentials)
}