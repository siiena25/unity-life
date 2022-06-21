package ru.mail.search.assistant.session

import kotlinx.coroutines.flow.Flow
import ru.mail.search.assistant.util.UnstableAssistantApi

@UnstableAssistantApi
interface KwsStatusInteractor {

    /**
     * Hint from assistant to enable or disable kws
     */
    fun observeKwsInclusionAdvice(): Flow<Boolean>
}