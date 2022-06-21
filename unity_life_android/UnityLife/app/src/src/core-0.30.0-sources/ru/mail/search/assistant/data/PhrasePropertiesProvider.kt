package ru.mail.search.assistant.data

import ru.mail.search.assistant.SkillServerParamProvider
import ru.mail.search.assistant.api.phrase.PhraseProperties
import ru.mail.search.assistant.api.phrase.PlayerData
import ru.mail.search.assistant.common.data.TimeZoneProvider
import ru.mail.search.assistant.common.data.locating.LocationProvider
import ru.mail.search.assistant.services.deviceinfo.DeviceInfoProvider

internal class PhrasePropertiesProvider(
    private val deviceInfoProvider: DeviceInfoProvider,
    private val timeZoneProvider: TimeZoneProvider,
    private val advertisingIdAdapter: AdvertisingIdAdapter?,
    private val skillServerParamProvider: SkillServerParamProvider,
    private val settingsRepository: SettingsRepository,
    private val locationProvider: LocationProvider?,
    private val mailPhraseParamsProvider: MailPhraseParamsProvider?
) {

    fun getPhraseProperties(playerData: PlayerData?, refuseTts: Boolean?): PhraseProperties {
        return PhraseProperties(
            capabilities = deviceInfoProvider.capabilities,
            timeZone = timeZoneProvider.getCurrentTimezone(),
            playerData = playerData,
            refuseTts = refuseTts,
            childMode = settingsRepository.getChildModeEnabled(),
            selectedSkillServer = skillServerParamProvider.getSkillServerIfNotDefault(),
            advertisingId = advertisingIdAdapter?.advertisingId,
            location = locationProvider?.getLocation(),
            dialogMode = deviceInfoProvider.dialogMode,
            mailPhraseParams = mailPhraseParamsProvider?.getMailPhraseParams(),
        )
    }
}