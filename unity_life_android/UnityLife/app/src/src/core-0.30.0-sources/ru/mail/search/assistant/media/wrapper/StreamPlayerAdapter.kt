package ru.mail.search.assistant.media.wrapper

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.mail.search.assistant.commands.command.media.SoundPlayer
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.data.StreamsRepository
import ru.mail.search.assistant.media.MediaPlayer

internal class StreamPlayerAdapter(
    private val streamsRepository: StreamsRepository
) : PlayerAdapter<StreamPlayerAdapter.Params> {

    override val isPlaying: Boolean get() = streamsRepository.isPlaying()

    override fun play(params: Params): Flow<SoundPlayer.State> {
        return callbackFlow {
            streamsRepository.playStream(params.request, params.format) { state ->
                try {
                    offer(state)
                } catch (error: CancellationException) {
                    // CancellationException is regular case, when flow already closed,
                    // but we must handle this, because callback triggered on main thread
                }
            }
            awaitClose { streamsRepository.stopStream() }
        }
            .conflate()
            .distinctUntilChanged()
    }

    override fun stop() {
        streamsRepository.stopStream()
    }

    data class Params(
        val request: HttpRequest,
        val format: MediaPlayer.Format
    )
}