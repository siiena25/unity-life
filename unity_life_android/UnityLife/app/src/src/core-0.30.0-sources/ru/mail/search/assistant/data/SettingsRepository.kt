package ru.mail.search.assistant.data

import ru.mail.search.assistant.data.local.LocalSettingsDataSource
import ru.mail.search.assistant.entities.Settings
import ru.mail.search.assistant.services.deviceinfo.FeatureProvider

// TODO: EL-3505 refactor SettingsRepository
class SettingsRepository(
    private val local: LocalSettingsDataSource,
    private val featureProvider: FeatureProvider? = null
) {

    fun getKwsAvailability(): Boolean {
        return local.isKwsAvailable
    }

    fun setKwsAvailability(value: Boolean) {
        local.isKwsAvailable = value
    }

    fun setChildModeEnabled(isEnabled: Boolean) {
        local.isChildModeEnabled = isEnabled
    }

    fun getChildModeEnabled(): Boolean? {
        return if (featureProvider?.isChildrenModeAvailable == true) {
            local.isChildModeEnabled
        } else {
            null
        }
    }

    fun getSettings(): Settings {
        return Settings(
            local.isKwsAvailable,
            local.isChildModeEnabled,
        )
    }

    fun setSettings(settings: Settings) {
        local.isKwsAvailable = settings.kwsAvailability
        local.isChildModeEnabled = settings.isChildModeAvailable
    }
}