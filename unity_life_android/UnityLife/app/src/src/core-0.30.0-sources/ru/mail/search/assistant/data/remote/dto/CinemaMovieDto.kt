package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.message.Movie

data class CinemaMovieDto(
    @SerializedName("title") val title: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("poster") val poster: String?,
    @SerializedName("url") val url: String?
) : DataEntity<Movie> {
    override fun toDomain(): Movie {
        return Movie(
            title = title,
            genres = genres.orEmpty(),
            premierDate = null,
            posterUrl = poster,
            url = url
        )
    }
}