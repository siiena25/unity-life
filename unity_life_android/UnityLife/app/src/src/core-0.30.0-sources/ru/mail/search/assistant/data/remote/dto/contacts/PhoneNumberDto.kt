package ru.mail.search.assistant.data.remote.dto.contacts

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.entities.contacts.PhoneNumber

internal data class PhoneNumberDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
) {
    companion object {
        fun fromDomain(number: PhoneNumber): PhoneNumberDto {
            return PhoneNumberDto(number.id, number.name.orEmpty())
        }
    }
}