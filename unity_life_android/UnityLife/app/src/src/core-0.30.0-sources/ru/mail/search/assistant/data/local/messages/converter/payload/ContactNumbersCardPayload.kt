package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class ContactNumbersCardPayload(
    @SerializedName("callback_event") val callbackEvent: String,
    @SerializedName("contact_id") val contactId: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("avatar_uri") val avatarUri: String?,
    @SerializedName("numbers") val numbers: List<PhoneNumber>,
) {
    data class PhoneNumber(
        @SerializedName("id") val id: Int,
        @SerializedName("number") val number: String,
        @SerializedName("name") val name: String,
    )
}