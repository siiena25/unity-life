package ru.mail.search.assistant.data

import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mail.search.assistant.api.statistics.devicestat.DeviceStatDataSource
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.isCausedByPoorNetworkConnection
import ru.mail.search.assistant.entities.DeviceStatEvent

class DeviceStatEventRepository(
    private val sessionProvider: SessionCredentialsProvider,
    private val deviceStatDataSource: DeviceStatDataSource,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    private companion object {

        private const val TAG = "DeviceStatEvent"
    }

    fun sendEvent(event: DeviceStatEvent) {
        GlobalScope.launch(poolDispatcher.io) {
            runCatching {
                val credentials = sessionProvider.getCredentials()
                val eventJson = JsonObject().apply {
                    add("data", JsonObject().apply {
                        event.data.map {
                            addProperty(it.key, it.value)
                        }
                    })
                }
                deviceStatDataSource.sendEvent(credentials, event.timestamp, event.type, eventJson)
            }.onFailure { error ->
                if (error !is CancellationException && !error.isCausedByPoorNetworkConnection()) {
                    logger?.e(TAG, error, "Failed to send device stat event")
                }
            }
        }
    }
}