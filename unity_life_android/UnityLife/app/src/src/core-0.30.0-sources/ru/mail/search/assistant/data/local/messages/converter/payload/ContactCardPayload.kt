package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class ContactCardPayload(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("photo_uri") val photoUri: String?,
    @SerializedName("phone_number") val phoneNumber: String,
)