package ru.mail.search.assistant.interactor

import ru.mail.search.assistant.common.util.Result

 interface StatesRepository<T, P> {
    suspend fun setState(sessionId: String, state: T): Result<String>
    suspend fun getState(params: P, sessionId: String): Result<T>
}
