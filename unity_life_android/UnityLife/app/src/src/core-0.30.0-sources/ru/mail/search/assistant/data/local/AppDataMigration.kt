package ru.mail.search.assistant.data.local

interface AppDataMigration {

    suspend fun migrate(): Boolean
}