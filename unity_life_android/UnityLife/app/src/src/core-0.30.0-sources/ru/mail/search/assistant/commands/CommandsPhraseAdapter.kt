package ru.mail.search.assistant.commands

import com.google.gson.JsonObject
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.ExecutionContext

interface CommandsPhraseAdapter {

    suspend fun sendText(
        context: ExecutionContext,
        text: String,
        callbackData: String? = null,
        clientData: String? = null
    ): PhraseResult

    suspend fun sendEvent(
        context: ExecutionContext,
        event: String,
        callbackData: String? = null,
        clientData: String? = null,
        params: Map<String, String> = emptyMap()
    ): PhraseResult

    suspend fun sendPushPayload(
        context: ExecutionContext,
        pushId: String?,
        callbackData: String
    ): PhraseResult

    fun parseCommands(json: JsonObject): List<ExecutableCommandData>
}