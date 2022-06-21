package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes.MOVIE_TIMETABLE
import ru.mail.search.assistant.data.local.messages.converter.payload.CinemaSessionPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.MovieTimetableMsgPayload
import ru.mail.search.assistant.entities.message.CinemaSession
import ru.mail.search.assistant.entities.message.MessageData

internal class MovieTimetableMsgPayloadConverter :
    PayloadGsonConverter<MessageData.MovieTimetable, MovieTimetableMsgPayload>() {

    override val type: String get() = MOVIE_TIMETABLE

    override fun payloadToPojo(payload: String): MessageData.MovieTimetable {
        return fromJson<MovieTimetableMsgPayload>(payload) {
            MessageData.MovieTimetable(
                title = title,
                genres = genres,
                url = url,
                cinemaSessions = cinemaSessions.map { sessionItem ->
                    CinemaSession(
                        title = sessionItem.title,
                        address = sessionItem.address,
                        url = sessionItem.url,
                        distance = sessionItem.distance,
                        sessions = sessionItem.sessions
                    )
                }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.MovieTimetable): String {
        return toJson(data) {
            MovieTimetableMsgPayload(
                title = title,
                genres = genres,
                url = url,
                cinemaSessions = cinemaSessions.map { sessionItem ->
                    CinemaSessionPayload(
                        title = sessionItem.title,
                        address = sessionItem.address,
                        url = sessionItem.url,
                        distance = sessionItem.distance,
                        sessions = sessionItem.sessions
                    )
                }
            )
        }
    }
}