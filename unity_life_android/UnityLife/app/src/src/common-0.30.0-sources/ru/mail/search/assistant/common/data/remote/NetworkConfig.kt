package ru.mail.search.assistant.common.data.remote

import android.content.Context
import android.content.SharedPreferences
import ru.mail.search.assistant.common.data.remote.error.ClientOutdatedCallback
import ru.mail.search.assistant.common.util.ResourceManager
import ru.mail.search.assistant.common.util.ResourceManagerImpl

class NetworkConfig(
    val appVersionName: String,
    val hostProvider: HostProvider,
    val deviceIdProvider: DeviceIdProvider,
    val clientOutdatedCallback: ClientOutdatedCallback? = null,
) {

    companion object {

        private const val PREFERENCES_NAME_DEFAULT = "my_assistant_preferences"

        /**
         * [deviceUniqueId] - can be obtained using retrieveUniqueId method
         */
        fun createDefault(
            context: Context,
            appVersion: String,
            clientSubtypePlatform: String,
            deviceUniqueId: String,
            clientOutdatedCallback: ClientOutdatedCallback? = null,
            isDebug: Boolean = false
        ): NetworkConfig {
            val resourceManager =
                ResourceManagerImpl(context)
            val hostProvider =
                createDefaultHostProvider(resourceManager)
            val deviceIdProvider = createDefaultDeviceIdProvider(
                clientSubtypePlatform,
                deviceUniqueId,
                isDebug = isDebug
            )
            return NetworkConfig(
                appVersion,
                hostProvider,
                deviceIdProvider,
                clientOutdatedCallback
            )
        }

        /**
         * Generate and store unique id
         */
        fun retrieveUniqueId(context: Context): String {
            val preferences = getDefaultPreferences(context)
            return retrieveUniqueId(preferences)
        }

        /**
         * Generate and store unique id
         */
        fun retrieveUniqueId(preferences: SharedPreferences): String {
            return DeviceUniqueIdProvider(preferences).getId()
        }

        fun createDefaultHostProvider(resourceManager: ResourceManager): HostProvider {
            return ProductionHostProvider(resourceManager)
        }

        fun createDefaultDeviceIdProvider(
            clientSubtypePlatform: String,
            deviceUniqueId: String,
            clientType: String = DeviceIdProvider.CLIENT_TYPE_MOBILE,
            isDebug: Boolean = false
        ): DeviceIdProvider {
            val realm = if (isDebug) {
                DeviceIdProvider.REALM_DEV
            } else {
                DeviceIdProvider.REALM_PRODUCTION
            }
            return DeviceIdProvider(realm, clientSubtypePlatform, deviceUniqueId, clientType)
        }

        private fun getDefaultPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFERENCES_NAME_DEFAULT, Context.MODE_PRIVATE)
        }
    }
}