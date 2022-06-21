package ru.mail.search.assistant.commands.factory

import ru.mail.search.assistant.api.suggests.Suggest
import ru.mail.search.assistant.commands.command.media.SoundPlayer
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.entities.PhraseMetadata
import ru.mail.search.assistant.entities.audio.KwsSkipInterval
import ru.mail.search.assistant.entities.message.MessageData

/**
 * Commands factory which can be used by assistant client
 */
interface PublicCommandsFactory {

    fun showMessage(data: MessageData): ExecutableCommand<*>

    fun showWelcomeMessage(data: MessageData): ExecutableCommand<*>

    fun showSuggests(suggests: List<Suggest>): ExecutableCommand<*>

    fun playTts(
        url: String,
        isBlocking: Boolean,
        isPlayingForced: Boolean,
        kwsSkipInterval: List<KwsSkipInterval>?
    ): ExecutableCommand<*>

    fun playDialogSound(
        player: SoundPlayer,
        isBlocking: Boolean = false,
        isPlayingForced: Boolean = false,
        kwsSkipInterval: List<KwsSkipInterval>? = null
    ): ExecutableCommand<*>

    fun awaitUserInput(): ExecutableCommand<*>

    fun sendTextPhrase(
        text: String,
        callbackData: String? = null,
        clientData: String? = null,
        showMessage: Boolean = true
    ): ExecutableCommand<PhraseMetadata>

    fun sendEvent(
        event: String,
        callbackData: String? = null,
        clientData: String? = null,
        params: Map<String, String> = emptyMap()
    ): ExecutableCommand<PhraseMetadata>

    fun sendPushPayload(
        pushId: String?,
        callbackData: String
    ): ExecutableCommand<PhraseMetadata>

    fun executePhraseCommand(command: ExecutableCommand<PhraseResult>): ExecutableCommand<PhraseMetadata>

    fun executeNestedCommands(commands: List<ExecutableCommandData>): ExecutableCommand<*>
}