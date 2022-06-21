package ru.mail.search.assistant.services.music.callback.noisy

import ru.mail.search.assistant.services.music.callback.BaseMediaControllerCallback

internal class BecomingNoisyCallback(
    private val becomingNoisyReceiver: BecomingNoisyReceiver
) : BaseMediaControllerCallback() {

    override fun onPlay() {
        becomingNoisyReceiver.register()
    }

    override fun onPause() {
        becomingNoisyReceiver.unregister()
    }

    override fun onStop() {
        becomingNoisyReceiver.unregister()
    }
}