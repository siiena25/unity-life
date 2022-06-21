package ru.mail.search.assistant.commands.factory

import ru.mail.search.assistant.api.suggests.Suggest
import ru.mail.search.assistant.commands.CommandsStatisticAdapter
import ru.mail.search.assistant.commands.command.CommandErrorHandler
import ru.mail.search.assistant.commands.command.media.SoundPlayer
import ru.mail.search.assistant.commands.command.phrase.NestedExecutionCommand
import ru.mail.search.assistant.commands.command.phrase.UserInputCommand
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.PhraseMetadata
import ru.mail.search.assistant.entities.audio.KwsSkipInterval
import ru.mail.search.assistant.entities.message.MessageData

internal class PublicCommandsFactoryImpl(
    private val commandErrorHandler: CommandErrorHandler,
    private val factoryProvider: FactoryProvider,
    private val logger: Logger?,
    private val statisticAdapter: CommandsStatisticAdapter
) : PublicCommandsFactory {

    override fun showMessage(data: MessageData): ExecutableCommand<*> {
        return factoryProvider.commands.addMessage("", data)
    }

    override fun showWelcomeMessage(data: MessageData): ExecutableCommand<*> {
        return factoryProvider.commands.showWelcomeDialogMessage(data)
    }

    override fun showSuggests(suggests: List<Suggest>): ExecutableCommand<*> {
        return factoryProvider.commands.showSuggests("", suggests)
    }

    override fun playTts(
        url: String,
        isBlocking: Boolean,
        isPlayingForced: Boolean,
        kwsSkipInterval: List<KwsSkipInterval>?
    ): ExecutableCommand<*> {
        return factoryProvider.commands.playTts(
            url = url,
            isBlocking = isBlocking,
            isPlayingForced = isPlayingForced,
            kwsSkipIntervals = kwsSkipInterval
        )
    }

    override fun playDialogSound(
        player: SoundPlayer,
        isBlocking: Boolean,
        isPlayingForced: Boolean,
        kwsSkipInterval: List<KwsSkipInterval>?
    ): ExecutableCommand<*> {
        return factoryProvider.commands.playDialogSound(
            player = player,
            isBlocking = isBlocking,
            isPlayingForced = isPlayingForced,
            kwsSkipIntervals = kwsSkipInterval
        )
    }

    override fun awaitUserInput(): ExecutableCommand<*> {
        return factoryProvider.commands.waitForUserInput(
            minWaitingTime = null,
            muteActivationSound = false,
            callbackData = null
        )
    }

    override fun sendTextPhrase(
        text: String,
        callbackData: String?,
        clientData: String?,
        showMessage: Boolean
    ): ExecutableCommand<PhraseMetadata> {
        val command = factoryProvider.userInput.sendTextMessage(
            text = text,
            callbackData = callbackData,
            clientData = clientData,
            showMessage = showMessage
        )
        return executePhraseCommand(command)
    }

    override fun sendEvent(
        event: String,
        callbackData: String?,
        clientData: String?,
        params: Map<String, String>
    ): ExecutableCommand<PhraseMetadata> {
        val command = factoryProvider.userInput.sendEvent(
            event = event,
            callbackData = callbackData,
            clientData = clientData,
            params = params
        )
        return executePhraseCommand(command)
    }

    override fun sendPushPayload(
        pushId: String?,
        callbackData: String
    ): ExecutableCommand<PhraseMetadata> {
        val command = factoryProvider.userInput.sendPushPayload(
            pushId = pushId,
            callbackData = callbackData
        )
        return executePhraseCommand(command)
    }

    override fun executePhraseCommand(command: ExecutableCommand<PhraseResult>): ExecutableCommand<PhraseMetadata> {
        return UserInputCommand(
            command,
            this,
            commandErrorHandler,
            statisticAdapter,
            logger
        )
    }

    override fun executeNestedCommands(commands: List<ExecutableCommandData>): ExecutableCommand<*> {
        return NestedExecutionCommand(commands, commandErrorHandler, logger)
    }
}