package ru.mail.search.assistant.voice

import kotlinx.coroutines.flow.Flow
import ru.mail.search.assistant.api.phrase.ActivationType
import ru.mail.search.assistant.api.phrase.PlayerData
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.data.PhrasePropertiesProvider
import ru.mail.search.assistant.voicemanager.PhraseRecordingCallback
import ru.mail.search.assistant.voicemanager.VoiceRecordStatus
import ru.mail.search.assistant.voicemanager.VoiceRepository
import ru.mail.search.assistant.voicemanager.VoiceRepositoryCallbackAdapter
import ru.mail.search.assistant.voicemanager.flowmode.FlowModeRecordingCallback

internal class VoiceInteractor(
    private val voiceRepository: VoiceRepository,
    private val clientStateRepository: ClientStateRepository,
    private val phrasePropertiesProvider: PhrasePropertiesProvider
) {

    fun startPhrase(
        startedManually: Boolean,
        minWaitingTime: Int? = null,
        phraseRequestId: Int,
        playerData: PlayerData?,
        callback: PhraseRecordingCallback,
        activationType: ActivationType? = null,
        callbackData: String? = null
    ) {
        val phraseProperties = phrasePropertiesProvider.getPhraseProperties(
            playerData = playerData,
            refuseTts = false
        )
        voiceRepository.startPhrase(
            startedManually,
            minWaitingTime,
            phraseRequestId,
            phraseProperties,
            clientStateRepository.getPhraseClientState(phraseRequestId),
            callback,
            activationType,
            callbackData
        )
    }

    fun startFlowMode(
        flowModeModel: String,
        playerData: PlayerData?,
        callback: FlowModeRecordingCallback
    ) {
        val phraseProperties = phrasePropertiesProvider.getPhraseProperties(
            playerData = playerData,
            refuseTts = false
        )
        voiceRepository.startFlowMode(flowModeModel, phraseProperties, callback)
    }

    fun cancelRecording() {
        voiceRepository.cancelRecording()
    }

    fun observeStatus(): Flow<VoiceRecordStatus> {
        return VoiceRepositoryCallbackAdapter(voiceRepository)
            .observeStatus()
    }
}