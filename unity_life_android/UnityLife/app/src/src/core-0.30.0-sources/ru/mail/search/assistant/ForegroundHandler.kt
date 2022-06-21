package ru.mail.search.assistant

import ru.mail.search.assistant.interactor.PlayerLimitInteractor

class ForegroundHandler(private val playerLimitInteractor: PlayerLimitInteractor) {

    fun onAppForeground() {
        playerLimitInteractor.onAppForeground()
    }

    fun onAppBackground() {
        playerLimitInteractor.onAppBackground()
    }
}