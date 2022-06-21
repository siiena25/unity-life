package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes.CINEMA_THEATER_TIMETABLE
import ru.mail.search.assistant.data.local.messages.converter.payload.CinemaTheaterTimetableMsgPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.MovieSessionPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.MovieSession
import ru.mail.search.assistant.entities.message.MovieShowType

internal class CinemaTheaterTimetableMsgPayloadConverter :
    PayloadGsonConverter<MessageData.CinemaTimetable, CinemaTheaterTimetableMsgPayload>() {

    companion object {

        private const val SHOW_TYPE_UNDEFINED = 0
        private const val SHOW_TYPE_2D = 1
        private const val SHOW_TYPE_3D = 2
    }

    override val type: String get() = CINEMA_THEATER_TIMETABLE

    override fun payloadToPojo(payload: String): MessageData.CinemaTimetable {
        return fromJson<CinemaTheaterTimetableMsgPayload>(payload) {
            MessageData.CinemaTimetable(
                title = title,
                address = address,
                url = url,
                movieSessions = movieSessions.map { sessionItem ->
                    MovieSession(
                        title = sessionItem.title,
                        genres = sessionItem.genres,
                        posterUrl = sessionItem.posterUrl,
                        url = sessionItem.url,
                        showType = when (sessionItem.showType) {
                            SHOW_TYPE_2D -> MovieShowType.FORMAT_2D
                            SHOW_TYPE_3D -> MovieShowType.FORMAT_3D
                            else -> MovieShowType.UNDEFINED
                        },
                        sessions = sessionItem.sessions
                    )
                }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.CinemaTimetable): String {
        return toJson(data) {
            CinemaTheaterTimetableMsgPayload(
                title = title,
                address = address,
                url = url,
                movieSessions = movieSessions.map { sessionItem ->
                    MovieSessionPayload(
                        title = sessionItem.title,
                        genres = sessionItem.genres,
                        posterUrl = sessionItem.posterUrl,
                        url = sessionItem.url,
                        showType = when (sessionItem.showType) {
                            MovieShowType.FORMAT_2D -> SHOW_TYPE_2D
                            MovieShowType.FORMAT_3D -> SHOW_TYPE_3D
                            MovieShowType.UNDEFINED -> SHOW_TYPE_UNDEFINED
                        },
                        sessions = sessionItem.sessions
                    )
                }
            )
        }
    }
}