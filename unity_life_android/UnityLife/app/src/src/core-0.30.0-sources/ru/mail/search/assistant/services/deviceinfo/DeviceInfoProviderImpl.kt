package ru.mail.search.assistant.services.deviceinfo

import ru.mail.search.assistant.common.data.remote.DeviceIdProvider
import ru.mail.search.assistant.data.DialogModeProvider

/**
 * Created by kirillf on 10/12/16.
 */
class DeviceInfoProviderImpl(
    private val deviceIdProvider: DeviceIdProvider,
    private val capabilitiesProvider: CapabilitiesProvider,
    private val dialogModeProvider: DialogModeProvider?,
) : DeviceInfoProvider {

    override val deviceId: String get() = deviceIdProvider.deviceId

    override val capabilities: Map<String, Boolean>
        get() = capabilitiesProvider.capabilities

    override val dialogMode: String?
        get() = dialogModeProvider?.getDialogMode()
}
