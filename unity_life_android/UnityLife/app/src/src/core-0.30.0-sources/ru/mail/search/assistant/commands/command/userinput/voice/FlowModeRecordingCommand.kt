package ru.mail.search.assistant.commands.command.userinput.voice

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import ru.mail.search.assistant.commands.CommandsStateAdapter
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.data.local.MessageUuidProvider
import ru.mail.search.assistant.entities.PhraseMetadata
import ru.mail.search.assistant.interactor.AudioFocusHandler
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.util.Tag
import ru.mail.search.assistant.voice.VoiceInteractor
import ru.mail.search.assistant.voicemanager.flowmode.FlowModeRecordingCallback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class FlowModeRecordingCommand(
    private val flowModeModel: String,
    private val voiceInteractor: VoiceInteractor,
    stateAdapter: CommandsStateAdapter,
    messagesRepository: MessagesRepository,
    permissionManager: AssistantPermissionChecker,
    audioFocusHandler: AudioFocusHandler,
    private val musicController: CommandsMusicController,
    private val flowModeCommandsParser: FlowModeCommandsParser,
    uuidProvider: MessageUuidProvider,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : RecordingCommand(
    voiceInteractor,
    stateAdapter,
    messagesRepository,
    permissionManager,
    audioFocusHandler,
    poolDispatcher,
    uuidProvider,
    logger
) {

    private companion object {

        private const val FLOW_MODE_COMMANDS_CHANNEL_SIZE = 5
    }

    override suspend fun startVoiceRecording(context: ExecutionContext): PhraseResult {
        return coroutineScope {
            val phraseText = context.phrase.toShortString()
            val message = "Start executing flow mode $phraseText"
            logger?.i(Tag.ASSISTANT_COMMAND, message)
            val commandsChannel = Channel<List<String>>(FLOW_MODE_COMMANDS_CHANNEL_SIZE)
            val commandsProcessing = launch { handleFlowModeCommands(context, commandsChannel) }
            val result = recordVoicePhrase(commandsChannel)
            commandsProcessing.join()
            hideLoadingMessage()
            result
        }
    }

    private suspend fun recordVoicePhrase(
        commandsSource: SendChannel<List<String>>
    ): PhraseResult {
        val playerData = withContext(poolDispatcher.main) { musicController.getPlayerData() }
        return suspendCancellableCoroutine { continuation ->
            val callback = RecordingCallback(continuation, commandsSource)
            voiceInteractor.startFlowMode(flowModeModel, playerData, callback)
            continuation.invokeOnCancellation { voiceInteractor.cancelRecording() }
        }
    }

    private suspend fun handleFlowModeCommands(
        context: ExecutionContext,
        commandsChannel: ReceiveChannel<List<String>>
    ) {
        commandsChannel.consumeAsFlow().collect { commands ->
            flowModeCommandsParser.parse(commands, lastMessageId)
                .map { command -> context.sync(command) }
                .awaitAll()
        }
    }

    private inner class RecordingCallback(
        private val continuation: CancellableContinuation<PhraseResult>,
        private val commandsSource: SendChannel<List<String>>
    ) : FlowModeRecordingCallback {

        override fun onProcess(recognizedText: String, phraseId: String) {
            if (recognizedText.isNotBlank() && !flowModeCommandsParser.isAsrHandled()) {
                showAsrText(recognizedText)
            }
        }

        override fun onReceiveCommands(commands: List<String>, phraseId: String) {
            try {
                commandsSource.offer(commands)
            } catch (error: ClosedSendChannelException) {
                // Recording already finished
            }
        }

        override fun onSuccess(phraseId: String) {
            val metadata = PhraseMetadata(phraseId = phraseId, skill = null)
            val result = PhraseResult(metadata, emptyList())
            commandsSource.close()
            continuation.resume(result)
        }

        override fun onError(error: Throwable) {
            commandsSource.close()
            continuation.resumeWithException(error)
        }
    }
}