package ru.mail.search.assistant.interactor

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.session.KwsStatusInteractor

class KwsStatusRepository(private val logger: Logger?) : KwsStatusInteractor, KwsStatusAdapter {

    private companion object {

        private const val TAG = "KwsStatusAdapter"
    }

    private val inclusionAdviceChannel = ConflatedBroadcastChannel(true)

    override fun observeKwsInclusionAdvice(): Flow<Boolean> {
        return inclusionAdviceChannel.asFlow()
    }

    override fun pauseKws() {
        logger?.d(TAG, "kws pause request")
        inclusionAdviceChannel.offer(false)
    }

    override fun resumeKws() {
        logger?.d(TAG, "kws resume request")
        inclusionAdviceChannel.offer(true)
    }
}