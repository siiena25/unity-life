package ru.mail.search.assistant.data.remote.parser

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.parsingError
import ru.mail.search.assistant.common.util.getFloat
import ru.mail.search.assistant.common.util.getInt
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.entities.ControlType
import ru.mail.search.assistant.entities.ServerCommand

internal class ControlParser {

    companion object {
        const val CONTROL_TYPE_RESUME = "resume_control"
        const val CONTROL_TYPE_PAUSE = "pause_control"
        const val CONTROL_TYPE_VOLUME = "volume_control"
        const val CONTROL_TYPE_NEXT = "next_control"
        const val CONTROL_TYPE_PREV = "previous_control"
        const val CONTROL_TYPE_FORWARD = "forward_control"
        const val CONTROL_TYPE_BACKWARD = "backward_control"
        const val CONTROL_TYPE_REPEAT = "repeat_control"
        const val CONTROL_TYPE_REPEAT_PLAYLIST = "repeat_playlist_control"
        const val CONTROL_TYPE_REWIND = "rewind_control"
        const val CONTROL_TYPE_STOP = "stop_control"
    }

    fun parse(commandJson: JsonObject, controlType: ControlType): ServerCommand.Controls? {
        return when (commandJson.getString(name = "type")) {
            CONTROL_TYPE_RESUME -> ServerCommand.Controls.PlaybackResume(controlType)
            CONTROL_TYPE_PAUSE -> ServerCommand.Controls.PlaybackPause(controlType)
            CONTROL_TYPE_VOLUME -> commandJson.mapToVolume(controlType)
            CONTROL_TYPE_NEXT -> ServerCommand.Controls.PlaybackNext(controlType)
            CONTROL_TYPE_PREV -> ServerCommand.Controls.PlaybackPrev(controlType)
            CONTROL_TYPE_FORWARD -> ServerCommand.Controls.PlaybackForward(controlType)
            CONTROL_TYPE_BACKWARD -> ServerCommand.Controls.PlaybackBackward(controlType)
            CONTROL_TYPE_STOP -> ServerCommand.Controls.PlaybackStop(controlType)
            CONTROL_TYPE_REPEAT -> commandJson.mapToRepeat(controlType, repeatType = 1)
            CONTROL_TYPE_REPEAT_PLAYLIST -> commandJson.mapToRepeat(controlType, repeatType = 2)
            CONTROL_TYPE_REWIND -> commandJson.mapToRewind(controlType)
            else -> null
        }
    }

    private fun JsonObject.mapToVolume(controlType: ControlType): ServerCommand.Controls.PlaybackVolume {
        val volume = getInt("level") ?: parsingError("volume: missing level")
        return ServerCommand.Controls.PlaybackVolume(controlType, volume)
    }

    private fun JsonObject.mapToRepeat(
        controlType: ControlType,
        repeatType: Int,
    ): ServerCommand.Controls.PlaybackRepeat {
        val count = getInt("count") ?: parsingError("repeat $repeatType: missing count")
        return ServerCommand.Controls.PlaybackRepeat(controlType, repeatType, count)
    }

    private fun JsonObject.mapToRewind(controlType: ControlType): ServerCommand.Controls.PlaybackRewind {
        val playlistPosition = getInt("position")
            ?: parsingError("rewind_control: missing position")
        val elapsed = getFloat("elapsed")
            ?: parsingError("rewind_control: missing elapsed")
        return ServerCommand.Controls.PlaybackRewind(controlType, playlistPosition, elapsed)
    }
}
