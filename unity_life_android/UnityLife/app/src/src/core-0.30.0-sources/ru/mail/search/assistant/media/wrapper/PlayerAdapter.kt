package ru.mail.search.assistant.media.wrapper

import kotlinx.coroutines.flow.Flow
import ru.mail.search.assistant.commands.command.media.SoundPlayer

interface PlayerAdapter<T> {

    val isPlaying: Boolean

    fun play(params: T): Flow<SoundPlayer.State>

    fun stop()
}