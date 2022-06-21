package ru.mail.search.assistant.commands.command.media

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.CommandsStateAdapter
import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.assistant.AssistantStatus
import ru.mail.search.assistant.entities.audio.KwsSkipInterval
import ru.mail.search.assistant.util.Tag

internal class PlayDialogSoundCommand(
    private val player: SoundPlayer,
    private val isBlocking: Boolean,
    private val isPlayingForced: Boolean,
    private val kwsSkipIntervals: List<KwsSkipInterval>?,
    private val stateAdapter: CommandsStateAdapter,
    private val commandsFactory: CommandsFactory,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing sound play command")
        if (isSoundSuppressed(context)) return
        rtLogDevicePhraseExtraDataEvent.onStartMedia(context.phrase.requestId)
        val soundCommand = commandsFactory.playSound(player, kwsSkipIntervals)
        if (context.assistant.isSilenced) return
        val soundResult = context.async(soundCommand)
        if (isBlocking) {
            enableTtsStatus(context)
            runCatching { soundResult.await() }
                .also { disableTtsStatus(context) }
                .getOrThrow()
        }
    }

    override suspend fun notify(context: NotificationContext) {
        if (context.cause == CommandNotification.Silence ||
            context.cause == CommandNotification.Revoke ||
            context.cause is CommandNotification.UserInput.Initiated
        ) {
            context.assistant.revoke()
        }
    }

    private suspend fun enableTtsStatus(context: ExecutionContext) {
        stateAdapter.setContextAssistantStatus(context, AssistantStatus.TTS)
    }

    private suspend fun disableTtsStatus(context: ExecutionContext) {
        withContext(NonCancellable) {
            stateAdapter.resetContextAssistantStatus(context)
        }
    }

    private fun isSoundSuppressed(context: ExecutionContext): Boolean {
        return !isPlayingForced && context.refuseDialogInteraction()
    }
}