package ru.mail.search.assistant.data.local.db.entity.contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mail.search.assistant.data.DataConverter
import ru.mail.search.assistant.entities.contacts.Contact

@Entity(tableName = "contacts")
internal data class ContactEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @ColumnInfo(name = "photo_uri")
    val photoUri: String?,
) {

    companion object : DataConverter<Contact, ContactEntity>() {

        override fun Contact.convert(): ContactEntity = ContactEntity(id, firstName, lastName, photoUri)
    }
}