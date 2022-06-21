package ru.mail.search.assistant.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.res.Resources
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Patterns
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.common.util.EMPTY
import ru.mail.search.assistant.common.util.runIf
import ru.mail.search.assistant.entities.contacts.Contact
import ru.mail.search.assistant.entities.contacts.PhoneNumber

internal class ContactsManager(
    private val resources: Resources,
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher,
) {

    companion object {
        private const val PHONE_CLEAN_PATTERN = "[^+0-9]"
        private const val NAME_SPLIT_PATTERN = "\\s+"
        private const val PHONE_CONTACT_ID_SELECTION = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"

        private val CONTACT_PROJECTION = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.PHOTO_URI,
        )

        private val PHONE_PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.LABEL,
        )
    }

    @SuppressLint("Recycle")
    suspend fun getContacts(): List<Contact> = withContext(dispatcher) {
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            CONTACT_PROJECTION,
            null,
            null,
            null
        )
            .let(::requireNotNull)
            .use { it.readContacts() }
            .filter { it.firstName.isNotEmpty() && it.numbers.isNotEmpty() }
    }

    private fun Cursor.readContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        while (moveToNext()) {
            contacts.add(readContact())
        }
        return contacts
    }

    private fun Cursor.readContact(): Contact {
        val id = contactId()
        val numbers = runIf(hasPhoneNumber()) { readContactNumbers(id) }
            .orEmpty()

        val displayName = contactDisplayName()
            // If contact doesn't have a name value, the parameter will return a value of the first phone number
            .takeUnless { it.isPhoneNumber() }
            .orEmpty()
            .split(NAME_SPLIT_PATTERN.toRegex())
        val firstName = displayName.firstOrNull().orEmpty()
        val lastName = displayName.drop(n = 1).joinToString(separator = " ")

        return Contact(
            id = id,
            firstName = firstName,
            lastName = lastName,
            numbers = numbers,
            photoUri = contactPhotoUri(),
        )
    }

    @SuppressLint("Recycle")
    private fun readContactNumbers(id: Int): List<PhoneNumber> {
        return contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PHONE_PROJECTION,
            PHONE_CONTACT_ID_SELECTION,
            arrayOf(id.toString()),
            null
        )
            .let(::requireNotNull)
            .use { it.readContactNumbers() }
    }

    private fun Cursor.readContactNumbers(): List<PhoneNumber> {
        val numbers = mutableListOf<PhoneNumber>()
        while (moveToNext()) {
            numbers.add(readContactNumber())
        }
        return numbers
    }

    private fun Cursor.readContactNumber(): PhoneNumber {
        val name = ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, phoneType(), phoneLabel()).toString()
        // Format of the contact's phone number depends on Android firmware of the device and can contain any separators
        val number = phoneNumber().replace(PHONE_CLEAN_PATTERN.toRegex(), String.EMPTY)
        return PhoneNumber(
            id = phoneId(),
            name = name,
            number = number,
        )
    }

    private fun String.isPhoneNumber(): Boolean = Patterns.PHONE.matcher(this).matches()

    private fun Cursor.hasPhoneNumber(): Boolean = getColumnInt(ContactsContract.Contacts.HAS_PHONE_NUMBER) > 0

    private fun Cursor.contactId() = getColumnInt(ContactsContract.Contacts._ID)

    private fun Cursor.contactDisplayName() = requireColumnString(ContactsContract.Contacts.DISPLAY_NAME)

    private fun Cursor.contactPhotoUri() = getColumnString(ContactsContract.Contacts.PHOTO_URI)

    private fun Cursor.phoneId() = getColumnInt(ContactsContract.CommonDataKinds.Phone._ID)

    private fun Cursor.phoneNumber() = requireColumnString(ContactsContract.CommonDataKinds.Phone.NUMBER)

    private fun Cursor.phoneType() = getColumnInt(ContactsContract.CommonDataKinds.Phone.TYPE)

    private fun Cursor.phoneLabel() = getColumnString(ContactsContract.CommonDataKinds.Phone.LABEL)

    private fun Cursor.requireColumnString(column: String): String = requireNotNull(getColumnString(column))

    private fun Cursor.getColumnString(column: String): String? = getString(getColumnIndexOrThrow(column))

    private fun Cursor.getColumnInt(column: String): Int = getInt(getColumnIndexOrThrow(column))
}