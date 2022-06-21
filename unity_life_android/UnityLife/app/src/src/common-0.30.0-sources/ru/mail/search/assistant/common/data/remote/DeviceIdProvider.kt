package ru.mail.search.assistant.common.data.remote

/**
 * Provider for device id used.
 *
 * [realm]
 * [REALM_PRODUCTION] - production application value
 * [REALM_DEV] - realm for application development
 *
 * [clientSubtypePlatform] - unique sdk client type
 * [deviceUniqueId] - unique device id
 */
class DeviceIdProvider(
    private val realm: String,
    private val clientSubtypePlatform: String,
    private val deviceUniqueId: String,
    private val clientType: String = CLIENT_TYPE_MOBILE
) {

    companion object {

        const val REALM_PRODUCTION = "c"
        const val REALM_DEV = "d"
        const val CLIENT_TYPE_MOBILE = "m"
    }

    val deviceId: String by lazy { ":$realm:$clientType:$clientSubtypePlatform:$deviceUniqueId" }
}