package ru.mail.search.assistant.data.local.db.entity.contacts

import androidx.room.Embedded
import androidx.room.Relation
import ru.mail.search.assistant.data.DataConverter
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.contacts.Contact

internal data class ContactWithPhonesRelation(
    @Embedded val contact: ContactEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "contact_id",
    )
    val numbers: List<PhoneNumberEntity>,
) : DataEntity<Contact> {

    companion object : DataConverter<Contact, ContactWithPhonesRelation>() {

        override fun Contact.convert(): ContactWithPhonesRelation {
            val numbers = numbers.map { PhoneNumberEntity(it.id, it.number, it.name, id) }
            return ContactWithPhonesRelation(
                contact = ContactEntity.fromDomain(model = this),
                numbers = numbers
            )
        }
    }

    override fun toDomain(): Contact {
        return Contact(
            id = contact.id,
            firstName = contact.firstName,
            lastName = contact.lastName,
            photoUri = contact.photoUri,
            numbers = numbers.map(PhoneNumberEntity::toDomain)
        )
    }
}
