package ru.mail.search.assistant.data.local.db

import android.content.ContentValues
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.common.util.getArray
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.parseAsObject
import ru.mail.search.assistant.data.exception.MigrationException
import ru.mail.search.assistant.util.Tag

@Suppress("MagicNumber")
object DbMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DELETE FROM messages WHERE `type` = 'el_invite_card'")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val jsonParser = JsonParser()
            val cursor = database.query("SELECT * FROM messages WHERE `type` = 'el_photo_album'")
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getLong(cursor.getColumnIndex("id"))
                    val payload = it.getString(cursor.getColumnIndex("payload"))
                    val payloadJson = jsonParser.parse(payload)
                    val moreUrl: String? = null
                    val newPayload = JsonObject().apply {
                        add("photo_urls", payloadJson)
                        addProperty("more_url", moreUrl)
                    }
                    val contentValues = ContentValues().apply {
                        put("payload", newPayload.toString())
                    }
                    database.update(
                        "messages",
                        0,
                        contentValues,
                        "id=?",
                        arrayOf(id.toString())
                    )
                }
            }
            database.execSQL("DELETE FROM messages WHERE `type` = 'el_single_photo'")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (`id` TEXT NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `request_code` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }

    val MIGRATION_4_5 = object : LoggedMigration(4, 5) {

        @SuppressWarnings("StringLiteralDuplication")
        override fun migrate(database: SupportSQLiteDatabase) {
            val jsonParser = JsonParser()
            val updateMessages = ArrayList<Pair<Long, String>>()
            database.query("SELECT * FROM messages WHERE `type` = 'el_photo_album'").use { cursor ->
                val idColumn = cursor.getColumnIndex("id")
                val payloadColumn = cursor.getColumnIndex("payload")
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val payloadJson = cursor.getString(payloadColumn)
                    val payloadObject = jsonParser.parseAsObject(payloadJson)
                    if (payloadObject == null) {
                        logger?.e(
                            Tag.MIGRATION,
                            MigrationException(
                                "Db migration from $startVersion to $endVersion:" +
                                        " wrong payload, message ignored"
                            )
                        )
                        continue
                    }
                    val imageData = JsonArray()
                    val photoUrlsElement = payloadObject.getArray("photo_urls")
                    if (photoUrlsElement == null) {
                        logger?.e(
                            Tag.MIGRATION,
                            MigrationException(
                                "Db migration from $startVersion to $endVersion:" +
                                        " wrong photo url array, message ignored"
                            )
                        )
                        continue
                    }
                    for (urlElement in photoUrlsElement) {
                        val url = urlElement.getString()
                        if (url == null) {
                            logger?.e(
                                Tag.MIGRATION,
                                MigrationException(
                                    "Db migration from $startVersion to $endVersion:" +
                                            " wrong photo url, image ignored"
                                )
                            )
                            continue
                        }
                        val imageDataObject = JsonObject().apply {
                            val thumbnail = JsonObject().apply {
                                addProperty("url", url)
                            }
                            add("thumbnail", thumbnail)
                        }
                        imageData.add(imageDataObject)
                    }
                    if (imageData.size() == 0) {
                        logger?.e(
                            Tag.MIGRATION,
                            MigrationException(
                                "Db migration from $startVersion to $endVersion:" +
                                        " empty image data, message ignored"
                            )
                        )
                    }
                    payloadObject.remove("photo_urls")
                    payloadObject.add("image_data", imageData)
                    updateMessages.add(Pair(id, payloadObject.toString()))
                }
            }
            updateMessages.forEach { (id, payload) ->
                val contentValues = ContentValues().apply {
                    put("payload", payload)
                }
                database.update(
                    "messages",
                    0,
                    contentValues,
                    "id=?",
                    arrayOf(id.toString())
                )
            }
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE  INDEX `index_messages_creation_time` ON `messages` (`creation_time`)")
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {

        override fun migrate(database: SupportSQLiteDatabase) = with(database) {
            execSQL(
                "CREATE TABLE IF NOT EXISTS `contacts` (" +
                        "`id` INTEGER NOT NULL," +
                        "`first_name` TEXT NOT NULL," +
                        "`last_name` TEXT NOT NULL," +
                        "`photo_uri` TEXT," +
                        "PRIMARY KEY(`id`)" +
                        ")"
            )
            execSQL(
                "CREATE TABLE IF NOT EXISTS `phone_numbers` (" +
                        "`id` INTEGER NOT NULL," +
                        "`number` TEXT NOT NULL," +
                        "`name` TEXT," +
                        "`contact_id` INTEGER NOT NULL," +
                        "PRIMARY KEY(`id`)," +
                        "FOREIGN KEY(`contact_id`)" +
                        "REFERENCES `contacts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                        ")"
            )
        }
    }
}