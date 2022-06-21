package ru.mail.search.assistant.commands

import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent

internal class CommandsStatisticAdapterImpl(
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent
) : CommandsStatisticAdapter {

    override fun initializePhrase(context: ExecutionContext) {
        rtLogDevicePhraseExtraDataEvent.createPhrase(context.phrase.requestId)
    }

    override fun finishPhrase(context: ExecutionContext) {
        rtLogDevicePhraseExtraDataEvent.onPhraseFinished(context.phrase.requestId)
    }
}