package ru.mail.search.assistant.data.remote.dto.contacts

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.entities.contacts.Contact

internal data class ContactDto(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phones") val numbers: List<PhoneNumberDto>
) {
    companion object {
        fun fromDomain(contact: Contact): ContactDto {
            return ContactDto(
                id = contact.id,
                firstName = contact.firstName,
                lastName = contact.lastName,
                numbers = contact.numbers.map(PhoneNumberDto::fromDomain)
            )
        }
    }
}