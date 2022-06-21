package ru.mail.search.assistant.commands.factory

import ru.mail.search.assistant.api.suggests.Suggest
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.commands.CommandsStateAdapter
import ru.mail.search.assistant.commands.command.*
import ru.mail.search.assistant.commands.command.media.*
import ru.mail.search.assistant.commands.command.message.AddContactNumbersCard
import ru.mail.search.assistant.commands.command.message.AddContactsCard
import ru.mail.search.assistant.commands.command.message.AddMessage
import ru.mail.search.assistant.commands.command.message.EmergencyCallCard
import ru.mail.search.assistant.commands.command.message.PhoneCallCard
import ru.mail.search.assistant.commands.command.message.ShowWelcomeMessage
import ru.mail.search.assistant.commands.command.playtrack.*
import ru.mail.search.assistant.commands.command.sms.EditSmsCommand
import ru.mail.search.assistant.commands.command.sms.SendSmsCommand
import ru.mail.search.assistant.commands.main.skipkws.KwsSkipController
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.*
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.data.ContactsManager
import ru.mail.search.assistant.data.remote.RemoteDataSource
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.audio.*
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.interactor.AudioFocusHandler
import ru.mail.search.assistant.media.wrapper.StreamPlayerAdapter
import ru.mail.search.assistant.media.wrapper.TtsPlayer

@SuppressWarnings("TooManyFunctions")
internal class CommandsFactory(
    private val sessionProvider: SessionCredentialsProvider,
    private val messagesRepository: MessagesRepository,
    private val musicController: CommandsMusicController,
    private val ttsPlayer: TtsPlayer,
    private val streamPlayer: StreamPlayerAdapter,
    private val assistantContextRepository: AssistantContextRepository,
    private val stateAdapter: CommandsStateAdapter,
    private val audioFocusHandler: AudioFocusHandler,
    private val kwsSkipController: KwsSkipController,
    private val poolDispatcher: PoolDispatcher,
    private val remoteDataSource: RemoteDataSource,
    private val splitExperimentParamProvider: SplitExperimentParamProvider?,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val clientStateRepository: ClientStateRepository,
    private val contactsManager: ContactsManager,
    private val userInputCommandsFactory: UserInputCommandsFactory,
    private val contactsRepository: ContactsRepository,
    private val publicCommandsFactory: PublicCommandsFactory,
    private val logger: Logger?,
) {

    fun playMediaMessage(
        messageId: Long,
        autoPlay: Boolean,
        trackNumber: Int,
        trackPosition: Float
    ): PlayMediaMessage {
        return PlayMediaMessage(
            messageId,
            autoPlay,
            trackNumber,
            trackPosition,
            musicController,
            logger
        )
    }

    fun playTts(phraseId: String, tts: Tts): PlayDialogSoundCommand {
        val player = InternalTtsSoundPlayer(
            phraseId,
            tts.streamId,
            ttsPlayer,
            rtLogDevicePhraseExtraDataEvent,
            sessionProvider,
            remoteDataSource,
            splitExperimentParamProvider,
            poolDispatcher
        )
        return playDialogSound(
            player = player,
            isBlocking = tts.isBlocking,
            isPlayingForced = tts.isPlayingForced,
            kwsSkipIntervals = tts.kwsSkipIntervals
        )
    }

    fun playTts(
        url: String,
        isBlocking: Boolean,
        isPlayingForced: Boolean,
        kwsSkipIntervals: List<KwsSkipInterval>?
    ): PlayDialogSoundCommand {
        val player = ExternalTtsSoundPlayer(
            url,
            ttsPlayer,
            rtLogDevicePhraseExtraDataEvent,
            poolDispatcher
        )
        return playDialogSound(
            player = player,
            isBlocking = isBlocking,
            isPlayingForced = isPlayingForced,
            kwsSkipIntervals = kwsSkipIntervals
        )
    }

    fun playDialogSound(
        player: SoundPlayer,
        isBlocking: Boolean,
        isPlayingForced: Boolean,
        kwsSkipIntervals: List<KwsSkipInterval>?
    ): PlayDialogSoundCommand {
        return PlayDialogSoundCommand(
            player = player,
            isBlocking = isBlocking,
            isPlayingForced = isPlayingForced,
            kwsSkipIntervals = kwsSkipIntervals,
            stateAdapter = stateAdapter,
            commandsFactory = this,
            rtLogDevicePhraseExtraDataEvent = rtLogDevicePhraseExtraDataEvent,
            logger = logger
        )
    }

    fun playDialogMedia(
        url: String,
        kwsSkipIntervals: List<KwsSkipInterval>?
    ): PlayDialogSoundCommand {
        val player = DialogSoundPlayer(
            url,
            streamPlayer,
            rtLogDevicePhraseExtraDataEvent,
            poolDispatcher
        )
        return playDialogSound(
            player = player,
            isBlocking = true,
            isPlayingForced = true,
            kwsSkipIntervals = kwsSkipIntervals
        )
    }

    fun playSound(player: SoundPlayer, kwsSkipIntervals: List<KwsSkipInterval>?): PlaySoundCommand {
        return PlaySoundCommand(
            player,
            audioFocusHandler,
            kwsSkipIntervals,
            kwsSkipController,
            poolDispatcher
        )
    }

    fun addMessage(phraseId: String, data: MessageData): AddMessage {
        return AddMessage(phraseId, data, messagesRepository, poolDispatcher, logger)
    }

    fun waitForUserInput(
        minWaitingTime: Int?,
        muteActivationSound: Boolean,
        callbackData: String?
    ): WaitForUserInput {
        return WaitForUserInput(
            assistantContextRepository,
            clientStateRepository,
            minWaitingTime,
            muteActivationSound,
            logger,
            callbackData
        )
    }

    fun enterFlowMode(flowModeModel: String): EnterFlowModeCommand {
        return EnterFlowModeCommand(
            flowModeModel,
            assistantContextRepository,
            clientStateRepository,
            logger
        )
    }

    fun playSound(
        phraseId: String,
        playlist: List<Sound>,
        autoPlay: Boolean,
        trackNumber: Int,
        trackPosition: Float
    ): PlaySound {
        return PlaySound(
            phraseId,
            autoPlay,
            playlist,
            trackNumber,
            trackPosition,
            this,
            rtLogDevicePhraseExtraDataEvent,
            logger
        )
    }

    fun playMusic(
        phraseId: String,
        playlist: List<AudioTrack>,
        autoPlay: Boolean,
        trackNumber: Int,
        trackPosition: Float
    ): PlayMusic {
        return PlayMusic(
            phraseId,
            playlist,
            autoPlay,
            trackNumber,
            trackPosition,
            this,
            rtLogDevicePhraseExtraDataEvent,
            logger
        )
    }

    fun playPodcasts(
        phraseId: String,
        playlist: List<AudioTrack>,
        autoPlay: Boolean,
        trackNumber: Int,
        trackPosition: Float
    ): PlayPodcasts {
        return PlayPodcasts(
            phraseId,
            autoPlay,
            playlist,
            trackNumber,
            trackPosition,
            this,
            rtLogDevicePhraseExtraDataEvent,
            logger
        )
    }

    fun resumeMusic(): ResumeMusic {
        return ResumeMusic(
            musicController,
            messagesRepository,
            poolDispatcher,
            logger
        )
    }

    fun playRadio(
        phraseId: String,
        playlist: List<Radiostation>,
        autoPlay: Boolean,
        trackNumber: Int
    ): PlayRadio {
        return PlayRadio(
            phraseId,
            autoPlay,
            playlist,
            trackNumber,
            this,
            rtLogDevicePhraseExtraDataEvent,
            logger
        )
    }

    fun playTale(
        phraseId: String,
        playlist: List<Tale>,
        autoPlay: Boolean,
        trackNumber: Int,
        trackPosition: Float
    ): PlayTale {
        return PlayTale(
            phraseId,
            playlist,
            autoPlay,
            trackNumber,
            trackPosition,
            this,
            rtLogDevicePhraseExtraDataEvent,
            logger
        )
    }

    fun awaitUserInputWithTimer(time: Long): AwaitUserInputWithTimer {
        return AwaitUserInputWithTimer(time, logger)
    }

    fun showSuggests(phraseId: String, suggests: List<Suggest>): ShowSuggests {
        return ShowSuggests(phraseId, suggests, messagesRepository, logger)
    }

    fun showWelcomeDialogMessage(messageData: MessageData): ShowWelcomeMessage {
        return ShowWelcomeMessage(messageData, messagesRepository, logger)
    }

    fun showUrl(url: String): ShowUrl {
        return ShowUrl(url, assistantContextRepository, logger)
    }

    fun requestPermission(permission: String, requestCode: Int): RequestPermissionsCommand {
        return RequestPermissionsCommand(
            permission,
            requestCode,
            assistantContextRepository
        )
    }

    fun syncContacts(event: String): SyncContactsCommand {
        return SyncContactsCommand(
            event,
            contactsManager,
            userInputCommandsFactory,
            publicCommandsFactory,
            contactsRepository
        )
    }

    fun contactsCard(
        phraseId: String,
        hasMore: Boolean,
        page: Int,
        contactIds: List<Int>,
        callbackEvent: String,
    ): AddContactsCard {
        return AddContactsCard(
            phraseId,
            hasMore,
            page,
            contactIds,
            callbackEvent,
            contactsRepository,
            commandsFactory = this,
        )
    }

    fun contactNumbersCard(
        phraseId: String,
        contactId: Int,
        callbackEvent: String,
    ): AddContactNumbersCard {
        return AddContactNumbersCard(
            contactId,
            callbackEvent,
            phraseId,
            contactsRepository,
            commandsFactory = this,
        )
    }

    fun findPhoneByPart(contactId: Int, phonePart: String, callbackEvent: String): FindPhoneByPartCommand {
        return FindPhoneByPartCommand(
            contactId,
            phonePart,
            callbackEvent,
            contactsRepository,
            userInputCommandsFactory,
            publicCommandsFactory,
        )
    }

    fun phoneCall(phoneNumberId: Int): PhoneCallCommand {
        return PhoneCallCommand(
            phoneNumberId,
            contactsRepository,
            assistantContextRepository,
        )
    }

    fun phoneCallCard(phraseId: String, contactId: Int, phoneNumberId: Int): PhoneCallCard {
        return PhoneCallCard(
            phraseId,
            contactId,
            phoneNumberId,
            contactsRepository,
            commandsFactory = this,
        )
    }

    fun emergencyCall(phoneNumber: String): EmergencyCallCommand {
        return EmergencyCallCommand(
            phoneNumber,
            assistantContextRepository,
        )
    }

    fun emergencyCallCard(phraseId: String, phoneNumber: String): EmergencyCallCard {
        return EmergencyCallCard(
            phraseId,
            phoneNumber,
            commandsFactory = this,
        )
    }

    fun editSms(text: String): EditSmsCommand = EditSmsCommand(text, assistantContextRepository)

    fun sendSms(numberId: Int, text: String): SendSmsCommand {
        return SendSmsCommand(
            numberId,
            text,
            contactsRepository,
            assistantContextRepository,
        )
    }
}