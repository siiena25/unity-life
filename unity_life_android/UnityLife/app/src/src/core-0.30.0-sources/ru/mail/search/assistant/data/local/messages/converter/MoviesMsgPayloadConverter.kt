package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes.MOVIES
import ru.mail.search.assistant.data.local.messages.converter.payload.MoviePayload
import ru.mail.search.assistant.data.local.messages.converter.payload.MoviesMsgPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.Movie

internal class MoviesMsgPayloadConverter : PayloadGsonConverter<MessageData.Movies, MoviesMsgPayload>() {

    override val type: String get() = MOVIES

    override fun payloadToPojo(payload: String): MessageData.Movies {
        return fromJson<MoviesMsgPayload>(payload) {
            MessageData.Movies(
                movies.map { moviesItem ->
                    Movie(
                        moviesItem.title,
                        moviesItem.genres,
                        moviesItem.premierDate,
                        moviesItem.posterUrl,
                        moviesItem.url
                    )
                }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.Movies): String {
        return toJson(data) {
            MoviesMsgPayload(
                movies.map { moviesItem ->
                    MoviePayload(
                        moviesItem.title,
                        moviesItem.genres,
                        moviesItem.premierDate,
                        moviesItem.posterUrl,
                        moviesItem.url
                    )
                }
            )
        }
    }
}