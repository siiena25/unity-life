package ru.mail.search.assistant.interactor

import ru.mail.search.assistant.data.local.AssistantMigrationManager

class CoreInteractor(private val migrationManager: AssistantMigrationManager) {

    suspend fun checkCurrentVersion(sourceVersion: Int) {
        migrationManager.checkAndActualizeCurrentVersion(sourceVersion)
    }
}