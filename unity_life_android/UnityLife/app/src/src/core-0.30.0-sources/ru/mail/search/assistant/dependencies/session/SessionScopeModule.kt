package ru.mail.search.assistant.dependencies.session

import android.content.Context
import ru.mail.search.assistant.AssistantIntentHandlerProvider
import ru.mail.search.assistant.ForegroundHandler
import ru.mail.search.assistant.ModificationsProvider
import ru.mail.search.assistant.SkillServerParamProvider
import ru.mail.search.assistant.api.suggests.SuggestsParser
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.main.AssistantCommandQueueProvider
import ru.mail.search.assistant.commands.main.skipkws.KwsSkipController
import ru.mail.search.assistant.commands.main.skipkws.MusicKwsSkipRepository
import ru.mail.search.assistant.commands.main.skipkws.MusicPlayerMediaFailureHandler
import ru.mail.search.assistant.common.data.NetworkConnectivityManager
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.data.locating.LocationProvider
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.ResourceManager
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.*
import ru.mail.search.assistant.data.remote.parser.*
import ru.mail.search.assistant.dependencies.AudioModule
import ru.mail.search.assistant.dependencies.LocalModule
import ru.mail.search.assistant.dependencies.RemoteModule
import ru.mail.search.assistant.dependencies.RtLogModule
import ru.mail.search.assistant.entities.message.apprefs.AppsRefParser
import ru.mail.search.assistant.interactor.*
import ru.mail.search.assistant.media.AssistantBandwidthMeter
import ru.mail.search.assistant.media.AudioLevelInterceptor
import ru.mail.search.assistant.media.MediaBandwidthMeter
import ru.mail.search.assistant.session.AssistantSessionMusicController
import ru.mail.search.assistant.util.AssistantPermissionChecker
import ru.mail.search.assistant.voice.VoiceInteractor

internal class SessionScopeModule(
    private val appContext: Context,
    analytics: Analytics?,
    private val poolDispatcher: PoolDispatcher,
    resourceManager: ResourceManager,
    localModule: LocalModule,
    remoteModule: RemoteModule,
    messagesRepository: MessagesRepository,
    queueProvider: AssistantCommandQueueProvider,
    private val pendingIntentsInteractor: PendingIntentsInteractor,
    playerLimitInteractor: PlayerLimitInteractor,
    musicPlayerMediaFailureHandler: MusicPlayerMediaFailureHandler,
    val playerEventRepository: PlayerEventRepository,
    skillServerParamProvider: SkillServerParamProvider,
    modificationsProvider: ModificationsProvider,
    intentHandlerProvider: AssistantIntentHandlerProvider?,
    sessionProvider: SessionCredentialsProvider,
    splitExperimentParamProvider: SplitExperimentParamProvider?,
    rtLogModule: RtLogModule,
    audioModule: AudioModule,
    settingsRepository: SettingsRepository,
    clientStateRepository: ClientStateRepository,
    locationProvider: LocationProvider?,
    bandwidthMeter: AssistantBandwidthMeter,
    contactsRepository: ContactsRepository,
    val logger: Logger?
) {

    private val permissionChecker = AssistantPermissionChecker(appContext)

    private val suggestsParser = SuggestsParser(resourceManager)
    private val lazyModificationsProvider = LazyModificationsProvider(modificationsProvider)
    private val externalParsersProvider = ExternalParsersProvider(lazyModificationsProvider)
    private val appsRefParser = AppsRefParser()
    private val audioSourceParser = AudioSourceParser()
    private val kwsSkipIntervalsParser = KwsSkipIntervalsParser()
    private val audioTrackParser = AudioTrackParser(kwsSkipIntervalsParser, audioSourceParser)
    private val radioParser = RadioParser(kwsSkipIntervalsParser, audioSourceParser)
    private val noiseParser = NoiseParser(kwsSkipIntervalsParser, audioSourceParser)
    private val taleParser = TaleParser(kwsSkipIntervalsParser, audioSourceParser)
    private val playlistParser =
        PlaylistParser(audioTrackParser, radioParser, noiseParser, taleParser)
    private val resultParser = ResultParser(
        suggestsParser,
        kwsSkipIntervalsParser,
        playlistParser,
        audioTrackParser,
        radioParser,
        noiseParser,
        taleParser,
        externalParsersProvider,
        appsRefParser,
        analytics,
        logger
    )

    val kwsStatusRepository = KwsStatusRepository(logger)
    private val kwsSkipController = KwsSkipController(kwsStatusRepository)
    private val musicKwsSkipRepository =
        MusicKwsSkipRepository(messagesRepository, poolDispatcher)
    val commandsMusicController = CommandsMusicController(
        appContext,
        kwsSkipController,
        musicKwsSkipRepository,
        analytics,
        poolDispatcher,
        playerEventRepository,
        logger
    )

    private val advertisingIdAdapter =
        AdvertisingIdAdapter(remoteModule.advertisingIdProvider, logger)
    private val phrasePropertiesProvider = PhrasePropertiesProvider(
        remoteModule.deviceInfoProvider,
        remoteModule.timeZoneProvider,
        advertisingIdAdapter,
        skillServerParamProvider,
        settingsRepository,
        locationProvider,
        remoteModule.mailPhraseParamsProvider,
    )

    val voiceRepository = audioModule.createVoiceRepository()
    private val voiceInteractor = VoiceInteractor(
        voiceRepository,
        clientStateRepository,
        phrasePropertiesProvider
    )

    val playerInteractor = MediaBandwidthMeter(bandwidthMeter)

    private val phraseInteractor: PhraseInteractor = PhraseInteractorImpl(
        sessionProvider,
        remoteModule.phraseApi,
        phrasePropertiesProvider,
        resultParser,
        rtLogModule.devicePhraseExtraDataEvent,
        clientStateRepository,
        analytics
    )

    private val assistantContextRepository = AssistantContextRepository(logger)
    val assistantContextInteractor = AssistantContextInteractor(assistantContextRepository)
    val sessionMusicController =
        AssistantSessionMusicController(
            appContext,
            analytics,
            logger,
            musicPlayerMediaFailureHandler
        )
    private val audioLevelInterceptor = AudioLevelInterceptor(permissionChecker, analytics, logger)
    private val mediaModule =
        MediaModule(appContext, analytics, audioLevelInterceptor, bandwidthMeter, logger)

    private val contactsManager = ContactsManager(appContext.resources, appContext.contentResolver, poolDispatcher.io)
    private val commandsModule = CommandsModule(
        localModule,
        mediaModule,
        analytics,
        sessionProvider,
        phraseInteractor,
        voiceInteractor,
        messagesRepository,
        assistantContextRepository,
        queueProvider,
        permissionChecker,
        poolDispatcher,
        resultParser,
        commandsMusicController,
        kwsSkipController,
        lazyModificationsProvider,
        intentHandlerProvider,
        remoteModule.dataSource,
        splitExperimentParamProvider,
        rtLogModule.devicePhraseExtraDataEvent,
        clientStateRepository,
        contactsManager,
        contactsRepository,
        logger
    )

    val foregroundHandler: ForegroundHandler = ForegroundHandler(playerLimitInteractor)
    val ttsInteractor: TtsInteractor = TtsInteractor(
        TtsAudioLevelInteractor(audioLevelInterceptor),
        mediaModule.ttsPlayer,
        mediaModule.soundStreamPlayer
    )

    val commandsAdapter: CommandsAdapter = CommandsAdapter(
        commandsModule.commandsInteractor
    )

    val serverAvailabilityChecker: ServerAvailabilityInteractor =
        ServerAvailabilityInteractor(remoteModule.dataSource, sessionProvider)

    fun createPendingIntentsProcessor(): PendingIntentsProcessor {
        val connectivityManager = NetworkConnectivityManager(appContext, logger)
        return PendingIntentsProcessor(
            pendingIntentsInteractor,
            commandsModule.commandsInteractor,
            connectivityManager,
            poolDispatcher,
            logger
        )
    }
}