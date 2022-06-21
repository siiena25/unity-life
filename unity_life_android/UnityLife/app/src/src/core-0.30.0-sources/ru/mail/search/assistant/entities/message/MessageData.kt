package ru.mail.search.assistant.entities.message

import ru.mail.search.assistant.auth.common.domain.model.AuthContext
import ru.mail.search.assistant.entities.audio.*
import ru.mail.search.assistant.entities.message.apprefs.AppRefsSpan
import ru.mail.search.assistant.entities.message.images.ImageData
import ru.mail.search.assistant.entities.message.mailru.Letter
import ru.mail.search.assistant.entities.message.mailru.Mail
import ru.mail.search.assistant.entities.message.mailru.MailBox
import ru.mail.search.assistant.entities.message.welcome.WelcomeScreen
import ru.mail.search.assistant.entities.message.welcome.WelcomeShortcut
import java.util.*

sealed class MessageData {

    // DO NOT PUT IN DATABASE
    @Deprecated("Deprecated api")
    data class OutgoingLoading(
        val text: String,
        /**
         *  process that created it
         */
        val uuid: UUID
    ) : MessageData()

    data class OutgoingData(
        val text: String,
        val uuid: UUID? = null
    ) : MessageData()

    data class IncomingText(
        val text: String
    ) : MessageData()

    data class Fact(
        val title: String?,
        val text: String?,
        val fullText: String?,
        val link: String?,
        val linkTitle: String?,
        val imageUrl: String?,
        val searchUrl: String?,
        val searchTitle: String?,
        val appRefs: List<AppRefsSpan>
    ) : MessageData()

    data class ActionCard(
        val text: String,
        val tag: String,
        val linkLabel: String?,
        val linkUrl: String
    ) : MessageData()

    data class NewsCard(
        val title: String?,
        val news: List<News>
    ) : MessageData()

    data class Weather(
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
    ) : MessageData()

    data class Slider(
        val items: List<SliderItem>,
        val extensionItem: SliderItem?
    ) : MessageData()

    data class Movies(
        val movies: List<Movie>
    ) : MessageData()

    data class CinemaTimetable(
        val title: String?,
        val address: String?,
        val url: String?,
        val movieSessions: List<MovieSession>
    ) : MessageData()

    data class MovieTimetable(
        val title: String?,
        val genres: List<String>,
        val url: String?,
        val cinemaSessions: List<CinemaSession>
    ) : MessageData()

    data class PhotoAlbum(
        val imageData: List<ImageData>,
        val morePhotosUrl: String?
    ) : MessageData()

    data class AuthorizeCard(
        val text: String,
        val authContext: AuthContext
    ) : MessageData()

    data class EventListCard(
        val items: List<EventItemDescription>
    ) : MessageData()

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
    ) : MessageData()

    data class SerpCard(
        val items: List<SerpItem>,
        val searchUrl: String
    ) : MessageData()

    data class RecipesCard(
        val recipes: List<Recipe>,
        val searchUrl: String?
    ) : MessageData()

    data class MailRuSingleLetter(val letter: Letter) : MessageData()

    data class MailRuBoxStatus(val mailBox: MailBox) : MessageData()

    data class MailRuSenderMails(val mails: List<Mail>) : MessageData()

    data class MailRuAuthRequest(
        val retryEvent: String,
        val callbackData: String?
    ) : MessageData()

    data class WelcomeScreenMessage(val welcomeScreen: WelcomeScreen) : MessageData()

    object WidgetsScreenMessage : MessageData()

    data class MiniApp(
        val title: String?,
        val text: String?,
        val imageUrl: String?,
        val url: String
    ) : MessageData()

    data class ShortcutSuggest(
        val list: List<WelcomeShortcut>,
        val maxHeight: Int
    ) : MessageData()

    data class PermissionRequest(val message: String) : MessageData()

    data class ContactsCard(
        val hasMore: Boolean,
        val page: Int,
        val callbackEvent: String,
        val contacts: List<Contact>,
    ) : MessageData() {

        data class Contact(
            val id: Int,
            val firstName: String,
            val lastName: String,
            val photoUri: String?,
            val phoneNumber: String,
        )
    }

    data class ContactNumbersCard(
        val callbackEvent: String,
        val contactId: Int,
        val firstName: String,
        val lastName: String,
        val avatarUri: String?,
        val numbers: List<PhoneNumber>,
    ) : MessageData() {

        data class PhoneNumber(
            val id: Int,
            val number: String,
            val name: String,
        )
    }

    data class ContactCard(
        val id: Int,
        val firstName: String,
        val lastName: String,
        val photoUri: String?,
        val phoneNumber: String,
    ) : MessageData()

    data class EmergencyCallCard(val phoneNumber: String) : MessageData()

    sealed class Player : MessageData() {

        data class RadioMsg(val playlist: List<Radiostation>) : Player()

        data class TracksMsg(val playlist: List<AudioTrack>) : Player()

        data class TaleMsg(val playlist: List<Tale>) : Player()

        data class SoundMsg(val playlist: List<Sound>) : Player()

        @Deprecated("Backward compatibility")
        data class PodcastMsg(val podcast: Podcast) : Player()

        data class PodcastsMsg(val playlist: List<AudioTrack>) : Player()
    }

    data class Wink(
        val title: String,
        val subtitle: String,
        val buttonText: String,
        val link: String? = null
    ) : MessageData()
}