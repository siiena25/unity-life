package ru.mail.search.assistant.data

import androidx.room.withTransaction
import ru.mail.search.assistant.data.local.db.AssistantDatabase
import ru.mail.search.assistant.data.local.db.dao.ContactsDao
import ru.mail.search.assistant.data.local.db.entity.contacts.ContactEntity
import ru.mail.search.assistant.data.local.db.entity.contacts.ContactWithPhonesRelation
import ru.mail.search.assistant.entities.contacts.Contact
import ru.mail.search.assistant.entities.contacts.PhoneNumber

internal class ContactsRepository(
    private val assistantDatabase: AssistantDatabase,
) {
    private val contactsDao: ContactsDao = assistantDatabase.getContactsDao()

    suspend fun getContacts(ids: List<Int>): List<Contact> {
        return contactsDao.getContactsByIds(ids).map(ContactWithPhonesRelation::toDomain)
    }

    suspend fun getContact(id: Int): Contact {
        return contactsDao.getContactById(id).toDomain()
    }

    suspend fun getPhoneNumber(id: Int): PhoneNumber {
        return contactsDao.getPhoneById(id).toDomain()
    }

    suspend fun updateContacts(contacts: List<Contact>) {
        val relations = contacts.map(ContactWithPhonesRelation::fromDomain)
        val contactEntities = relations.map(ContactWithPhonesRelation::contact)
        val phoneNumbers = relations.map(ContactWithPhonesRelation::numbers).flatten()
        assistantDatabase.withTransaction {
            with(contactsDao) {
                deleteOutdatedContacts(currentContactsId = contactEntities.map(ContactEntity::id))
                insertOrUpdateContacts(contactEntities)
                insertOrUpdatePhoneNumbers(phoneNumbers)
            }
        }
    }
}