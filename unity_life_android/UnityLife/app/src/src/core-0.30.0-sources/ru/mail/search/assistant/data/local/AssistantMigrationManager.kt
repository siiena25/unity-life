package ru.mail.search.assistant.data.local

import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.util.Tag
import ru.mail.search.assistant.util.analytics.event.AssistantError
import ru.mail.search.assistant.util.analytics.logAssistantError

class AssistantMigrationManager(
    private val settings: LocalSettingsDataSource,
    private val analytics: Analytics?,
    private val logger: Logger?
) {

    private val migrations: Map<Int, Lazy<AppDataMigration>> = mapOf()

    suspend fun checkAndActualizeCurrentVersion(sourceVersion: Int) {
        val currentVersion = settings.appDataVersion
        if (currentVersion < 0) {
            logger?.i(
                Tag.MIGRATION,
                "Assistant set migrate version $sourceVersion for clear install"
            )
            setCurrentVersion(sourceVersion)
        } else if (currentVersion < sourceVersion) {
            migrate(currentVersion, sourceVersion)
        }
    }

    private suspend fun migrate(from: Int, to: Int): Boolean {
        migrations.asSequence()
            .dropWhile { (version, _) -> version <= from }
            .takeWhile { (version, _) -> version <= to }
            .forEach { (version, migration) ->
                logger?.i(
                    Tag.MIGRATION,
                    "Assistant start migration from version:$version -> $migration"
                )
                if (executeMigration(migration.value, version)) {
                    setCurrentVersion(version)
                } else {
                    return false
                }
            }
        return true
    }

    private fun setCurrentVersion(version: Int) {
        settings.appDataVersion = version
    }

    private suspend fun executeMigration(migration: AppDataMigration, version: Int): Boolean {
        return runCatching { migration.migrate() }
            .onFailure { error ->
                logger?.e(Tag.MIGRATION, error)
                analytics?.logAssistantError(
                    AssistantError.Module.MIGRATION,
                    "Failed assistant migration on version $version (%s)",
                    error
                )
            }
            .getOrDefault(false)
    }
}