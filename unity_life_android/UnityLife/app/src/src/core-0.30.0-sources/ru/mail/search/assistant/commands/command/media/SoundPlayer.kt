package ru.mail.search.assistant.commands.command.media

import kotlinx.coroutines.flow.Flow

interface SoundPlayer {

    suspend fun play(requestId: Int): Flow<State>

    sealed class State {

        data class Playing(val eventTime: Long, val position: Long) : State()

        object Paused : State()

        object Finished : State()
    }
}