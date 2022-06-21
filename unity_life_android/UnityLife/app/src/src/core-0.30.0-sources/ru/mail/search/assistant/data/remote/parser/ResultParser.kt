package ru.mail.search.assistant.data.remote.parser

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.mail.search.assistant.api.suggests.SkillIcons
import ru.mail.search.assistant.api.suggests.Suggest
import ru.mail.search.assistant.api.suggests.SuggestsParser
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.data.exception.parsingError
import ru.mail.search.assistant.common.util.*
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.remote.dto.*
import ru.mail.search.assistant.entities.ControlType
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.ServerQueuedCommand
import ru.mail.search.assistant.entities.audio.AudioTrack
import ru.mail.search.assistant.entities.audio.Tts
import ru.mail.search.assistant.entities.message.*
import ru.mail.search.assistant.entities.message.apprefs.AppsRefParser
import ru.mail.search.assistant.entities.message.images.Image
import ru.mail.search.assistant.entities.message.images.ImageData
import ru.mail.search.assistant.entities.message.images.Thumbnail
import ru.mail.search.assistant.util.analytics.event.AssistantError
import ru.mail.search.assistant.util.analytics.logAssistantError
import java.net.URL

internal class ResultParser(
    private val suggestsParser: SuggestsParser,
    private val kwsSkipIntervalsParser: KwsSkipIntervalsParser,
    private val playlistParser: PlaylistParser,
    private val audioTrackParser: AudioTrackParser,
    private val radioParser: RadioParser,
    private val noiseParser: NoiseParser,
    private val taleParser: TaleParser,
    private val externalParsersProvider: ExternalParsersProvider,
    private val appsRefParser: AppsRefParser,
    private val analytics: Analytics?,
    logger: Logger?,
) {

    private companion object {

        private const val TYPE_TTS = "tts"
        private const val TYPE_TEXT = "text"
        private const val TYPE_LISTEN = "listen"
        private const val TYPE_URL = "url"
        private const val TYPE_SOUND = "sound"
        private const val TYPE_MEDIA = "media"
        private const val TYPE_FACT = "fact"
        private const val TYPE_WEATHER = "weather"
        private const val TYPE_SEARCH_CARD = "search_card"
        private const val TYPE_VK_MUSIC_LINK = "vk_music_link"
        private const val TYPE_NEWS_CARD = "news_card"
        private const val TYPE_DELAY = "delay"
        private const val TYPE_SLIDERS = "sliders"
        private const val TYPE_CINEMA_MOVIES = "cinema_movies"
        private const val TYPE_CINEMA_MOVIE_SCHEDULE = "cinema_movie_schedule"
        private const val TYPE_CINEMA_THEATER_SCHEDULE = "cinema_theater_schedule"
        private const val TYPE_IMAGES = "images"
        private const val TYPE_EVENT_LIST = "event_list"
        private const val TYPE_MAILCOUNT_CARD = "mail_count"
        private const val TYPE_COMPANY_CARD = "poi"
        private const val TYPE_SERP = "search_result"
        private const val TYPE_RECIPES = "recipes"
        private const val TYPE_WELCOME_SCREEN = "welcome_screen"
        private const val TYPE_AUTHORIZE_CARD = "authorize"
        private const val TYPE_FLOW_MODE = "enter_flowmode"
        private const val TYPE_PLAYLIST = "playlist"
    }

    private val gson = Gson()
    private val welcomeScreenParser = WelcomeScreenParser(gson, suggestsParser, logger)
    private val controlParser = ControlParser()
    private val contactsParser = ContactsParser()

    fun parseCommands(phraseResult: JsonObject): List<ServerQueuedCommand> {
        val commands = ArrayList<ServerQueuedCommand>()
        parseCommands(phraseResult, commands)
        parseControls(phraseResult, commands)
        return commands
    }

    private fun parseCommands(json: JsonObject, commands: MutableList<ServerQueuedCommand>) {
        val commandsJson = json.getArray("commands") ?: return

        val playlist = lazy { mutableListOf<AudioTrack>() }
        val podcasts = lazy { mutableListOf<AudioTrack>() }
        val suggests = ArrayList<Suggest>()
        var suggestsIdx = 0

        commandsJson.forEachCommandSafety { type, commandJson ->
            val command = externalParsersProvider.externalParsers[type]
                ?.parse(commandJson)
                ?: when (type) {
                    TYPE_TTS -> commandJson.mapToSpeakOut()
                    TYPE_TEXT -> ServerCommand.ShowText(commandJson.getString("text").orEmpty())
                    TYPE_LISTEN -> commandJson.mapToListenCommand()
                    TYPE_URL -> commandJson.getString("url")?.let(ServerCommand::ShowUrl)
                    TYPE_SOUND -> commandJson.mapToDialogSound()
                    TYPE_FACT -> commandJson.mapToFact()
                    TYPE_WEATHER -> commandJson.mapToWeather()
                    TYPE_MAILCOUNT_CARD -> commandJson.mapToMailCardCount()
                    TYPE_NEWS_CARD -> commandJson.mapToNews()
                    TYPE_SLIDERS -> commandJson.mapToSlider()
                    TYPE_CINEMA_MOVIES -> commandJson.mapToCinemaMovies()
                    TYPE_CINEMA_MOVIE_SCHEDULE -> commandJson.mapToMovieTimetable()
                    TYPE_CINEMA_THEATER_SCHEDULE -> commandJson.mapToCinemaTheaterTimetable()
                    TYPE_IMAGES -> commandJson.mapToImages()
                    TYPE_MEDIA -> commandJson.mapToMedia(playlist, podcasts)
                    TYPE_DELAY -> commandJson.mapToDelay()
                    TYPE_AUTHORIZE_CARD -> commandJson.mapToAuthorizeCard()
                    TYPE_EVENT_LIST -> commandJson.mapToEventList()
                    TYPE_COMPANY_CARD -> commandJson.mapToCompanyCard()
                    TYPE_SERP -> commandJson.mapToSerpCard()
                    TYPE_RECIPES -> commandJson.mapToRecipes()
                    TYPE_WELCOME_SCREEN -> welcomeScreenParser.parse(commandJson)
                    TYPE_FLOW_MODE -> commandJson.mapToFlowMod()
                    TYPE_PLAYLIST -> playlistParser.parse(commandJson)
                    TYPE_SEARCH_CARD, TYPE_VK_MUSIC_LINK -> commandJson.mapToSearchCard()

                    // Contacts
                    ContactsParser.ADDRESS_BOOK_PERMISSION_CARD,
                    ContactsParser.ADDRESS_BOOK_PERMISSION_REQUEST,
                    ContactsParser.ADDRESS_BOOK_REQUEST,
                    ContactsParser.MATCHED_CONTACT_CARD,
                    ContactsParser.MATCHED_CONTACT_PHONES_CARD,
                    ContactsParser.CHECKING_PHONE_BY_DIGITS,
                    ContactsParser.PHONE_CALL,
                    ContactsParser.PHONE_CARD,
                    ContactsParser.EMERGENCY_CALL,
                    ContactsParser.EMERGENCY_CARD,
                    ContactsParser.EDIT_MESSAGE,
                    ContactsParser.SEND_SMS -> contactsParser.parse(commandJson)

                    // Controls commands
                    ControlParser.CONTROL_TYPE_RESUME,
                    ControlParser.CONTROL_TYPE_PAUSE,
                    ControlParser.CONTROL_TYPE_VOLUME,
                    ControlParser.CONTROL_TYPE_NEXT,
                    ControlParser.CONTROL_TYPE_PREV,
                    ControlParser.CONTROL_TYPE_FORWARD,
                    ControlParser.CONTROL_TYPE_BACKWARD,
                    ControlParser.CONTROL_TYPE_STOP,
                    ControlParser.CONTROL_TYPE_REPEAT,
                    ControlParser.CONTROL_TYPE_REPEAT_PLAYLIST,
                    ControlParser.CONTROL_TYPE_REWIND -> controlParser.parse(commandJson, ControlType.COMMAND)

                    // Suggests
                    SuggestsParser.TYPE_TEXT,
                    SuggestsParser.TYPE_EVENT,
                    SuggestsParser.TYPE_URL -> {
                        suggestsIdx = commands.size
                        suggestsParser.parseSuggest(commandJson)?.let(suggests::add)
                        null
                    }

                    else -> ServerCommand.UnknownCommand
                }

            command?.let { commands.addSyncCommand(it) }
        }

        /**
         *  emulation of command show url - add to end of queue
         *  delete when back end will start sending it
         */
        val openUrlCommand = commands.firstOrNull { it.command is ServerCommand.ShowUrl }
        commands
            .takeIf { openUrlCommand == null }
            ?.firstOrNull { it.command is ServerCommand.ShowSearchCard }
            ?.command
            ?.let { (it as ServerCommand.ShowSearchCard).linkUrl }
            ?.let { commands.addSyncCommand(ServerCommand.ShowUrl(it)) }

        if (suggests.isNotEmpty()) {
            commands.addSyncCommand(suggestsIdx, ServerCommand.Suggests(suggests))
        }
    }

    private fun parseControls(json: JsonObject, commands: MutableList<ServerQueuedCommand>) {
        json.getArray("controls")?.forEachCommandSafety { type, commandJson ->
            val command = externalParsersProvider.externalParsers[type]
                ?.parse(commandJson)
                ?: controlParser.parse(commandJson, ControlType.MEDIA)
                ?: ServerCommand.UnknownCommand
            commands.addMediaCommand(command)
        }
    }

    private fun JsonArray.forEachCommandSafety(
        block: (type: String, commandJson: JsonObject) -> Unit
    ) {
        for (i in 0 until size()) {
            runCatching {
                val commandJson = get(i).toObject()
                    ?: parsingError("Command json is not object type")
                val type = commandJson.getString("type")
                    ?: parsingError("Command json without type")
                block(type, commandJson)
            }.onFailure { error ->
                analytics?.logAssistantError(AssistantError.Module.COMMANDS_PARSING, error)
            }
        }
    }

    private fun JsonObject.mapToSpeakOut(): ServerCommand.SpeakOut {
        return ServerCommand.SpeakOut(
            Tts(
                streamId = getString("stream_id") ?: parsingError("$TYPE_TTS: missing url"),
                isBlocking = getBoolean("blocking", default = true),
                isPlayingForced = getBoolean("force_say", default = false),
                kwsSkipIntervals = kwsSkipIntervalsParser.parse(json = this),
            )
        )
    }

    private fun JsonObject.mapToListenCommand(): ServerCommand.ListenCommand {
        return ServerCommand.ListenCommand(
            minWaitingTime = getInt("min_waiting_time"),
            muteActivationSound = getBoolean("mute_activation_sound", default = false),
            callbackData = getString("callback_data"),
        )
    }

    private fun JsonObject.mapToDialogSound(): ServerCommand.PlayDialogSound {
        return ServerCommand.PlayDialogSound(
            text = getObject("meta")?.getString("title").orEmpty(),
            url = getString("url") ?: parsingError("sound: missing url"),
            kwsSkipIntervals = kwsSkipIntervalsParser.parse(json = this),
        )
    }

    private fun JsonObject.mapToMailCardCount(): ServerCommand.MailCountCard {
        return ServerCommand.MailCountCard(
            text = getString("text").orEmpty(),
            linkLabel = getString("link_text"),
            linkUrl = getString("link"),
        )
    }

    private fun JsonObject.mapToFlowMod(): ServerCommand.FlowMode {
        val flowMode = getString("flowmode_type") ?: parsingError("Missing flow mode type")
        return ServerCommand.FlowMode(flowMode)
    }

    private fun JsonObject.mapToSearchCard(): ServerCommand.ShowSearchCard? {
        return ServerCommand.ShowSearchCard(
            text = getString("text").orEmpty(),
            linkLabel = getString("link_text"),
            linkUrl = getString("link_url") ?: return null
        )
    }

    private fun JsonObject.mapToFact(): ServerCommand.ShowFact {
        val dao = gson.fromJson<CardFactDto>(toString())

        val appRefs = dao.appRefs
            ?.map(AppRefDto::toDomain)
            .orEmpty()
        val refs = dao.text
            ?.let { appsRefParser.parse(it, appRefs, AppRef::name) }
            ?: Pair(dao.text, emptyList())

        val linkTitle = dao.url
            ?.takeIfNotEmpty()
            ?.let(::URL)
            ?.authority

        return ServerCommand.ShowFact(
            title = dao.title,
            text = refs.first,
            fullText = dao.fullText,
            link = dao.url,
            linkTitle = linkTitle,
            imageUrl = dao.imageUrl,
            searchUrl = dao.searchUrl,
            searchTitle = dao.searchTitle,
            appRefs = refs.second,
        )
    }

    private fun JsonObject.mapToWeather(): ServerCommand.ShowWeather {
        return gson.fromJson<WeatherCardDto>(toString())
            .mapToCommand()
    }

    private fun WeatherCardDto.mapToCommand(): ServerCommand.ShowWeather {
        return ServerCommand.ShowWeather(
            temperature = temperature,
            city = city,
            cityFormed = cityFormed,
            dateFormed = dateFormed,
            pressure = pressure,
            windSpeed = windSpeed,
            humidity = humidity,
            iconType = iconType,
            iconUrl = iconUrl,
            morning = periods?.morning?.toDomain(),
            daytime = periods?.daytime?.toDomain(),
            night = periods?.night?.toDomain(),
            url = url,
        )
    }

    private fun JsonObject.mapToNews(): ServerCommand.ShowNewsCard {
        val news = getArray("news")
            ?.mapNotNull(JsonElement::toObject)
            ?.map {
                News(
                    title = it.getString("title"),
                    text = it.getString("text"),
                    url = it.getString("url"),
                )
            }
            .orEmpty()
        return ServerCommand.ShowNewsCard(
            title = getString("title"),
            news = news,
        )
    }

    private fun JsonObject.mapToSlider(): ServerCommand.ShowSlider? {
        val dto = gson.fromJson<SlidersDto>(toString())
            .takeIf { !it.items.isNullOrEmpty() }
            ?: return null
        val items = dto.items?.map(SlidersItemDto::toDomain).orEmpty()
        val extensionItem = dto.moreItem?.let(SlidersItemDto::toDomain)
        return ServerCommand.ShowSlider(items, extensionItem)
    }

    private fun JsonObject.mapToCinemaMovies(): ServerCommand.ShowMovies? {
        val dto = gson.fromJson<CinemaMoviesDto>(toString())
            .takeIf { !it.movies.isNullOrEmpty() }
            ?: return null
        val movies = dto.movies
            ?.map(CinemaMovieDto::toDomain)
            .orEmpty()
        return ServerCommand.ShowMovies(movies)
    }

    private fun JsonObject.mapToMovieTimetable(): ServerCommand.ShowMovieTimetable {
        val dto = gson.fromJson<MovieTimetableDto>(toString())
        return ServerCommand.ShowMovieTimetable(
            title = dto.movie?.title,
            genres = dto.movie?.genres.orEmpty(),
            posterUrl = dto.movie?.poster,
            url = dto.movie?.url,
            cinemaSessions = dto.schedule?.map(CinemaSessionDto::toDomain).orEmpty(),
        )
    }

    private fun JsonObject.mapToCinemaTheaterTimetable(): ServerCommand.ShowCinemaTheaterTimetable {
        val dto = gson.fromJson<CinemaTheaterTimetableDto>(toString())
        return ServerCommand.ShowCinemaTheaterTimetable(
            title = dto.theater?.title,
            address = dto.theater?.address,
            url = dto.theater?.url,
            movieSessions = dto.schedule?.map(MovieSessionDto::toDomain).orEmpty(),
        )
    }

    private fun JsonObject.mapToImages(): ServerCommand {
        val images = getArray("urls_with_preview")
            ?.map { it.toObject() ?: parsingError("$TYPE_IMAGES: can't parse image object") }
            ?.map { it.mapToImage() }
            .orEmpty()
        if (images.isEmpty()) parsingError("$TYPE_IMAGES: empty images list")
        val searchUrl = getString("search_url")
        return ServerCommand.ShowImages(images, searchUrl)
    }

    private fun JsonObject.mapToImage(): ImageData {
        val thumbnailObject = getObject("thumb")
        val thumbnailUrl = thumbnailObject?.getString("url")
        val imageObject = getObject("image")
        val imageUrl = imageObject?.getString("url")?.toHttpsIfNeeded()
        if (imageUrl.isNullOrEmpty() && thumbnailUrl.isNullOrEmpty()) parsingError("$TYPE_IMAGES: missing image url")

        val image = imageUrl?.let {
            Image(
                url = it,
                width = imageObject.getInt("width"),
                height = imageObject.getInt("height"),
                ext = imageObject.getString("ext"),
                size = imageObject.getLong("size"),
            )
        }
        val thumbnail = thumbnailUrl?.let {
            Thumbnail(
                url = it,
                width = thumbnailObject.getInt("width"),
                height = thumbnailObject.getInt("height"),
            )
        }
        return ImageData(image, thumbnail)
    }

    private fun JsonObject.mapToMedia(
        playlist: Lazy<MutableList<AudioTrack>>,
        podcasts: Lazy<MutableList<AudioTrack>>,
    ): ServerCommand? {
        val autoPlay = getBoolean("autoplay", default = true)
        return when (getInt("media_type", default = -1)) {
            PlaylistParser.MEDIA_TYPE_MUSIC -> {
                val track = audioTrackParser.parse(jsonObject = this)
                val isInitialized = playlist.isInitialized()
                playlist.value.add(track)
                runIf(!isInitialized) {
                    ServerCommand.PlaylistMusic(
                        playlist = playlist.value,
                        autoPlay = autoPlay,
                        trackNumber = 0,
                        trackPosition = 0f,
                    )
                }
            }

            PlaylistParser.MEDIA_TYPE_PODCAST -> {
                val track = audioTrackParser.parse(jsonObject = this)
                val isInitialized = podcasts.isInitialized()
                podcasts.value.add(track)
                runIf(!isInitialized) {
                    ServerCommand.PlaylistPodcast(
                        playlist = podcasts.value,
                        autoPlay = autoPlay,
                        trackNumber = 0,
                        trackPosition = 0f,
                    )
                }
            }

            PlaylistParser.MEDIA_TYPE_RADIO -> {
                val radioStation = radioParser.parse(jsonObject = this)
                ServerCommand.PlaylistRadio(
                    playlist = listOf(radioStation),
                    autoPlay = autoPlay,
                    trackNumber = 0,
                )
            }

            PlaylistParser.MEDIA_TYPE_NOISE -> {
                val sound = noiseParser.parse(jsonObject = this)
                ServerCommand.PlaylistNoise(
                    playlist = listOf(sound),
                    autoPlay = autoPlay,
                    trackNumber = 0,
                    trackPosition = 0f,
                )
            }

            else -> {
                val tale = taleParser.parse(jsonObject = this)
                ServerCommand.PlaylistTale(
                    playlist = listOf(tale),
                    autoPlay = autoPlay,
                    trackNumber = 0,
                    trackPosition = 0f,
                )
            }
        }
    }

    private fun JsonObject.mapToDelay(): ServerCommand.Delay {
        return getLong(name = "length", default = -1)
            .takeIf { it > 0 }
            ?.let { ServerCommand.Delay(it) }
            ?: parsingError("$TYPE_DELAY: unsupported length")
    }

    private fun JsonObject.mapToAuthorizeCard(): ServerCommand.AuthorizeCard {
        return ServerCommand.AuthorizeCard(
            text = getString("text").orEmpty(),
            authType = getString("auth_type").orEmpty(),
            repeatText = getObject("repeat_data")?.getString("text").orEmpty()
        )
    }

    private fun JsonObject.mapToEventList(): ServerCommand.EventListCard {
        val skills = getAsJsonArray("items")
            .map {
                it.toObject()
                    ?.mapToEvent()
                    ?: parsingError("$TYPE_EVENT_LIST: illegal event format")
            }
        return ServerCommand.EventListCard(skills)
    }

    private fun JsonObject.mapToEvent(): EventItemDescription {
        val textSuggest = getString("subtitle")
            ?.let { Suggest.Text(text = it, payload = it, callbackData = null) }
        val event = getObject("event")?.let(suggestsParser::parseEventSuggest)
        val icons = getObject("icons")
        val skillIcons = SkillIcons(
            light = icons?.getString("light"),
            dark = icons?.getString("dark"),
        )
        return EventItemDescription(
            title = getString("title").orEmpty(),
            subtitle = getString("subtitle"),
            suggest = event ?: textSuggest,
            icon = getString("icon"),
            icons = skillIcons,
        )
    }

    private fun JsonObject.mapToCompanyCard(): ServerCommand.CompanyCard {
        return gson.fromJson<CompanyCardDto>(toString()).run {
            ServerCommand.CompanyCard(
                title = title,
                address = address,
                phones = phones,
                imageUrl = imageUrl,
                schedule = schedule,
                metro = metro,
                mapUrl = mapUrl,
                routeUrl = routeUrl,
                siteUrl = siteUrl,
                category = category,
                description = description,
                distance = distance,
                rating = rating,
            )
        }
    }

    private fun JsonObject.mapToSerpCard(): ServerCommand.SerpCard {
        val dto = gson.fromJson<SerpDto>(toString())
        val items = dto.items.map(SerpItemDto::toDomain)
        return ServerCommand.SerpCard(items, dto.searchUrl)
    }

    private fun JsonObject.mapToRecipes(): ServerCommand.Recipes {
        val items = getArray("items")
            ?.mapNotNull { it.toObject()?.mapToRecipe() }
            ?.takeIf { it.isNotEmpty() }
            ?: parsingError("$TYPE_RECIPES: missing items")
        val searchUrl = getString("search_url")
        return ServerCommand.Recipes(items, searchUrl)
    }

    private fun JsonObject.mapToRecipe(): Recipe {
        return Recipe(
            title = getString("title"),
            text = getString("text"),
            url = getString("url_full"),
            urlShort = getString("url_short"),
            imageUrl = getString("image_url"),
        )
    }

    private fun String.toHttpsIfNeeded(): String {
        return if (NetworkUtils.isClearTextTrafficPermitted || this.startsWith("https")) {
            this
        } else {
            this.replace("http", "https")
        }
    }

    private fun MutableList<ServerQueuedCommand>.addSyncCommand(serverCommand: ServerCommand) {
        add(ServerQueuedCommand(QueueType.SYNC, serverCommand))
    }

    private fun MutableList<ServerQueuedCommand>.addSyncCommand(
        index: Int,
        serverCommand: ServerCommand
    ) {
        add(index, ServerQueuedCommand(QueueType.SYNC, serverCommand))
    }

    private fun MutableList<ServerQueuedCommand>.addMediaCommand(serverCommand: ServerCommand) {
        add(ServerQueuedCommand(QueueType.MEDIA, serverCommand))
    }
}