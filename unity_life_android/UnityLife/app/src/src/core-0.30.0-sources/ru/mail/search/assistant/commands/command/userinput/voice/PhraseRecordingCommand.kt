package ru.mail.search.assistant.commands.command.userinput.voice

import com.google.gson.JsonParser
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.api.phrase.ActivationType
import ru.mail.search.assistant.commands.CommandsStateAdapter
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.main.PhraseResultMapper
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.memorizedTimeDifference
import ru.mail.search.assistant.common.util.analytics.timeDifference
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.parseAsObject
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.data.local.MessageUuidProvider
import ru.mail.search.assistant.data.remote.parser.ResultParser
import ru.mail.search.assistant.entities.PhraseMetadata
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.interactor.AudioFocusHandler
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.util.Tag
import ru.mail.search.assistant.util.analytics.event.VoiceRecordRecognized
import ru.mail.search.assistant.util.analytics.event.VoiceRecordSending
import ru.mail.search.assistant.util.analytics.event.VoiceRecordSent
import ru.mail.search.assistant.voice.VoiceInteractor
import ru.mail.search.assistant.voicemanager.PhraseRecordingCallback
import ru.mail.search.assistant.voicemanager.data.VoicePhraseResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class PhraseRecordingCommand(
    private val properties: Properties,
    private val voiceInteractor: VoiceInteractor,
    stateAdapter: CommandsStateAdapter,
    private val messagesRepository: MessagesRepository,
    permissionManager: AssistantPermissionChecker,
    audioFocusHandler: AudioFocusHandler,
    private val commandsFactory: CommandsFactory,
    private val musicController: CommandsMusicController,
    private val resultMapper: PhraseResultMapper,
    private val resultParser: ResultParser,
    uuidProvider: MessageUuidProvider,
    private val poolDispatcher: PoolDispatcher,
    private val analytics: Analytics?,
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

    private val jsonParser = JsonParser()

    override suspend fun startVoiceRecording(context: ExecutionContext): PhraseResult {
        val phraseText = context.phrase.toShortString()
        val isKws = !properties.startedManually
        val message = "Start executing voice recording (isKws=$isKws) $phraseText"
        logger?.i(Tag.ASSISTANT_COMMAND, message)
        analytics?.log(VoiceRecordSending())
        val result = recordVoicePhrase(context)
        return handleVoicePhraseResult(context, result)
    }

    private suspend fun recordVoicePhrase(context: ExecutionContext): VoicePhraseResult {
        val playerData = withContext(poolDispatcher.main) { musicController.getPlayerData() }
        return suspendCancellableCoroutine { continuation ->
            val startedManually = properties.startedManually
            val minWaitingTime = properties.minWaitingTime
            val phraseRequestId = context.phrase.requestId
            val callback = PhraseRecordingCallbackImpl(continuation)
            val activationType = properties.activationType
            val callbackData = properties.callbackData
            voiceInteractor.startPhrase(
                startedManually,
                minWaitingTime,
                phraseRequestId,
                playerData,
                callback,
                activationType,
                callbackData
            )
            continuation.invokeOnCancellation { voiceInteractor.cancelRecording() }
        }
    }

    private suspend fun handleVoicePhraseResult(
        context: ExecutionContext,
        voicePhraseResult: VoicePhraseResult
    ): PhraseResult {
        val startSendingTime = analytics?.getCurrentTime()
        return runCatching { getPhraseResult(context, voicePhraseResult) }.also {
            startSendingTime?.let {
                analytics?.log(VoiceRecordSent(analytics.timeDifference(startSendingTime)))
            }
            if (!properties.startedManually) {
                analytics?.memorizedTimeDifference(VoiceRecordRecognized.STORE_TAG_TIME_DIFFERENCE)
                    ?.let { time -> analytics.log(VoiceRecordRecognized(time)) }
            }
        }.getOrThrow()
    }

    private suspend fun getPhraseResult(
        context: ExecutionContext,
        voicePhraseResult: VoicePhraseResult
    ): PhraseResult {
        val resultJson = jsonParser.parseAsObject(voicePhraseResult.response)
            ?.getObject("result")
            ?: throw ResultParsingException("Missing result body")
        val asrText = resultJson.getString("asr_text")
        val phraseId = voicePhraseResult.phraseId
        handleAsrText(context, phraseId, asrText)
        val commands = resultParser.parseCommands(resultJson)
        val skill = resultJson.getString("skill")
        val metadata = PhraseMetadata(phraseId = phraseId, skill = skill)
        val executableCommands = resultMapper.map(phraseId, commands)
        return PhraseResult(metadata, executableCommands)
    }

    private suspend fun handleAsrText(
        context: ExecutionContext,
        phraseId: String,
        asrText: String?
    ) {
        val recognizedText = asrText?.takeIf { it.isNotBlank() }
        if (recognizedText != null) {
            showOutgoingMessage(context, phraseId, recognizedText)
        } else {
            hideLoadingMessage()
        }
    }

    private suspend fun showOutgoingMessage(
        context: ExecutionContext,
        phraseId: String,
        text: String
    ) {
        val message = MessageData.OutgoingData(text, lastMessageId)
        val addMessage = commandsFactory.addMessage(phraseId, message)
        context.async(addMessage)
    }

    class Properties(
        val startedManually: Boolean,
        val minWaitingTime: Int?,
        val activationType: ActivationType? = null,
        val callbackData: String? = null
    )

    private inner class PhraseRecordingCallbackImpl(
        private val continuation: CancellableContinuation<VoicePhraseResult>
    ) : PhraseRecordingCallback {

        override fun onProcess(recognizedText: String, phraseId: String) {
            if (recognizedText.isNotBlank()) {
                showAsrText(recognizedText)
            }
        }

        override fun onSuccess(result: VoicePhraseResult) {
            continuation.resume(result)
        }

        override fun onLoading() {
            messagesRepository.clearSuggests()
        }

        override fun onError(error: Throwable) {
            continuation.resumeWithException(error)
        }
    }
}