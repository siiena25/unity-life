package ru.mail.search.assistant

import android.content.SharedPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.data.locating.LocationProvider
import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.DeveloperConfig
import ru.mail.search.assistant.data.MailPhraseParamsProvider
import ru.mail.search.assistant.data.local.auth.AssistantCipherAdapter
import ru.mail.search.assistant.data.local.messages.MessagesStorage
import ru.mail.search.assistant.dependencies.CoreModule
import ru.mail.search.assistant.kws.KeywordSpotter
import ru.mail.search.assistant.services.deviceinfo.AdvertisingIdProvider
import ru.mail.search.assistant.services.notification.PlayerNotificationManager
import ru.mail.search.assistant.services.notification.PlayerNotificationResourcesProvider
import ru.mail.search.assistant.util.Tag
import ru.mail.search.assistant.voicemanager.AudioConfig

internal class CoreModuleComponent(
    private val appProperties: Assistant.AppProperties,
    private val networkConfig: NetworkConfig,
    private val sessionProvider: SessionCredentialsProvider,
    private val locationProvider: LocationProvider?,
    private val analytics: Analytics?,
    private val cipher: AssistantCipherAdapter?,
    private val preferences: SharedPreferences?,
    private val playerNotificationManager: PlayerNotificationManager?,
    private val notificationResourcesProvider: PlayerNotificationResourcesProvider,
    private val splitExperimentParamProvider: SplitExperimentParamProvider?,
    private val keywordSpotter: KeywordSpotter?,
    private val httpClient: HttpClient?,
    private val developerConfig: DeveloperConfig,
    private val audioConfig: AudioConfig,
    private val logger: Logger?,
    private val messageStorage: MessagesStorage?,
    private val advertisingIdProvider: AdvertisingIdProvider?,
    private val mailPhraseParamsProvider: MailPhraseParamsProvider?,
) : LazyComponent<CoreModule>() {

    private companion object {

        private const val CURRENT_DATA_VERSION = 4
    }

    override fun create(): CoreModule {
        logger?.d(Tag.ASSISTANT_CORE, "On create assistant core component")
        return CoreModule(
            appProperties,
            networkConfig,
            sessionProvider,
            analytics,
            locationProvider,
            cipher,
            preferences,
            playerNotificationManager,
            notificationResourcesProvider,
            splitExperimentParamProvider,
            keywordSpotter,
            httpClient,
            developerConfig,
            audioConfig,
            advertisingIdProvider,
            logger,
            messageStorage,
            mailPhraseParamsProvider,
        ).apply {
            initialize()
        }
    }

    private fun CoreModule.initialize() {
        runBlocking {
            coreInteractor.checkCurrentVersion(CURRENT_DATA_VERSION)
        }
        assistantMusicController.connect()
    }

    override fun onClose(component: CoreModule) {
        GlobalScope.launch {
            component.assistantMusicController.disconnect()
            component.pendingIntentsInteractor.release()
            component.musicPlayerMediaFailureHandler.release()
        }
        component.coreCoroutineContext.cancel()
        component.audioModule.audioSource.release()
        component.audioModule.audioThreadExecutor.release()
        logger?.d(Tag.ASSISTANT_CORE, "On close assistant core component")
    }
}