package ru.mail.search.assistant.data.local.messages

import ru.mail.search.assistant.data.local.db.entity.MessageEntity
import ru.mail.search.assistant.data.local.messages.converter.*
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData

class MessageEntityConverter {

    private val converters = HashMap<String, MessageDataPayloadConverter<*>>()

    private val outgoingDataConverter = OutgoingDataPayloadConverter()
    private val incomingTextConverter = IncomingTextPayloadConverter()
    private val factConverter = FactPayloadConverter()
    private val searchCardConverter = SearchCardPayloadConverter()
    private val actionCardConverter = ActionCardPayloadConverter()
    private val newsCardConverter = NewsCardPayloadConverter()
    private val radioMsgConverter = RadioMsgPayloadConverter()
    private val radioPlaylistMsgPayloadConverter = RadioPlaylistMsgPayloadConverter()
    private val soundMsgConverter = SoundMsgPayloadConverter()
    private val soundPlaylistMsgPayloadConverter = SoundPlaylistMsgPayloadConverter()
    private val taleMsgConverter = TaleMsgPayloadConverter()
    private val talePlaylistMsgConverter = TalePlaylistMsgPayloadConverter()
    private val podcastMsgConverter = PodcastMsgPayloadConverter()
    private val podcastsMsgPayloadConverter = PodcastsMsgPayloadConverter()
    private val tracksMsgConverter = TracksMsgPayloadConverter()
    private val weatherConverter = WeatherPayloadConverter()
    private val sliderConverter = SliderPayloadConverter()
    private val moviesMsgConverter = MoviesMsgPayloadConverter()
    private val movieTimetableMsgConverter = MovieTimetableMsgPayloadConverter()
    private val cinemaTheaterTimetableMsgConverter = CinemaTheaterTimetableMsgPayloadConverter()
    private val photoAlbumPayloadConverter = PhotoAlbumPayloadConverter()
    private val authCardPayloadConverter = AuthCardPayloadConverter()
    private val eventListCardMsgConverter = EventListMsgPayloadConverter()
    private val mailcountPayloadConverter = MailcountPayloadConverter()
    private val companyPayloadConverter = CompanyPayloadConverter()
    private val serpPayloadConverter = SerpPayloadConverter()
    private val recipesCardPayloadConverter = RecipesCardPayloadConverter()
    private val mailRuSingleLetterPayloadConverter = MailRuSingleLetterPayloadConverter()
    private val mailRuSendersMailsPayloadConverter = MailRuSendersMailsPayloadConverter()
    private val mailRuBoxStatusPayloadConverter = MailRuBoxStatusPayloadConverter()
    private val mailRuAuthRequestPayloadConverter = MailRuAuthRequestPayloadConverter()
    private val winkPayloadConverter = WinkPayloadConverter()
    private val miniAppPayloadConverter = MiniAppPayloadConverter()
    private val permissionRequestPayloadConverter = PermissionRequestPayloadConverter()
    private val contactsCardPayloadConverter = ContactsCardPayloadConverter()
    private val contactCardPayloadConverter = ContactCardPayloadConverter()
    private val emergencyCallCardPayloadConverter = EmergencyCallCardPayloadConverter()
    private val contactNumbersCardPayloadConverter = ContactNumbersCardPayloadConverter()

    init {
        registerConverter(outgoingDataConverter)
        registerConverter(incomingTextConverter)
        registerConverter(factConverter)
        registerConverter(searchCardConverter)
        registerConverter(actionCardConverter)
        registerConverter(newsCardConverter)
        registerConverter(radioMsgConverter)
        registerConverter(radioPlaylistMsgPayloadConverter)
        registerConverter(soundMsgConverter)
        registerConverter(soundPlaylistMsgPayloadConverter)
        registerConverter(taleMsgConverter)
        registerConverter(talePlaylistMsgConverter)
        registerConverter(podcastMsgConverter)
        registerConverter(tracksMsgConverter)
        registerConverter(weatherConverter)
        registerConverter(sliderConverter)
        registerConverter(moviesMsgConverter)
        registerConverter(movieTimetableMsgConverter)
        registerConverter(cinemaTheaterTimetableMsgConverter)
        registerConverter(photoAlbumPayloadConverter)
        registerConverter(authCardPayloadConverter)
        registerConverter(eventListCardMsgConverter)
        registerConverter(mailcountPayloadConverter)
        registerConverter(companyPayloadConverter)
        registerConverter(serpPayloadConverter)
        registerConverter(podcastsMsgPayloadConverter)
        registerConverter(recipesCardPayloadConverter)
        registerConverter(mailRuSingleLetterPayloadConverter)
        registerConverter(mailRuSendersMailsPayloadConverter)
        registerConverter(mailRuBoxStatusPayloadConverter)
        registerConverter(mailRuAuthRequestPayloadConverter)
        registerConverter(winkPayloadConverter)
        registerConverter(miniAppPayloadConverter)
        registerConverter(permissionRequestPayloadConverter)
        registerConverter(contactsCardPayloadConverter)
        registerConverter(contactCardPayloadConverter)
        registerConverter(emergencyCallCardPayloadConverter)
        registerConverter(contactNumbersCardPayloadConverter)
    }

    fun entityToMessage(entity: MessageEntity): DialogMessage {
        return DialogMessage(
            entity.creationTime.time,
            entity.phraseId,
            entity.creationTime,
            getConverter(entity.type).payloadToPojo(entity.payload)
        )
    }

    fun messageToEntity(message: DialogMessage): MessageEntity {
        return when (val data = message.data) {
            is MessageData.OutgoingData -> convert(outgoingDataConverter, message, data)
            is MessageData.IncomingText -> convert(incomingTextConverter, message, data)
            is MessageData.Fact -> convert(factConverter, message, data)
            is MessageData.ActionCard -> convert(actionCardConverter, message, data)
            is MessageData.NewsCard -> convert(newsCardConverter, message, data)
            is MessageData.Player.RadioMsg -> convert(radioPlaylistMsgPayloadConverter, message, data)
            is MessageData.Player.SoundMsg -> convert(soundPlaylistMsgPayloadConverter, message, data)
            is MessageData.Player.TaleMsg -> convert(talePlaylistMsgConverter, message, data)
            is MessageData.Player.TracksMsg -> convert(tracksMsgConverter, message, data)
            is MessageData.Player.PodcastMsg -> convert(podcastMsgConverter, message, data)
            is MessageData.Weather -> convert(weatherConverter, message, data)
            is MessageData.Slider -> convert(sliderConverter, message, data)
            is MessageData.Movies -> convert(moviesMsgConverter, message, data)
            is MessageData.MovieTimetable -> convert(movieTimetableMsgConverter, message, data)
            is MessageData.CinemaTimetable -> convert(cinemaTheaterTimetableMsgConverter, message, data)
            is MessageData.PhotoAlbum -> convert(photoAlbumPayloadConverter, message, data)
            is MessageData.AuthorizeCard -> convert(authCardPayloadConverter, message, data)
            is MessageData.EventListCard -> convert(eventListCardMsgConverter, message, data)
            is MessageData.CompanyCard -> convert(companyPayloadConverter, message, data)
            is MessageData.SerpCard -> convert(serpPayloadConverter, message, data)
            is MessageData.Player.PodcastsMsg -> convert(podcastsMsgPayloadConverter, message, data)
            is MessageData.RecipesCard -> convert(recipesCardPayloadConverter, message, data)
            is MessageData.MailRuSingleLetter -> convert(mailRuSingleLetterPayloadConverter, message, data)
            is MessageData.MailRuSenderMails -> convert(mailRuSendersMailsPayloadConverter, message, data)
            is MessageData.MailRuBoxStatus -> convert(mailRuBoxStatusPayloadConverter, message, data)
            is MessageData.MailRuAuthRequest -> convert(mailRuAuthRequestPayloadConverter, message, data)
            is MessageData.Wink -> convert(winkPayloadConverter, message, data)
            is MessageData.MiniApp -> convert(miniAppPayloadConverter, message, data)
            is MessageData.PermissionRequest -> convert(permissionRequestPayloadConverter, message, data)
            is MessageData.ContactsCard -> convert(contactsCardPayloadConverter, message, data)
            is MessageData.ContactCard -> convert(contactCardPayloadConverter, message, data)
            is MessageData.EmergencyCallCard -> convert(emergencyCallCardPayloadConverter, message, data)
            is MessageData.ContactNumbersCard -> convert(contactNumbersCardPayloadConverter, message, data)

            is MessageData.OutgoingLoading ->
                throw RuntimeException("Unsupported: you are trying to save temporary message to db")

            is MessageData.ShortcutSuggest,
            is MessageData.WelcomeScreenMessage,
            is MessageData.WidgetsScreenMessage ->
                throw RuntimeException("Unsupported: you are trying to save temporary message to db")
        }
    }

    private fun <T : MessageData> convert(
        converter: MessageDataPayloadConverter<T>,
        message: DialogMessage,
        data: T
    ): MessageEntity {
        return MessageEntity(
            type = converter.type,
            phraseId = message.phraseId,
            creationTime = message.date,
            payload = converter.pojoToPayload(data)
        )
    }

    private fun getConverter(type: String): MessageDataPayloadConverter<*> {
        return converters[type] ?: throw IllegalArgumentException("$type converter not found")
    }

    private fun registerConverter(converter: MessageDataPayloadConverter<*>) {
        converters[converter.type] = converter
    }
}