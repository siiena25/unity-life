package ru.mail.search.assistant.commands

import com.google.gson.JsonObject
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.main.PhraseResultMapper
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.timeDifference
import ru.mail.search.assistant.data.remote.parser.ResultParser
import ru.mail.search.assistant.entities.CommonPhraseResult
import ru.mail.search.assistant.interactor.PhraseInteractor
import ru.mail.search.assistant.util.analytics.event.TextMessageSending
import ru.mail.search.assistant.util.analytics.event.TextMessageSent

internal class CommandsPhraseAdapterImpl(
    private val phraseInteractor: PhraseInteractor,
    private val poolDispatcher: PoolDispatcher,
    private val resultParser: ResultParser,
    private val resultMapper: PhraseResultMapper,
    private val musicController: CommandsMusicController,
    private val analytics: Analytics?
) : CommandsPhraseAdapter {

    override suspend fun sendText(
        context: ExecutionContext,
        text: String,
        callbackData: String?,
        clientData: String?
    ): PhraseResult {
        val playerData = withContext(poolDispatcher.main) { musicController.getPlayerData() }
        val result = withContext(poolDispatcher.io) {
            val startSendingTime = analyticsStartSending()
            runCatching {
                phraseInteractor.sendTextPhrase(
                    text,
                    callbackData,
                    clientData,
                    playerData,
                    context.refuseDialogInteraction(),
                    context.phrase.requestId
                )
            }
                .also { analyticsEndSending(startSendingTime) }
                .getOrThrow()
        }
        return handleResult(result)
    }

    override suspend fun sendEvent(
        context: ExecutionContext,
        event: String,
        callbackData: String?,
        clientData: String?,
        params: Map<String, String>
    ): PhraseResult {
        val playerData = withContext(poolDispatcher.main) { musicController.getPlayerData() }
        val result = withContext(poolDispatcher.io) {
            phraseInteractor.sendEvent(
                event,
                callbackData,
                clientData,
                playerData,
                context.refuseDialogInteraction(),
                params,
                context.phrase.requestId
            )
        }
        return handleResult(result)
    }

    override suspend fun sendPushPayload(
        context: ExecutionContext,
        pushId: String?,
        callbackData: String
    ): PhraseResult {
        val result = withContext(poolDispatcher.io) {
            phraseInteractor.sendPushPayload(
                pushId = pushId,
                callbackData = callbackData,
                refuseTts = context.refuseDialogInteraction(),
                phraseRequestId = context.phrase.requestId
            )
        }
        return handleResult(result)
    }

    override fun parseCommands(json: JsonObject): List<ExecutableCommandData> {
        val commands = resultParser.parseCommands(json)
        return resultMapper.map("", commands)
    }

    private fun analyticsStartSending(): Long? {
        return analytics?.let {
            analytics.log(TextMessageSending())
            analytics.getCurrentTime()
        }
    }

    private fun analyticsEndSending(start: Long?) {
        start?.let {
            analytics?.log(TextMessageSent(analytics.timeDifference(start)))
        }
    }

    private fun handleResult(result: CommonPhraseResult): PhraseResult {
        val commands = resultMapper.map(result.metadata.phraseId, result.commands)
        return PhraseResult(result.metadata, commands)
    }
}