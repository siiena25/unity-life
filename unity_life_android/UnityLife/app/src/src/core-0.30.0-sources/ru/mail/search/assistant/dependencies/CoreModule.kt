package ru.mail.search.assistant.dependencies

import android.content.SharedPreferences
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.newSingleThreadContext
import ru.mail.search.assistant.*
import ru.mail.search.assistant.api.suggests.SkillListDataSource
import ru.mail.search.assistant.api.suggests.SuggestsParser
import ru.mail.search.assistant.commands.main.AssistantCommandQueueProvider
import ru.mail.search.assistant.commands.main.skipkws.MusicPlayerMediaFailureHandler
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.data.TimeZoneProvider
import ru.mail.search.assistant.common.data.locating.LocationProvider
import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.schedulers.PoolDispatcherFactory
import ru.mail.search.assistant.common.ui.PermissionManager
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.ResourceManager
import ru.mail.search.assistant.common.util.ResourceManagerImpl
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.core.CoreCommandsInteractor
import ru.mail.search.assistant.core.MarusiaFacilities
import ru.mail.search.assistant.data.*
import ru.mail.search.assistant.data.local.auth.AssistantCipherAdapter
import ru.mail.search.assistant.data.local.auth.RawDataFallback
import ru.mail.search.assistant.data.local.auth.SimpleCipher
import ru.mail.search.assistant.data.local.db.AssistantDatabase
import ru.mail.search.assistant.data.local.messages.MessageEntityConverter
import ru.mail.search.assistant.data.local.messages.MessagesStorage
import ru.mail.search.assistant.data.local.messages.RoomMessagesStorage
import ru.mail.search.assistant.data.news.NewsSourcesRepository
import ru.mail.search.assistant.data.player.PlayerStatusRepository
import ru.mail.search.assistant.dependencies.session.SessionScopeModule
import ru.mail.search.assistant.interactor.AssistantMessagesInteractor
import ru.mail.search.assistant.interactor.CoreInteractor
import ru.mail.search.assistant.interactor.PendingIntentsInteractor
import ru.mail.search.assistant.interactor.PlayerLimitInteractor
import ru.mail.search.assistant.kws.KeywordSpotter
import ru.mail.search.assistant.media.AssistantBandwidthMeter
import ru.mail.search.assistant.services.deviceinfo.AdvertisingIdProvider
import ru.mail.search.assistant.services.deviceinfo.DeviceInfoProvider
import ru.mail.search.assistant.services.deviceinfo.DeviceInfoProviderImpl
import ru.mail.search.assistant.services.music.MusicMediaSourceCacheProvider
import ru.mail.search.assistant.services.notification.PlayerNotificationManager
import ru.mail.search.assistant.services.notification.PlayerNotificationResourcesProvider
import ru.mail.search.assistant.voicemanager.AudioConfig

internal class CoreModule(
    appProperties: Assistant.AppProperties,
    networkConfig: NetworkConfig,
    private val sessionProvider: SessionCredentialsProvider,
    externalAnalytics: Analytics?,
    private val locationProvider: LocationProvider?,
    externalCipher: AssistantCipherAdapter?,
    preferences: SharedPreferences?,
    val playerNotificationManager: PlayerNotificationManager?,
    val notificationResourcesProvider: PlayerNotificationResourcesProvider,
    private val splitExperimentParamProvider: SplitExperimentParamProvider?,
    keywordSpotter: KeywordSpotter?,
    httpClient: HttpClient?,
    private val developerConfig: DeveloperConfig,
    private val audioConfig: AudioConfig,
    advertisingIdProvider: AdvertisingIdProvider?,
    val logger: Logger?,
    externalMessagesStorage: MessagesStorage?,
    mailPhraseParamsProvider: MailPhraseParamsProvider?,
) {

    val analytics = externalAnalytics
    private val appContext = appProperties.context
    private val cipher = externalCipher ?: createCipher()
    private val rawDataFallback = RawDataFallback(logger)

    val resourceManager: ResourceManager = ResourceManagerImpl(appContext)
    val poolDispatcher = PoolDispatcherFactory().createPoolDispatcher()
    val coreCoroutineContext = SupervisorJob()

    private val database = AssistantDatabase.getInstance(appContext, logger)

    private val messagesRepository = if (externalMessagesStorage == null) {
        val messagesDao = database.getMessagesDao()
        RoomMessagesStorage(messagesDao, MessageEntityConverter())
    } else {
        externalMessagesStorage
    }

    private val localModule = LocalModule(
        appContext,
        cipher,
        preferences,
        rawDataFallback,
        analytics,
        appProperties.featureProvider,
        logger,
        this.messagesRepository
    )

    private val queueProvider = AssistantCommandQueueProvider(poolDispatcher, logger)
    private val deviceInfoProvider: DeviceInfoProvider = DeviceInfoProviderImpl(
        networkConfig.deviceIdProvider,
        appProperties.capabilitiesProvider,
        appProperties.dialogModeProvider,
    )

    private val timeZoneProvider = TimeZoneProvider()

    private val remoteModule = RemoteModule(
        networkConfig,
        analytics,
        timeZoneProvider,
        deviceInfoProvider,
        sessionProvider,
        advertisingIdProvider,
        httpClient,
        logger,
        mailPhraseParamsProvider,
    )
    private val rtLogModule = RtLogModule(
        remoteModule,
        appProperties.featureProvider,
        poolDispatcher,
        logger
    )

    private val newsRepository = NewsSourcesRepository(
        localModule.newsSourceLocalDataSource,
        remoteModule.newsSourceDataSource
    )

    val playerEventRepository = PlayerEventRepository(
        sessionProvider,
        remoteModule.playerDeviceStatDataSource,
        logger,
        poolDispatcher
    )

    val playerStatusRepository = PlayerStatusRepository(
        sessionProvider,
        remoteModule.playerStatusDataSource,
        poolDispatcher,
        logger
    )

    val messageRepository = MessagesRepository(
        localModule.messageStorage,
        localModule.messageUuidProvider
    )

    private val contactsRepository = ContactsRepository(database)

    private val clientStateRepository = ClientStateRepository(
        permissionManager = PermissionManager(appContext, preferences)
    )

    val audioModule: AudioModule = AudioModule(
        rtLogModule,
        keywordSpotter,
        clientStateRepository,
        remoteModule.assistantHttpClient,
        sessionProvider,
        audioConfig,
        localModule.assistantPreferences,
        logger,
        analytics,
        poolDispatcher
    )

    val pendingIntentsInteractor = PendingIntentsInteractor(poolDispatcher)

    val assistantMusicController = AssistantMusicController(
        appContext,
        analytics,
        playerEventRepository,
        logger
    )

    val musicMediaSourceCacheProvider =
        MusicMediaSourceCacheProvider(appContext, localModule.settingsDataSource)
    val assistantSettings = AssistantSettings(
        appContext,
        localModule.settingsDataSource,
        messageRepository,
        musicMediaSourceCacheProvider,
        settingsRepository
    )
    val marusiaSettings = MarusiaSettings(
        newsRepository,
        remoteModule.shuffleRemoteDataSource
    )
    val coreInteractor = CoreInteractor(localModule.migrationManager)
    val coreCommandsInteractor = CoreCommandsInteractor(queueProvider, pendingIntentsInteractor)
    val assistantMessagesInteractor = AssistantMessagesInteractor(
        messageRepository,
        assistantMusicController
    )
    val settingsRepository: SettingsRepository get() = localModule.settingsRepository

    val messagesStorage: MessagesStorage get() = localModule.messageStorage
    val playerLimitInteractor = PlayerLimitInteractor(
        localModule.playerLimitRepository,
        analytics,
        poolDispatcher,
        logger
    )
    val musicPlayerMediaFailureHandler =
        MusicPlayerMediaFailureHandler(pendingIntentsInteractor, poolDispatcher, logger)
    val developerMenuInteractor = DeveloperMenuInteractor(
        settingsRepository,
        locationProvider,
        deviceInfoProvider,
        playerLimitInteractor,
        networkConfig.appVersionName
    )

    private val deviceStateEventRepository = DeviceStatEventRepository(
        sessionProvider,
        remoteModule.deviceStatDataSource,
        poolDispatcher,
        logger
    )
    val deviceStatEventHandler =
        DeviceStatEventHandler(rtLogModule.rtLog, deviceStateEventRepository)

    private val skillServerParamProvider = SkillServerParamProvider(developerConfig)
    private val suggestsParser = SuggestsParser(resourceManager)
    private val skillListDataSource = SkillListDataSource(
        sessionProvider,
        remoteModule.phraseApi,
        timeZoneProvider,
        locationProvider,
        suggestsParser
    )
    private val skillListRepository = SkillListRepository(
        skillListDataSource,
        deviceInfoProvider,
        settingsRepository,
        skillServerParamProvider
    )
    val marusiaFacilities = MarusiaFacilities(skillListRepository)

    val kwsManager get() = audioModule.kwsManager

    val backgroundMusicDataSource get() = remoteModule.backgroundMusicDataSource

    val bandwidthMeter = AssistantBandwidthMeter(appContext)

    fun createSessionScope(
        modificationsProvider: ModificationsProvider,
        intentHandlerProvider: AssistantIntentHandlerProvider?,
    ): SessionScopeModule {
        return SessionScopeModule(
            appContext,
            analytics,
            poolDispatcher,
            resourceManager,
            localModule,
            remoteModule,
            messageRepository,
            queueProvider,
            pendingIntentsInteractor,
            playerLimitInteractor,
            musicPlayerMediaFailureHandler,
            playerEventRepository,
            skillServerParamProvider,
            modificationsProvider,
            intentHandlerProvider,
            sessionProvider,
            splitExperimentParamProvider,
            rtLogModule,
            audioModule,
            settingsRepository,
            clientStateRepository,
            locationProvider,
            bandwidthMeter,
            contactsRepository,
            logger
        )
    }

    private fun createCipher(): AssistantCipherAdapter {
        val dispatcher = newSingleThreadContext("pool-el-thread-9")
        return AssistantCipherAdapter(SimpleCipher(appContext), dispatcher)
    }
}