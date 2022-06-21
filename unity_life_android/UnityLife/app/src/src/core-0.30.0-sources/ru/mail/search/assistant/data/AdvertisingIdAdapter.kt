package ru.mail.search.assistant.data

import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.services.deviceinfo.AdvertisingIdProvider

internal class AdvertisingIdAdapter(
    private val advertisingIdProvider: AdvertisingIdProvider?,
    private val logger: Logger?
) {

    companion object {
        private const val TAG = "AdvertisingIdProvider"
    }

    val advertisingId by lazy {
        runCatching {
            advertisingIdProvider?.getAdvertisingId()
        }.onFailure { error ->
            logger?.e(TAG, error, "Error getting advertising id")
        }.getOrNull()
    }
}
