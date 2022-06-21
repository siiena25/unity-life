package ru.mail.search.assistant.interactor

import ru.mail.search.assistant.api.phrase.PlayerData
import ru.mail.search.assistant.entities.CommonPhraseResult

/**
 * Created by Grigory Fedorov on 23.10.18.
 */
interface PhraseInteractor {

    suspend fun sendTextPhrase(
        text: String,
        callbackData: String? = null,
        clientData: String? = null,
        playerData: PlayerData? = null,
        refuseTts: Boolean? = null,
        phraseRequestId: Int? = null
    ): CommonPhraseResult

    suspend fun sendEvent(
        event: String,
        callbackData: String? = null,
        clientData: String? = null,
        playerData: PlayerData? = null,
        refuseTts: Boolean? = null,
        params: Map<String, String> = emptyMap(),
        phraseRequestId: Int? = null
    ): CommonPhraseResult

    suspend fun sendPushPayload(
        pushId: String?,
        callbackData: String,
        refuseTts: Boolean? = null,
        phraseRequestId: Int? = null
    ): CommonPhraseResult
}