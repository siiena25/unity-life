package ru.mail.search.assistant.data.local.db

import androidx.room.migration.Migration
import ru.mail.search.assistant.common.util.Logger

abstract class LoggedMigration(
    startVersion: Int,
    endVersion: Int
) : Migration(startVersion, endVersion) {

    protected var logger: Logger? = null
        private set

    fun setLogger(logger: Logger) {
        this.logger = logger
    }
}