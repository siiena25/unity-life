package ru.mail.search.assistant.commands.main.skipkws

import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.entities.audio.KwsSkipInterval

internal class SoundPlayerKwsSkipController(
    kwsSkipIntervals: List<KwsSkipInterval>,
    kwsSkipController: KwsSkipController,
    poolDispatcher: PoolDispatcher
) {

    private val kwsSkipHelper = KwsSkipHelper(kwsSkipController, poolDispatcher)
    private val intervals = kwsSkipIntervals

    fun onMediaResumed(eventTime: Long, position: Long) {
        kwsSkipHelper.onMediaResumed(eventTime, position, intervals)
    }

    fun onMediaPaused() {
        kwsSkipHelper.onMediaPaused()
    }

    fun release() {
        kwsSkipHelper.release()
    }
}