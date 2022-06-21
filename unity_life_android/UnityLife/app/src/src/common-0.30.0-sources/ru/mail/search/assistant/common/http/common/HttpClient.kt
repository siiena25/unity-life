package ru.mail.search.assistant.common.http.common

interface HttpClient {

    suspend fun execute(request: HttpRequest): ServerResponse
}