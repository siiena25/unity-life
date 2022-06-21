package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CinemaMoviesDto(
    @SerializedName("movies") val movies: List<CinemaMovieDto>?
)