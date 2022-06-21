package ru.mail.search.assistant

import android.content.SharedPreferences
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.data.locating.LocationProvider
import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.core.CoreCommandsInteractor
import ru.mail.search.assistant.core.MarusiaFacilities
import ru.mail.search.assistant.data.AssistantSettings
import ru.mail.search.assistant.data.DeveloperConfig
import ru.mail.search.assistant.data.MailPhraseParamsProvider
import ru.mail.search.assistant.data.MarusiaSettings
import ru.mail.search.assistant.data.local.auth.AssistantCipherAdapter
import ru.mail.search.assistant.data.local.messages.MessagesStorage
import ru.mail.search.assistant.dependencies.CoreModule
import ru.mail.search.assistant.interactor.AssistantMessagesInteractor
import ru.mail.search.assistant.kws.KeywordSpotter
import ru.mail.search.assistant.services.deviceinfo.AdvertisingIdProvider
import ru.mail.search.assistant.services.notification.PlayerNotificationManager
import ru.mail.search.assistant.services.notification.PlayerNotificationResourcesProvider
import ru.mail.search.assistant.voicemanager.AudioConfig
import ru.mail.search.assistant.voicemanager.manager.KwsManager
import java.util.concurrent.atomic.AtomicReference

class AssistantCore internal constructor(
    appProperties: Assistant.AppProperties,
    networkConfig: NetworkConfig,
    sessionProvider: SessionCredentialsProvider,
    locationProvider: LocationProvider?,
    analytics: Analytics?,
    cipher: AssistantCipherAdapter?,
    preferences: SharedPreferences?,
    playerNotificationManager: PlayerNotificationManager?,
    notificationResourcesProvider: PlayerNotificationResourcesProvider,
    splitExperimentParamProvider: SplitExperimentParamProvider?,
    keywordSpotter: KeywordSpotter?,
    httpClient: HttpClient?,
    developerConfig: DeveloperConfig,
    audioConfig: AudioConfig,
    val logger: Logger?,
    messagesStorage: MessagesStorage?,
    advertisingIdProvider: AdvertisingIdProvider?,
    private val onRelease: (AssistantCore) -> Unit,
    mailPhraseParamsProvider: MailPhraseParamsProvider?,
) {

    private val moduleComponent = CoreModuleComponent(
        appProperties,
        networkConfig,
        sessionProvider,
        locationProvider,
        analytics,
        cipher,
        preferences,
        playerNotificationManager,
        notificationResourcesProvider,
        splitExperimentParamProvider,
        keywordSpotter,
        httpClient,
        developerConfig,
        audioConfig,
        logger,
        messagesStorage,
        advertisingIdProvider,
        mailPhraseParamsProvider,
    )
    internal val module: CoreModule by lazy { moduleComponent.require() }

    private val currentSession = AtomicReference<AssistantSession?>(null)

    val commands: CoreCommandsInteractor get() = module.coreCommandsInteractor
    val sdkSettings: AssistantSettings get() = module.assistantSettings
    val marusiaSettings: MarusiaSettings get() = module.marusiaSettings
    val messages: AssistantMessagesInteractor get() = module.assistantMessagesInteractor
    val developerMenu: DeveloperMenuInteractor get() = module.developerMenuInteractor
    val deviceStatEvents: DeviceStatEventHandler get() = module.deviceStatEventHandler
    val marusiaFacilities: MarusiaFacilities get() = module.marusiaFacilities
    val kwsManager: KwsManager get() = module.kwsManager

    fun release() {
        currentSession.getAndSet(null)?.release()
        moduleComponent.close()
        onRelease(this)
    }

    fun init() {
        module
    }

    fun createSession(
        modificationsProvider: ModificationsProvider = EmptyModificationsProvider(),
        intentHandlerProvider: AssistantIntentHandlerProvider? = null
    ): AssistantSession {
        val session = AssistantSession(
            this@AssistantCore,
            modificationsProvider,
            intentHandlerProvider,
            logger,
            ::onSessionReleased
        )
        currentSession.set(session)
        return session
    }

    fun getCurrentSession(): AssistantSession? {
        return currentSession.get()
    }

    private fun onSessionReleased(session: AssistantSession) {
        currentSession.compareAndSet(session, null)
    }
}