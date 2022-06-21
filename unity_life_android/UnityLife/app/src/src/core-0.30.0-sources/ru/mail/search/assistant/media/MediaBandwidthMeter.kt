package ru.mail.search.assistant.media

import ru.mail.search.assistant.AssistantMediaTransferListener

class MediaBandwidthMeter internal constructor(private val bandwidthMeter: AssistantBandwidthMeter) {

    fun setTransferListener(externalListener: AssistantMediaTransferListener?) {
        bandwidthMeter.setTransferListener(externalListener)
    }

    fun removeTransferListener() {
        bandwidthMeter.removeTransferListener()
    }
}