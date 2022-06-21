package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.message.MovieSession
import ru.mail.search.assistant.entities.message.MovieShowType

data class MovieSessionDto(
    @SerializedName("title") val title: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("poster") val posterUrl: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("show_type") val showType: String?,
    @SerializedName("shows") val shows: List<String>?
) : DataEntity<MovieSession> {

    companion object {
        private const val MOVIE_SHOW_TYPE_2D = "2D"
        private const val MOVIE_SHOW_TYPE_3D = "3D"
    }

    override fun toDomain(): MovieSession {
        return MovieSession(
            title = title,
            genres = genres.orEmpty(),
            posterUrl = posterUrl,
            url = url,
            showType = when {
                showType.equals(MOVIE_SHOW_TYPE_2D, ignoreCase = true) -> MovieShowType.FORMAT_2D
                showType.equals(MOVIE_SHOW_TYPE_3D, ignoreCase = true) -> MovieShowType.FORMAT_3D
                else -> MovieShowType.UNDEFINED
            },
            sessions = shows.orEmpty()
        )
    }
}