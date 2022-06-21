package ru.mail.search.assistant.commands.factory

import ru.mail.search.assistant.api.phrase.ActivationType
import ru.mail.search.assistant.commands.CommandsMessagesAdapter
import ru.mail.search.assistant.commands.CommandsPhraseAdapter
import ru.mail.search.assistant.commands.CommandsStateAdapter
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.command.userinput.*
import ru.mail.search.assistant.commands.command.userinput.voice.FlowModeCommandsParser
import ru.mail.search.assistant.commands.command.userinput.voice.FlowModeRecordingCommand
import ru.mail.search.assistant.commands.command.userinput.voice.PhraseRecordingCommand
import ru.mail.search.assistant.commands.main.PhraseResultMapper
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.data.local.MessageUuidProvider
import ru.mail.search.assistant.data.remote.parser.ResultParser
import ru.mail.search.assistant.interactor.AudioFocusHandler
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.voice.VoiceInteractor

internal class UserInputCommandsFactory(
    private val voiceRepository: VoiceInteractor,
    private val musicController: CommandsMusicController,
    private val permissionManager: AssistantPermissionChecker,
    private val messagesRepository: MessagesRepository,
    private val poolDispatcher: PoolDispatcher,
    private val resultMapper: PhraseResultMapper,
    private val resultParser: ResultParser,
    private val audioFocusHandler: AudioFocusHandler,
    private val messageUuidProvider: MessageUuidProvider,
    private val phraseAdapter: CommandsPhraseAdapter,
    private val stateAdapter: CommandsStateAdapter,
    private val messagesAdapter: CommandsMessagesAdapter,
    private val factoryProvider: FactoryProvider,
    private val analytics: Analytics?,
    private val logger: Logger?
) {

    fun sendTextMessage(
        text: String,
        callbackData: String?,
        clientData: String?,
        showMessage: Boolean
    ): SendTextMessage {
        return SendTextMessage(
            text = text,
            callbackData = callbackData,
            clientData = clientData,
            appendOutgoingMessage = showMessage,
            phraseAdapter,
            stateAdapter,
            messagesAdapter,
            factoryProvider.public,
            logger
        )
    }

    fun sendEvent(
        event: String,
        callbackData: String?,
        clientData: String? = null,
        params: Map<String, String> = emptyMap()
    ): SendEvent {
        return SendEvent(
            event = event,
            callbackData = callbackData,
            clientData = clientData,
            params = params,
            phraseAdapter,
            logger
        )
    }

    fun sendPushPayload(pushId: String?, callbackData: String): SendPushPayload {
        return SendPushPayload(pushId = pushId, callbackData = callbackData, phraseAdapter, logger)
    }

    fun sendStartAppEvent(
        isResultIgnored: Boolean,
        isStartAppListenEnabled: Boolean?
    ): SendStartAppEvent {
        return SendStartAppEvent(
            isResultIgnored,
            isStartAppListenEnabled,
            phraseAdapter,
            factoryProvider.public,
            logger
        )
    }

    fun sendEventWithText(
        event: String,
        text: String?,
        callbackData: String?,
        clientData: String?
    ): SendEventWithText {
        return SendEventWithText(
            event,
            text,
            callbackData,
            clientData,
            messagesRepository,
            this,
            factoryProvider.commands,
            logger
        )
    }

    fun recordPhrase(
        startedManually: Boolean,
        minWaitingTime: Int?,
        activationType: ActivationType? = null,
        callbackData: String? = null
    ): PhraseRecordingCommand {
        val properties = PhraseRecordingCommand.Properties(
            startedManually = startedManually,
            minWaitingTime = minWaitingTime,
            activationType = activationType,
            callbackData = callbackData
        )
        return PhraseRecordingCommand(
            properties,
            voiceRepository,
            stateAdapter,
            messagesRepository,
            permissionManager,
            audioFocusHandler,
            factoryProvider.commands,
            musicController,
            resultMapper,
            resultParser,
            messageUuidProvider,
            poolDispatcher,
            analytics,
            logger
        )
    }

    fun recordFlowMode(flowModeModel: String): FlowModeRecordingCommand {
        val commandsParser = FlowModeCommandsParser(factoryProvider.public, logger)
        return FlowModeRecordingCommand(
            flowModeModel,
            voiceRepository,
            stateAdapter,
            messagesRepository,
            permissionManager,
            audioFocusHandler,
            musicController,
            commandsParser,
            messageUuidProvider,
            poolDispatcher,
            logger
        )
    }
}