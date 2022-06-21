package ru.mail.search.assistant

import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.PlayerEventRepository
import ru.mail.search.assistant.dependencies.session.SessionScopeModule
import ru.mail.search.assistant.interactor.AssistantContextInteractor
import ru.mail.search.assistant.interactor.CommandsAdapter
import ru.mail.search.assistant.interactor.ServerAvailabilityInteractor
import ru.mail.search.assistant.interactor.TtsInteractor
import ru.mail.search.assistant.media.MediaBandwidthMeter
import ru.mail.search.assistant.session.KwsStatusInteractor
import ru.mail.search.assistant.util.UnstableAssistantApi
import ru.mail.search.assistant.voicemanager.VoiceRepository

class AssistantSession(
    val core: AssistantCore,
    modificationsProvider: ModificationsProvider,
    intentHandlerProvider: AssistantIntentHandlerProvider?,
    logger: Logger?,
    private val onRelease: (AssistantSession) -> Unit
) {
    private val sessionComponent =
        SessionModuleComponent(core, modificationsProvider, intentHandlerProvider, logger)
    private val module: SessionScopeModule by lazy { sessionComponent.require() }
    private val pendingIntentsProcessor by lazy { module.createPendingIntentsProcessor() }

    val interaction: AssistantContextInteractor
        get() = module.assistantContextInteractor

    @UnstableAssistantApi
    val voice: VoiceRepository
        get() = module.voiceRepository

    @UnstableAssistantApi
    val kwsStatus: KwsStatusInteractor
        get() = module.kwsStatusRepository

    val foregroundHandler: ForegroundHandler
        get() = module.foregroundHandler

    val tts: TtsInteractor
        get() = module.ttsInteractor

    val playerEvents: PlayerEventRepository
        get() = module.playerEventRepository

    val commands: CommandsAdapter
        get() = module.commandsAdapter

    val serverAvailability: ServerAvailabilityInteractor
        get() = module.serverAvailabilityChecker

    val media: MediaBandwidthMeter
        get() = module.playerInteractor

    fun release() {
        voice.release()
        sessionComponent.close()
        pendingIntentsProcessor.release()
        onRelease(this)
    }

    fun startPendingIntentsProcessor() {
        pendingIntentsProcessor.start()
    }

    fun stopPendingIntentsProcessor() {
        pendingIntentsProcessor.stop()
    }
}