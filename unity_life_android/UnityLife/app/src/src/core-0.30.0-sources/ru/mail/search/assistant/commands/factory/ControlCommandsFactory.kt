package ru.mail.search.assistant.commands.factory

import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.command.media.RepeatMode
import ru.mail.search.assistant.commands.command.media.control.*
import ru.mail.search.assistant.common.util.Logger

internal class ControlCommandsFactory(
    private val musicController: CommandsMusicController,
    private val logger: Logger?
) {

    fun setVolume(volume: Int): SetVolume {
        return SetVolume(musicController, volume, logger)
    }

    fun playNext(): PlayNext {
        return PlayNext(musicController, logger)
    }

    fun playPrev(): PlayPrevious {
        return PlayPrevious(musicController, logger)
    }

    fun playPause(): PlayPause {
        return PlayPause(musicController, logger)
    }

    fun playStop(): PlayStop {
        return PlayStop(musicController, logger)
    }

    fun playForward(): PlayForward {
        return PlayForward(musicController, PlayForward.DEFAULT_STEP_MILLIS, logger)
    }

    fun playBackward(): PlayBackward {
        return PlayBackward(musicController, PlayBackward.DEFAULT_STEP_MILLIS, logger)
    }

    fun setRepeat(mode: RepeatMode, count: Int): RepeatControl {
        return RepeatControl(musicController, mode, count, logger)
    }

    fun playRewind(playlistPosition: Int, elapsed: Float): PlayRewind {
        return PlayRewind(musicController, playlistPosition, elapsed, logger)
    }
}