package ru.mail.search.assistant.dependencies.session

import ru.mail.search.assistant.AssistantIntentHandlerProvider
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.commands.command.CommandErrorHandler
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.factory.FactoryProvider
import ru.mail.search.assistant.commands.main.*
import ru.mail.search.assistant.commands.main.skipkws.KwsSkipController
import ru.mail.search.assistant.commands.processor.CommandIdGenerator
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.*
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.data.ContactsManager
import ru.mail.search.assistant.data.remote.RemoteDataSource
import ru.mail.search.assistant.data.remote.parser.ResultParser
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.dependencies.LocalModule
import ru.mail.search.assistant.interactor.AudioFocusHandler
import ru.mail.search.assistant.interactor.PhraseInteractor
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.voice.VoiceInteractor

internal class CommandsModule(
    localModule: LocalModule,
    mediaModule: MediaModule,
    analytics: Analytics?,
    sessionProvider: SessionCredentialsProvider,
    phraseInteractor: PhraseInteractor,
    voiceRepository: VoiceInteractor,
    messagesRepository: MessagesRepository,
    assistantContextRepository: AssistantContextRepository,
    queueProvider: AssistantCommandQueueProvider,
    permissionManager: AssistantPermissionChecker,
    poolDispatcher: PoolDispatcher,
    resultParser: ResultParser,
    musicController: CommandsMusicController,
    kwsSkipController: KwsSkipController,
    modifications: LazyModificationsProvider,
    intentHandlerProvider: AssistantIntentHandlerProvider?,
    remoteDataSource: RemoteDataSource,
    splitExperimentParamProvider: SplitExperimentParamProvider?,
    rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    clientStateRepository: ClientStateRepository,
    contactsManager: ContactsManager,
    contactsRepository: ContactsRepository,
    logger: Logger?
) {

    private val audioFocusHandler = AudioFocusHandler(musicController, logger)
    private val externalCommandDataProvidersProvider =
        ExternalCommandDataProvidersProvider(modifications)
    private val commandErrorHandler = CommandErrorHandler(assistantContextRepository, logger)

    private val factoryProvider = FactoryProvider(
        phraseInteractor,
        voiceRepository,
        permissionManager,
        messagesRepository,
        audioFocusHandler,
        kwsSkipController,
        localModule.messageUuidProvider,
        sessionProvider,
        mediaModule.ttsPlayer,
        mediaModule.soundStreamPlayer,
        assistantContextRepository,
        analytics,
        poolDispatcher,
        musicController,
        externalCommandDataProvidersProvider,
        resultParser,
        commandErrorHandler,
        remoteDataSource,
        splitExperimentParamProvider,
        rtLogDevicePhraseExtraDataEvent,
        clientStateRepository,
        contactsManager,
        contactsRepository,
        logger
    )
    private val mainIntentsHandler = MainIntentsHandler(
        factoryProvider,
        commandErrorHandler,
        factoryProvider.commandsAdapter.statisticAdapter,
        clientStateRepository,
        logger
    )
    private val handlersProvider = IntentHandlerProvider(
        intentHandlerProvider,
        factoryProvider.commandsAdapter,
        mainIntentsHandler
    )
    private val phraseContextIdGenerator = CommandIdGenerator()
    private val phraseRequestIdGenerator = CommandIdGenerator()
    val commandsInteractor: CommandsInteractor = CommandsInteractorImpl(
        assistantContextRepository,
        queueProvider,
        handlersProvider,
        phraseContextIdGenerator,
        phraseRequestIdGenerator,
        poolDispatcher,
        logger
    )
}