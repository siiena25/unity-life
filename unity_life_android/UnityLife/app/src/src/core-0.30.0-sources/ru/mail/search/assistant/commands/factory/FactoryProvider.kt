package ru.mail.search.assistant.commands.factory

import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.commands.*
import ru.mail.search.assistant.commands.command.CommandErrorHandler
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.main.ExternalCommandDataProvidersProvider
import ru.mail.search.assistant.commands.main.ExternalCommandFactory
import ru.mail.search.assistant.commands.main.ExternalCommandFactoryImpl
import ru.mail.search.assistant.commands.main.PhraseResultMapper
import ru.mail.search.assistant.commands.main.skipkws.KwsSkipController
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.*
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.data.ContactsManager
import ru.mail.search.assistant.data.local.MessageUuidProvider
import ru.mail.search.assistant.data.remote.RemoteDataSource
import ru.mail.search.assistant.data.remote.parser.ResultParser
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.interactor.AudioFocusHandler
import ru.mail.search.assistant.interactor.PhraseInteractor
import ru.mail.search.assistant.media.wrapper.StreamPlayerAdapter
import ru.mail.search.assistant.media.wrapper.TtsPlayer
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.voice.VoiceInteractor

internal class FactoryProvider(
    private val phraseInteractor: PhraseInteractor,
    private val voiceRepository: VoiceInteractor,
    private val permissionManager: AssistantPermissionChecker,
    private val messagesRepository: MessagesRepository,
    private val audioFocusHandler: AudioFocusHandler,
    private val kwsSkipController: KwsSkipController,
    private val messageUuidProvider: MessageUuidProvider,
    private val sessionProvider: SessionCredentialsProvider,
    private val ttsPlayer: TtsPlayer,
    private val mediaPlayer: StreamPlayerAdapter,
    private val contextRepository: AssistantContextRepository,
    private val analytics: Analytics?,
    private val poolDispatcher: PoolDispatcher,
    private val musicController: CommandsMusicController,
    private val externalCommandDataProvidersProvider: ExternalCommandDataProvidersProvider,
    private val resultParser: ResultParser,
    private val commandErrorHandler: CommandErrorHandler,
    private val remoteDataSource: RemoteDataSource,
    private val splitExperimentParamProvider: SplitExperimentParamProvider?,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val clientStateRepository: ClientStateRepository,
    private val contactsManager: ContactsManager,
    private val contactsRepository: ContactsRepository,
    private val logger: Logger?
) {

    private val phraseResultMapper = PhraseResultMapper(this)
    private val phraseAdapter = createPhraseAdapter()
    private val stateAdapter = createStateAdapter()
    private val messagesAdapter = createMessagesAdapter()
    private val statisticAdapter = createStatisticAdapter()

    val userInput: UserInputCommandsFactory = createUserInputCommandsFactory()
    val controls: ControlCommandsFactory = createControlsCommandsFactory()
    val public: PublicCommandsFactory = createPublicCommandsFactory()
    val commands: CommandsFactory = createCommandsFactory()
    val commandsAdapter: CommandsAdapter = createAdapter()
    val external: ExternalCommandFactory = createExternalCommandsFactory()

    private fun createPhraseAdapter(): CommandsPhraseAdapter {
        return CommandsPhraseAdapterImpl(
            phraseInteractor,
            poolDispatcher,
            resultParser,
            phraseResultMapper,
            musicController,
            analytics
        )
    }

    private fun createStateAdapter(): CommandsStateAdapter {
        return CommandsStateAdapterImpl(commandErrorHandler, contextRepository)
    }

    private fun createMessagesAdapter(): CommandsMessagesAdapter {
        return CommandsMessagesAdapterImpl(messagesRepository)
    }

    private fun createStatisticAdapter(): CommandsStatisticAdapter {
        return CommandsStatisticAdapterImpl(rtLogDevicePhraseExtraDataEvent)
    }

    private fun createAdapter(): CommandsAdapter {
        return CommandsAdapterImpl(
            phraseAdapter,
            stateAdapter,
            messagesAdapter,
            statisticAdapter,
            public
        )
    }

    private fun createUserInputCommandsFactory(): UserInputCommandsFactory {
        return UserInputCommandsFactory(
            voiceRepository,
            musicController,
            permissionManager,
            messagesRepository,
            poolDispatcher,
            phraseResultMapper,
            resultParser,
            audioFocusHandler,
            messageUuidProvider,
            phraseAdapter,
            stateAdapter,
            messagesAdapter,
            this,
            analytics,
            logger
        )
    }

    private fun createCommandsFactory(): CommandsFactory {
        return CommandsFactory(
            sessionProvider,
            messagesRepository,
            musicController,
            ttsPlayer,
            mediaPlayer,
            contextRepository,
            stateAdapter,
            audioFocusHandler,
            kwsSkipController,
            poolDispatcher,
            remoteDataSource,
            splitExperimentParamProvider,
            rtLogDevicePhraseExtraDataEvent,
            clientStateRepository,
            contactsManager,
            userInput,
            contactsRepository,
            public,
            logger
        )
    }

    private fun createControlsCommandsFactory(): ControlCommandsFactory {
        return ControlCommandsFactory(musicController, logger)
    }

    private fun createExternalCommandsFactory(): ExternalCommandFactory {
        return ExternalCommandFactoryImpl(
            externalCommandDataProvidersProvider,
            commandsAdapter,
            phraseInteractor,
            public,
            logger
        )
    }

    private fun createPublicCommandsFactory(): PublicCommandsFactory {
        return PublicCommandsFactoryImpl(
            commandErrorHandler,
            this,
            logger,
            statisticAdapter
        )
    }
}