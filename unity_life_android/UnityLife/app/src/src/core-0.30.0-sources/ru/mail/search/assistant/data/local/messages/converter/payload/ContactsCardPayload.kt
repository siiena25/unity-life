package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class ContactsCardPayload(
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("page") val page: Int,
    @SerializedName("callback_event") val callbackEvent: String,
    @SerializedName("contacts") val contacts: List<Contact>,
) {
    data class Contact(
        @SerializedName("id") val id: Int,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        @SerializedName("photo_uri") val photoUri: String?,
        @SerializedName("phone_number") val phoneNumber: String,
    )
}