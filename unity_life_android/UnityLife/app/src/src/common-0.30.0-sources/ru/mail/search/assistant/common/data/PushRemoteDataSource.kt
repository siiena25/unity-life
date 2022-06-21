package ru.mail.search.assistant.common.data

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClient
import ru.mail.search.assistant.common.http.assistant.setupJsonBody
import ru.mail.search.assistant.common.http.common.HttpRequestBuilder
import ru.mail.search.assistant.common.util.SecureString

class PushRemoteDataSource(
    private val httpClient: AssistantHttpClient,
    private val timeZoneProvider: TimeZoneProvider,
) {

    private companion object {

        const val ROUTE_SUBSCRIBE = "device/push/subscribe"
        const val ROUTE_UNSUBSCRIBE = "device/push/unsubscribe"
    }

    suspend fun subscribeForPush(
        fcmToken: SecureString? = null,
        gcmKey: String? = null,
    ) {
        httpClient.post(ROUTE_SUBSCRIBE, isAuthorized = true) {
            addTimeZone()
            setupJsonBody {
                fcmToken?.let { addProperty("token", fcmToken.raw) }
                gcmKey?.let { addPushSubscriptionSettings(gcmKey) }
            }
        }
    }

    @Suppress("unused") // sdk
    suspend fun unsubscribeFromPush() {
        httpClient.post(ROUTE_UNSUBSCRIBE, isAuthorized = true) {
            addTimeZone()
        }
    }

    private fun HttpRequestBuilder.addTimeZone() {
        addQueryParameter("timezone", timeZoneProvider.getCurrentTimezone())
    }

    private fun JsonObject.addPushSubscriptionSettings(gcmKey: String) {
        val settingsJson = JsonObject().apply {
            addProperty("gcm_key", gcmKey)
        }
        add("settings", settingsJson)
    }
}