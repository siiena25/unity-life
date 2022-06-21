package ru.mail.search.assistant.interactor

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.api.phrase.PhraseApi
import ru.mail.search.assistant.api.phrase.PlayerData
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.logParsingError
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.toObject
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.data.PhrasePropertiesProvider
import ru.mail.search.assistant.data.remote.parser.ResultParser
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.CommonPhraseResult
import ru.mail.search.assistant.entities.PhraseMetadata

internal class PhraseInteractorImpl(
    private val sessionProvider: SessionCredentialsProvider,
    private val phraseApi: PhraseApi,
    private val phrasePropertiesProvider: PhrasePropertiesProvider,
    private val resultParser: ResultParser,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val clientStateRepository: ClientStateRepository,
    private val analytics: Analytics?
) : PhraseInteractor {

    override suspend fun sendTextPhrase(
        text: String,
        callbackData: String?,
        clientData: String?,
        playerData: PlayerData?,
        refuseTts: Boolean?,
        phraseRequestId: Int?
    ): CommonPhraseResult {
        val credentials = sessionProvider.getCredentials()
        val clientState = phraseRequestId?.let(clientStateRepository::getPhraseClientState)
        val properties = phrasePropertiesProvider.getPhraseProperties(playerData, refuseTts)
        return executeCommonPhrase(phraseRequestId, PhraseApi.ROUTE_PHRASE_CREATE_TEXT) {
            val response = phraseApi.createTextPhrase(
                text = text,
                credentials = credentials,
                properties = properties,
                clientData = clientData,
                callbackData = callbackData,
                clientState = clientState
            )
            parsePhraseResultCommands(response)
        }
    }

    override suspend fun sendEvent(
        event: String,
        callbackData: String?,
        clientData: String?,
        playerData: PlayerData?,
        refuseTts: Boolean?,
        params: Map<String, String>,
        phraseRequestId: Int?
    ): CommonPhraseResult {
        val credentials = sessionProvider.getCredentials()
        val clientState = phraseRequestId?.let(clientStateRepository::getPhraseClientState)
        val properties = phrasePropertiesProvider.getPhraseProperties(playerData, refuseTts)
        return executeCommonPhrase(phraseRequestId, PhraseApi.ROUTE_PHRASE_CREATE_EVENT) {
            val response = phraseApi.createEvent(
                event = event,
                credentials = credentials,
                properties = properties,
                callbackData = callbackData,
                clientData = clientData,
                clientState = clientState,
                params = params
            )
            parsePhraseResultCommands(response)
        }
    }

    override suspend fun sendPushPayload(
        pushId: String?,
        callbackData: String,
        refuseTts: Boolean?,
        phraseRequestId: Int?
    ): CommonPhraseResult {
        val credentials = sessionProvider.getCredentials()
        val clientState = phraseRequestId?.let(clientStateRepository::getPhraseClientState)
        val properties = phrasePropertiesProvider.getPhraseProperties(null, refuseTts)
        return executeCommonPhrase(phraseRequestId, PhraseApi.ROUTE_PUSH_PAYLOAD) {
            val response = phraseApi.createPushPayload(
                credentials = credentials,
                pushId = pushId,
                properties = properties,
                callbackData = callbackData,
                clientState = clientState
            )
            parsePushResultCommands(response)
        }
    }

    private inline fun executeCommonPhrase(
        phraseRequestId: Int?,
        phraseRoute: String,
        request: () -> CommonPhraseResult
    ): CommonPhraseResult {
        phraseRequestId?.let(rtLogDevicePhraseExtraDataEvent::onResultRequested)
        return runCatching { request() }
            .onFailure { error ->
                if (error is ResultParsingException) {
                    analytics?.logParsingError("POST", phraseRoute)
                }
            }
            .onSuccess { result ->
                phraseRequestId?.let {
                    clientStateRepository.onPhraseCreated(phraseRequestId, result.metadata.phraseId)
                    rtLogDevicePhraseExtraDataEvent
                        .onCommonResultReceived(phraseRequestId, result.metadata.phraseId)
                }
            }
            .getOrThrow()
    }

    private fun parsePhraseResultCommands(response: String): CommonPhraseResult {
        val resultJson = parseResultJson(response)
        val phraseId = parsePhraseId(resultJson)
        val phraseResultJson = parsePhraseResult(resultJson)
        val skill = phraseResultJson?.let(::parseSkill)
        val metaInfo = PhraseMetadata(phraseId = phraseId, skill = skill)
        val commands = phraseResultJson?.let(resultParser::parseCommands).orEmpty()
        return CommonPhraseResult(metaInfo, commands)
    }

    private fun parsePushResultCommands(response: String): CommonPhraseResult {
        val resultJson = parseResultJson(response)
        val phraseId = parsePhraseId(resultJson)
        val skill = parseSkill(resultJson)
        val metaInfo = PhraseMetadata(phraseId = phraseId, skill = skill)
        val commands = resultParser.parseCommands(resultJson)
        return CommonPhraseResult(metaInfo, commands)
    }

    private fun parseResultJson(response: String): JsonObject {
        return JsonParser.parseString(response).toObject()?.getObject("result")
            ?: throw ResultParsingException("Can't parse phrase result")
    }

    private fun parsePhraseId(resultJson: JsonObject): String {
        return resultJson.getString("phrase_id")
            ?: throw ResultParsingException("Can't parse phrase id")
    }

    private fun parsePhraseResult(resultJson: JsonObject): JsonObject? {
        return resultJson.getObject("phrase_result")
    }

    private fun parseSkill(phraseResultJson: JsonObject): String? {
        return phraseResultJson.getString("skill")
    }
}