package ru.mail.search.assistant.entities

import ru.mail.search.assistant.api.suggests.Suggest
import ru.mail.search.assistant.entities.audio.*
import ru.mail.search.assistant.entities.message.*
import ru.mail.search.assistant.entities.message.apprefs.AppRefsSpan
import ru.mail.search.assistant.entities.message.images.ImageData
import ru.mail.search.assistant.entities.message.welcome.WelcomeScreen

/**
 * Created by Grigory Fedorov on 16.08.18.
 */
sealed class ServerCommand {

    data class SpeakOut(val tts: Tts) : ServerCommand()

    data class ShowText(
        val text: String
    ) : ServerCommand()

    data class ShowFact(
        val title: String?,
        val text: String?,
        val fullText: String?,
        val link: String?,
        val linkTitle: String?,
        val imageUrl: String?,
        val searchUrl: String?,
        val searchTitle: String?,
        val appRefs: List<AppRefsSpan>
    ) : ServerCommand()

    data class ShowSearchCard(
        val text: String,
        val linkLabel: String?,
        val linkUrl: String
    ) : ServerCommand()

    data class MailCountCard(
        val text: String,
        val linkLabel: String?,
        val linkUrl: String?
    ) : ServerCommand()

    data class ShowNewsCard(
        val title: String?,
        val news: List<News>
    ) : ServerCommand()

    data class ShowWeather(
        val temperature: Int?,
        val city: String?,
        val cityFormed: String?,
        val dateFormed: String?,
        val pressure: Int?,
        val windSpeed: Int?,
        val humidity: Int?,
        val iconType: String?,
        val iconUrl: String?,
        val morning: WeatherPeriod?,
        val daytime: WeatherPeriod?,
        val night: WeatherPeriod?,
        val url: String?
    ) : ServerCommand()

    data class ShowSlider(
        val items: List<SliderItem>,
        val extensionItem: SliderItem?
    ) : ServerCommand()

    data class ShowMovies(
        val movies: List<Movie>
    ) : ServerCommand()

    data class ShowMovieTimetable(
        val title: String?,
        val genres: List<String>,
        val posterUrl: String?,
        val url: String?,
        val cinemaSessions: List<CinemaSession>
    ) : ServerCommand()

    data class ShowCinemaTheaterTimetable(
        val title: String?,
        val address: String?,
        val url: String?,
        val movieSessions: List<MovieSession>
    ) : ServerCommand()

    data class ShowImages(
        val imageData: List<ImageData>,
        val morePhotosUrl: String?
    ) : ServerCommand()

    data class PlaylistRadio(
        val playlist: List<Radiostation>,
        val autoPlay: Boolean,
        val trackNumber: Int
    ) : ServerCommand()

    data class PlaylistTale(
        val playlist: List<Tale>,
        val autoPlay: Boolean,
        val trackNumber: Int,
        val trackPosition: Float
    ) : ServerCommand()

    data class PlaylistMusic(
        val playlist: List<AudioTrack>,
        val autoPlay: Boolean,
        val trackNumber: Int,
        val trackPosition: Float
    ) : ServerCommand()

    data class PlaylistPodcast(
        val playlist: List<AudioTrack>,
        val autoPlay: Boolean,
        val trackNumber: Int,
        val trackPosition: Float
    ) : ServerCommand()

    data class PlaylistNoise(
        val playlist: List<Sound>,
        val autoPlay: Boolean,
        val trackNumber: Int,
        val trackPosition: Float
    ) : ServerCommand()

    data class AuthorizeCard(
        val authType: String,
        val text: String,
        val repeatText: String
    ) : ServerCommand()

    data class EventListCard(val skills: List<EventItemDescription>) : ServerCommand()

    data class CompanyCard(
        val title: String,
        val address: String,
        val phones: List<String>,
        val imageUrl: String?,
        val schedule: String?,
        val metro: String?,
        val mapUrl: String?,
        val routeUrl: String?,
        val siteUrl: String?,
        val category: String?,
        val description: String?,
        val distance: String?,
        val rating: String?
    ) : ServerCommand()

    data class SerpCard(
        val items: List<SerpItem>,
        val searchUrl: String
    ) : ServerCommand()

    data class Recipes(
        val items: List<Recipe>,
        val searchUrl: String?
    ) : ServerCommand()

    data class FlowMode(val name: String) : ServerCommand()

    sealed class Controls : ServerCommand() {

        abstract val type: ControlType

        data class PlaybackResume(override val type: ControlType) : Controls()
        data class PlaybackPause(override val type: ControlType) : Controls()
        data class PlaybackVolume(override val type: ControlType, val volume: Int) : Controls()
        data class PlaybackNext(override val type: ControlType) : Controls()
        data class PlaybackPrev(override val type: ControlType) : Controls()
        data class PlaybackForward(override val type: ControlType) : Controls()
        data class PlaybackBackward(override val type: ControlType) : Controls()
        data class PlaybackStop(override val type: ControlType) : Controls()
        data class PlaybackRewind(
            override val type: ControlType,
            val playlistPosition: Int,
            val elapsed: Float
        ) : Controls()

        data class PlaybackRepeat(
            override val type: ControlType,
            val repeatType: Int,
            val count: Int
        ) : Controls()
    }

    data class Suggests(val list: List<Suggest>) : ServerCommand()

    data class PlayDialogSound(
        val text: String,
        val url: String,
        val kwsSkipIntervals: List<KwsSkipInterval>?
    ) : ServerCommand()

    data class ListenCommand(
        val minWaitingTime: Int?,
        val muteActivationSound: Boolean,
        val callbackData: String?
    ) : ServerCommand()

    data class Delay(val timestamp: Long) : ServerCommand()

    data class ShowUrl(val url: String) : ServerCommand()

    data class WelcomeScreenCommand(
        val welcomeScreen: WelcomeScreen
    ) : ServerCommand()

    data class PermissionRequestCard(val text: String) : ServerCommand()

    data class PermissionRequestCommand(val permission: String, val requestCode: Int) : ServerCommand()

    data class ContactsSync(val event: String) : ServerCommand()

    data class ContactsCard(
        val hasMore: Boolean,
        val page: Int,
        val contacts: List<Int>,
        val callbackEvent: String,
    ) : ServerCommand()

    data class ContactNumbersCard(
        val contactId: Int,
        val callbackEvent: String,
    ) : ServerCommand()

    data class FindPhoneByPart(
        val contactId: Int,
        val phonePart: String,
        val callbackEvent: String,
    ) : ServerCommand()

    data class PhoneCall(val numberId: Int) : ServerCommand()

    class PhoneCallCard(
        val contactId: Int,
        val numberId: Int
    ) : ServerCommand()

    data class EmergencyCall(val phoneNumber: String) : ServerCommand()

    data class EmergencyCallCard(val phoneNumber: String) : ServerCommand()

    data class EditSms(val text: String) : ServerCommand()

    data class SendSms(
        val numberId: Int,
        val text: String,
    ) : ServerCommand()

    object UnknownCommand : ServerCommand()

    abstract class ExternalUserDefined : ServerCommand()
}
