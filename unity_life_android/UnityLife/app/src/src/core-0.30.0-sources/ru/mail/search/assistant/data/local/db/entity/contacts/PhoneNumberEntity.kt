package ru.mail.search.assistant.data.local.db.entity.contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.contacts.PhoneNumber

@Entity(
    tableName = "phone_numbers",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
internal data class PhoneNumberEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "number")
    val number: String,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "contact_id")
    val contactId: Int,
) : DataEntity<PhoneNumber> {

    override fun toDomain(): PhoneNumber = PhoneNumber(id, number, name)
}