package ru.mail.search.assistant.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.mail.search.assistant.data.local.db.entity.contacts.ContactEntity
import ru.mail.search.assistant.data.local.db.entity.contacts.ContactWithPhonesRelation
import ru.mail.search.assistant.data.local.db.entity.contacts.PhoneNumberEntity

@Dao
internal interface ContactsDao {

    @Query("SELECT * FROM contacts WHERE id IN (:ids)")
    suspend fun getContactsByIds(ids: List<Int>): List<ContactWithPhonesRelation>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Int): ContactWithPhonesRelation

    @Query("SELECT * FROM phone_numbers WHERE id = :id")
    suspend fun getPhoneById(id: Int): PhoneNumberEntity

    @Query("DELETE FROM contacts WHERE id NOT IN (:currentContactsId)")
    suspend fun deleteOutdatedContacts(currentContactsId: List<Int>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateContacts(contacts: List<ContactEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePhoneNumbers(phoneNumbers: List<PhoneNumberEntity>)
}
