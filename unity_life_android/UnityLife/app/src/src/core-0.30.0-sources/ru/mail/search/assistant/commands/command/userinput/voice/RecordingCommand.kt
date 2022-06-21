package ru.mail.search.assistant.commands.command.userinput.voice

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import ru.mail.search.assistant.audiorecorder.recorder.AudioRecordException
import ru.mail.search.assistant.commands.CommandsStateAdapter
import ru.mail.search.assistant.commands.command.CancelableContextCommand
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.model.InteractionMethod
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.LoadingMessage
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.data.local.MessageUuidProvider
import ru.mail.search.assistant.entities.assistant.AssistantStatus
import ru.mail.search.assistant.interactor.AudioFocusEvent
import ru.mail.search.assistant.interactor.AudioFocusHandler
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.util.Tag
import ru.mail.search.assistant.voice.VoiceInteractor
import ru.mail.search.assistant.voicemanager.VoiceRecordStatus

internal abstract class RecordingCommand(
    private val voiceRepository: VoiceInteractor,
    private val stateAdapter: CommandsStateAdapter,
    private val messagesRepository: MessagesRepository,
    private val permissionManager: AssistantPermissionChecker,
    private val audioFocusHandler: AudioFocusHandler,
    private val poolDispatcher: PoolDispatcher,
    uuidProvider: MessageUuidProvider,
    private val logger: Logger?
) : CancelableContextCommand<PhraseResult>(logger = logger) {

    protected val lastMessageId = uuidProvider.get()

    override suspend fun executeInContext(
        context: ExecutionContext
    ): PhraseResult = coroutineScope {
        checkRecordAudioPermission()
        val voiceContext = if (context.phrase.interactionMethod != InteractionMethod.VOICE) {
            context.overrideInteractionMethod(InteractionMethod.VOICE)
        } else {
            context
        }
        val voiceStatusObserving = launch { observeVoiceStatus(context) }
        withContext(poolDispatcher.work) {
            tryToChangeAudioFocus(AudioFocusEvent.Duck(lastMessageId))
            runCatching { startVoiceRecording(voiceContext) }
                .also {
                    voiceStatusObserving.cancel()
                    withContext(NonCancellable) {
                        tryToChangeAudioFocus(AudioFocusEvent.Unduck(lastMessageId))
                    }
                }
                .onFailure {
                    withContext(NonCancellable) {
                        hideLoadingMessage()
                    }
                }
                .also {
                    checkForCancellation()
                }
                .getOrThrow()
        }
    }

    abstract suspend fun startVoiceRecording(context: ExecutionContext): PhraseResult

    protected fun showAsrText(textSoFar: String) {
        val message = LoadingMessage.Show(
            textSoFar = textSoFar,
            uuid = lastMessageId
        )
        messagesRepository.offerLoadingMessage(message)
    }

    protected fun hideLoadingMessage() {
        val message = LoadingMessage.Hide(lastMessageId)
        messagesRepository.offerLoadingMessage(message)
    }

    private suspend fun observeVoiceStatus(context: ExecutionContext) {
        runCatching {
            voiceRepository.observeStatus()
                .buffer(Channel.CONFLATED)
                .collect { status ->
                    val assistantStatus = when (status) {
                        VoiceRecordStatus.IDLE -> AssistantStatus.DEFAULT
                        VoiceRecordStatus.RECORDING -> AssistantStatus.RECORDING
                        VoiceRecordStatus.LOADING -> AssistantStatus.LOADING
                    }
                    tryToSetStatus(context, assistantStatus)
                }
        }
            .onFailure { error ->
                if (error !is CancellationException) {
                    logger?.e(Tag.ASSISTANT_COMMAND, error)
                }
            }
        resetContextAssistantStatus(context)
    }

    private suspend fun tryToSetStatus(context: ExecutionContext, status: AssistantStatus) {
        stateAdapter.setContextAssistantStatus(context, status)
    }

    private suspend fun resetContextAssistantStatus(context: ExecutionContext) {
        withContext(NonCancellable) {
            stateAdapter.resetContextAssistantStatus(context)
        }
    }

    private suspend fun tryToChangeAudioFocus(event: AudioFocusEvent) {
        runCatching { audioFocusHandler.event(event) }
            .onFailure { error ->
                if (error !is CancellationException) {
                    logger?.e(Tag.ASSISTANT_COMMAND, error)
                }
            }
    }

    private suspend fun checkRecordAudioPermission() {
        val hasPermission = withContext(poolDispatcher.main) {
            permissionManager.checkRecordAudioPermission()
        }
        if (!hasPermission) {
            throw AudioRecordException("No permission to record sound")
        }
    }
}
