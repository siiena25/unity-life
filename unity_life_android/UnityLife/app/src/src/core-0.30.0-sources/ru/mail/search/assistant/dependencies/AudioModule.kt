package ru.mail.search.assistant.dependencies

import android.content.SharedPreferences
import ru.mail.search.assistant.audiorecorder.session.CommonAudioThreadExecutor
import ru.mail.search.assistant.audition.sending.AudioRecordConfig
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClient
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.data.rtlog.AuditionAnalyticsImpl
import ru.mail.search.assistant.data.rtlog.VoiceManagerAnalyticsImpl
import ru.mail.search.assistant.kws.KeywordSpotter
import ru.mail.search.assistant.voicemanager.AudioConfig
import ru.mail.search.assistant.voicemanager.VoiceManagerComponent
import ru.mail.search.assistant.voicemanager.VoiceRepository
import ru.mail.search.assistant.voicemanager.manager.KwsManager
import ru.mail.search.assistant.voicemanager.manager.VoiceAudioSource

internal class AudioModule(
    rtLogModule: RtLogModule,
    keywordSpotter: KeywordSpotter?,
    clientStateRepository: ClientStateRepository,
    httpClient: AssistantHttpClient,
    sessionProvider: SessionCredentialsProvider,
    private val audioConfig: AudioConfig,
    sharedPreferences: SharedPreferences,
    val logger: Logger?,
    analytics: Analytics?,
    poolDispatcher: PoolDispatcher
) {

    private val config = AudioRecordConfig()
    private val auditionAnalytics = AuditionAnalyticsImpl(rtLogModule.deviceChunksExtraDataEvent)
    private val voiceManagerAnalytics = VoiceManagerAnalyticsImpl(
        config,
        clientStateRepository,
        rtLogModule.deviceChunksExtraDataEvent,
        rtLogModule.devicePhraseExtraDataEvent
    )

    val audioThreadExecutor = CommonAudioThreadExecutor()
    val kwsManager: KwsManager get() = voiceManagerComponent.kwsManager
    val audioSource: VoiceAudioSource get() = voiceManagerComponent.audioSource

    private val voiceManagerComponent: VoiceManagerComponent by lazy {
        VoiceManagerComponent(
            keywordSpotter,
            httpClient,
            sessionProvider,
            sharedPreferences,
            poolDispatcher,
            logger,
            analytics,
            voiceManagerAnalytics,
            auditionAnalytics,
            audioConfig
        )
    }

    fun createVoiceRepository(): VoiceRepository {
        return voiceManagerComponent.createVoiceRepository()
    }
}
