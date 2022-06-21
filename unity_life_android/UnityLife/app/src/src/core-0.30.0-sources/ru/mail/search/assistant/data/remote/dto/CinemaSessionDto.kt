package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.message.CinemaSession

data class CinemaSessionDto(
    @SerializedName("title") val title: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("distance") val distance: String?,
    @SerializedName("shows") val shows: List<String>?
) : DataEntity<CinemaSession> {
    override fun toDomain(): CinemaSession {
        return CinemaSession(
            title = title,
            address = address,
            url = url,
            distance = distance,
            sessions = shows.orEmpty()
        )
    }
}