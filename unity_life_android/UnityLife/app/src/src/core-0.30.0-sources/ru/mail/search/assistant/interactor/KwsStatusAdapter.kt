package ru.mail.search.assistant.interactor

import androidx.annotation.AnyThread
import ru.mail.search.assistant.util.UnstableAssistantApi

@UnstableAssistantApi
interface KwsStatusAdapter {

    @AnyThread
    fun pauseKws()

    @AnyThread
    fun resumeKws()
}