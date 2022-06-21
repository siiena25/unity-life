package ru.mail.search.assistant.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.local.db.converter.ApiHostTypeConverter
import ru.mail.search.assistant.data.local.db.converter.DateTypeConverter
import ru.mail.search.assistant.data.local.db.converter.SessionTypeConverter
import ru.mail.search.assistant.data.local.db.dao.AssistantReminderDao
import ru.mail.search.assistant.data.local.db.dao.ContactsDao
import ru.mail.search.assistant.data.local.db.dao.MessagesDao
import ru.mail.search.assistant.data.local.db.dao.SessionDao
import ru.mail.search.assistant.data.local.db.entity.AssistantReminderEntity
import ru.mail.search.assistant.data.local.db.entity.MessageEntity
import ru.mail.search.assistant.data.local.db.entity.SessionEntity
import ru.mail.search.assistant.data.local.db.entity.contacts.ContactEntity
import ru.mail.search.assistant.data.local.db.entity.contacts.PhoneNumberEntity

@Database(
    entities = [
        MessageEntity::class,
        SessionEntity::class,
        AssistantReminderEntity::class,
        ContactEntity::class,
        PhoneNumberEntity::class,
    ],
    version = 7
)
@TypeConverters(ApiHostTypeConverter::class, DateTypeConverter::class, SessionTypeConverter::class)
abstract class AssistantDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "message.db"

        @Volatile
        private var instance: AssistantDatabase? = null

        fun getInstance(app: Context, logger: Logger?): AssistantDatabase {
            return instance ?: synchronized(this) {
                instance ?: createDatabase(app, logger).also { instance = it }
            }
        }

        @SuppressWarnings("NestedBlockDepth")
        private fun createDatabase(ctx: Context, logger: Logger?): AssistantDatabase {
            listOf(
                DbMigrations.MIGRATION_1_2,
                DbMigrations.MIGRATION_2_3,
                DbMigrations.MIGRATION_3_4,
                DbMigrations.MIGRATION_4_5,
                DbMigrations.MIGRATION_5_6,
                DbMigrations.MIGRATION_6_7,
            ).forEach { migration ->
                if (migration is LoggedMigration) {
                    logger?.let { migration.setLogger(logger) }
                }
            }
            return Room.databaseBuilder(
                ctx.applicationContext,
                AssistantDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .addMigrations(
                    DbMigrations.MIGRATION_1_2,
                    DbMigrations.MIGRATION_2_3,
                    DbMigrations.MIGRATION_3_4,
                    DbMigrations.MIGRATION_4_5,
                    DbMigrations.MIGRATION_5_6,
                    DbMigrations.MIGRATION_6_7,
                )
                .build()
        }
    }

    abstract fun getMessagesDao(): MessagesDao

    abstract fun getSessionDao(): SessionDao

    internal abstract fun getContactsDao(): ContactsDao

    @Deprecated("Used for migration in EL-1647")
    abstract fun getReminderDao(): AssistantReminderDao
}