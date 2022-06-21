package ru.mail.search.assistant.commands.main

import ru.mail.search.assistant.commands.CommandsStatisticAdapter
import ru.mail.search.assistant.commands.command.CommandErrorHandler
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.factory.FactoryProvider
import ru.mail.search.assistant.commands.intent.AssistantIntent
import ru.mail.search.assistant.commands.intent.AssistantIntentsHandler
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.data.exception.UserInputException
import ru.mail.search.assistant.util.Tag
import java.net.UnknownHostException

internal class MainIntentsHandler(
    private val factoryProvider: FactoryProvider,
    private val commandErrorHandler: CommandErrorHandler,
    private val statisticAdapter: CommandsStatisticAdapter,
    private val clientStateRepository: ClientStateRepository,
    private val logger: Logger?
) : AssistantIntentsHandler {

    override suspend fun handle(context: ExecutionContext, intent: AssistantIntent): Boolean {
        return when (intent) {
            is AssistantIntent.UserInput ->
                handleUserInput(context, intent)
            is AssistantIntent.SilentEvent -> {
                val command = factoryProvider.userInput.sendEvent(
                    event = intent.event,
                    callbackData = intent.callbackData,
                    clientData = intent.clientData,
                    params = intent.params
                )
                context.executeWithResult(command)
                true
            }
            is AssistantIntent.PushPayload -> {
                val command = factoryProvider.userInput.sendPushPayload(
                    intent.pushId,
                    intent.callbackData
                )
                context.executeWithResult(command)
                true
            }
            is AssistantIntent.StartApp -> {
                val command = factoryProvider.userInput.sendStartAppEvent(
                    intent.isResultIgnored,
                    intent.isStartAppListenEnabled
                )
                context.executeWithResult(command)
                true
            }
            is AssistantIntent.OpenUrl -> {
                runCatching { context.async(factoryProvider.commands.showUrl(intent.url)).await() }
                    .onFailure { error -> onError(error) }
                    .getOrThrow()
                true
            }
            is AssistantIntent.AddMessage -> {
                runCatching { context.async(addMessage(intent)).await() }
                    .onFailure { error -> onError(error) }
                    .getOrThrow()
                true
            }
            else -> false
        }
    }

    private suspend fun handleUserInput(
        context: ExecutionContext,
        intent: AssistantIntent.UserInput
    ): Boolean {
        when (intent) {
            is AssistantIntent.UserInput.Text -> {
                if (intent.silentMode) {
                    context.silence()
                }
                val command = factoryProvider.userInput.sendTextMessage(
                    intent.text,
                    intent.callbackData,
                    intent.clientData,
                    intent.showMessage
                )
                context.executeUserInputCommand(command)
            }
            is AssistantIntent.UserInput.Voice -> {
                val command = factoryProvider.userInput.recordPhrase(
                    startedManually = intent.startedManually,
                    minWaitingTime = intent.minWaitingTime,
                    activationType = intent.activationType,
                    callbackData = intent.callbackData
                )
                context.executeUserInputCommand(command)
            }
            is AssistantIntent.UserInput.Event -> {
                val command = factoryProvider.userInput.sendEventWithText(
                    intent.event,
                    intent.text,
                    intent.callbackData,
                    intent.clientData
                )
                context.executeUserInputCommand(command)
            }
            is AssistantIntent.UserInput.FlowMode -> {
                val command = factoryProvider.userInput.recordFlowMode(intent.flowModeModel)
                executeFlowMode(context, command)
            }
        }
        return true
    }

    private suspend fun executeFlowMode(context: ExecutionContext, command: ExecutableCommand<*>) {
        val phrase = context.phrase
        val phraseDescription = phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "On start user input $phraseDescription")
        context.notify(CommandNotification.UserInput.Initiated(phrase))
        runCatching { context.async(command).await() }
            .onSuccess {
                logger?.i(Tag.ASSISTANT_COMMAND, "On success user input $phraseDescription")
                context.notify(CommandNotification.UserInput.Processed(phrase))
            }
            .onFailure { error ->
                onUserInputFailed(context, error)
            }
            .getOrThrow()
    }

    private suspend fun ExecutionContext.executeUserInputCommand(
        command: ExecutableCommand<PhraseResult>
    ) {
        val phraseDescription = phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "On start user input $phraseDescription")
        val phraseRequestId = phrase.requestId
        clientStateRepository.onStartUserRequest(phraseRequestId)
        statisticAdapter.initializePhrase(this)
        notify(CommandNotification.UserInput.Initiated(phrase))
        runCatching { async(command).await() }
            .onSuccess { result ->
                logger?.i(Tag.ASSISTANT_COMMAND, "On success user input $phraseDescription")
                notify(CommandNotification.UserInput.Processed(phrase))
                handlePhraseResult(result)
            }
            .onFailure { error ->
                onUserInputFailed(this, error)
            }
            .also {
                clientStateRepository.onFinishUserRequest(phraseRequestId)
                statisticAdapter.finishPhrase(this)
            }
            .getOrThrow()
    }

    private suspend fun onUserInputFailed(context: ExecutionContext, error: Throwable) {
        val phraseDescription = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "On user input failure $phraseDescription")
        context.notify(CommandNotification.UserInput.Failed(context.phrase))
        val errorToThrow = if (error is UnknownHostException) {
            UserInputException(error)
        } else {
            error
        }
        onError(errorToThrow)
    }

    private suspend fun ExecutionContext.executeWithResult(command: ExecutableCommand<PhraseResult>) {
        statisticAdapter.initializePhrase(this)
        runCatching { async(command).await() }
            .onSuccess { result -> handlePhraseResult(result) }
            .onFailure { error -> onError(error) }
            .also { statisticAdapter.finishPhrase(this) }
            .getOrThrow()
    }

    private suspend fun ExecutionContext.handlePhraseResult(phraseResult: PhraseResult) {
        phraseResult.commands
            .map { command ->
                when (command.queueType) {
                    QueueType.SYNC -> sync(command.command)
                    QueueType.MEDIA -> mediaEvent(command.command)
                }
            }
            .map { result ->
                runCatching { result.await() }
                    .onFailure { error -> onError(error) }
            }
    }

    private fun addMessage(intent: AssistantIntent.AddMessage): ExecutableCommand<*> {
        return factoryProvider.public.showMessage(intent.message)
    }

    private fun onError(error: Throwable) {
        commandErrorHandler.onError(error)
    }
}