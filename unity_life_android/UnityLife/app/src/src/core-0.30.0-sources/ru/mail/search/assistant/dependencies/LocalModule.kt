package ru.mail.search.assistant.dependencies

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.ResourceManagerImpl
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.PlayerLimitRepository
import ru.mail.search.assistant.data.SettingsRepository
import ru.mail.search.assistant.data.local.AssistantMigrationManager
import ru.mail.search.assistant.data.local.LocalSettingsDataSource
import ru.mail.search.assistant.data.local.PlayerLimitDataSource
import ru.mail.search.assistant.data.local.TimeBasedMessageUuidProvider
import ru.mail.search.assistant.data.local.auth.AssistantCipherAdapter
import ru.mail.search.assistant.data.local.auth.RawDataFallback
import ru.mail.search.assistant.data.local.messages.MessagesStorage
import ru.mail.search.assistant.data.news.NewsSourcesLocalDataSource
import ru.mail.search.assistant.services.deviceinfo.FeatureProvider

class LocalModule(
    appContext: Context,
    cipher: AssistantCipherAdapter,
    preferences: SharedPreferences?,
    rawDataFallback: RawDataFallback,
    analytics: Analytics?,
    featureProvider: FeatureProvider?,
    logger: Logger?,
    val messageStorage: MessagesStorage
) {

    val messageUuidProvider = TimeBasedMessageUuidProvider()

    val assistantPreferences: SharedPreferences =
        preferences ?: createDefaultPreferences(appContext)
    val settingsDataSource = LocalSettingsDataSource(assistantPreferences)
    val settingsRepository = SettingsRepository(settingsDataSource, featureProvider)

    val migrationManager = AssistantMigrationManager(settingsDataSource, analytics, logger)

    private val playerLimitDataSource = PlayerLimitDataSource(assistantPreferences)
    val playerLimitRepository = PlayerLimitRepository(
        playerLimitDataSource,
        cipher,
        rawDataFallback
    )
    val newsSourceLocalDataSource = NewsSourcesLocalDataSource(
        assistantPreferences,
        ResourceManagerImpl(appContext)
    )

    private fun createDefaultPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}