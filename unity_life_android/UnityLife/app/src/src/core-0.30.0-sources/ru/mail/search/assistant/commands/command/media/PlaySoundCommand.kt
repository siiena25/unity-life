package ru.mail.search.assistant.commands.command.media

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.main.skipkws.KwsSkipController
import ru.mail.search.assistant.commands.main.skipkws.SoundPlayerKwsSkipController
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.entities.audio.KwsSkipInterval
import ru.mail.search.assistant.interactor.AudioFocusHandler

internal class PlaySoundCommand(
    private val player: SoundPlayer,
    private val audioFocusHandler: AudioFocusHandler,
    private val kwsSkipIntervals: List<KwsSkipInterval>?,
    private val kwsSkipController: KwsSkipController,
    private val poolDispatcher: PoolDispatcher
) : ExecutableCommand<Unit> {

    private val commandContext = Job()

    override suspend fun execute(context: ExecutionContext) {
        if (context.assistant.isSilenced) return
        withContext(commandContext) {
            audioFocusHandler.withMusicDuck {
                withKwsSkip { kwsSkipController ->
                    play(context, kwsSkipController)
                }
            }
        }
    }

    override suspend fun notify(context: NotificationContext) {
        if (context.cause == CommandNotification.Silence ||
            context.cause == CommandNotification.Revoke ||
            context.cause is CommandNotification.UserInput.Initiated
        ) {
            context.assistant.revoke()
            commandContext.cancel()
        }
    }

    private suspend fun play(
        context: ExecutionContext,
        kwsSkipController: SoundPlayerKwsSkipController?
    ) {
        if (context.assistant.isSilenced) return
        player.play(context.phrase.requestId)
            .onEach { state -> kwsSkipController?.onStateChanged(state) }
            .first { state -> state is SoundPlayer.State.Finished }
    }

    private fun SoundPlayerKwsSkipController.onStateChanged(state: SoundPlayer.State) {
        if (state is SoundPlayer.State.Playing) {
            onMediaResumed(state.eventTime, state.position)
        } else {
            onMediaPaused()
        }
    }

    private inline fun withKwsSkip(block: (kwsSkipController: SoundPlayerKwsSkipController?) -> Unit) {
        val kwsSkipController = createKwsSkipController()
        if (kwsSkipController != null) {
            runCatching { block(kwsSkipController) }
                .also { kwsSkipController.release() }
                .getOrThrow()
        } else {
            block(null)
        }
    }

    private fun createKwsSkipController(): SoundPlayerKwsSkipController? {
        kwsSkipIntervals ?: return null
        return SoundPlayerKwsSkipController(kwsSkipIntervals, kwsSkipController, poolDispatcher)
    }
}