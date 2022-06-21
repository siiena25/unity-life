package ru.mail.search.assistant.services.music.callback.cover

import ru.mail.search.assistant.services.music.callback.BaseMediaControllerCallback

internal class CoverCallback(private val manager: CoverManager) : BaseMediaControllerCallback() {

    private var isActive = false

    override fun onPrepare() {
        isActive = true
        manager.activate()
    }

    override fun onPlay() {
        if (!isActive) {
            isActive = true
            manager.activate()
        }
    }

    override fun onStop() {
        isActive = false
        manager.deactivate()
    }
}