package ru.mail.search.assistant.commands.processor.model

/**
 * [contextId] - context instance identifier
 * [requestId] - local phrase identifier
 */
data class PhraseContext(
    val contextId: Int,
    val requestId: Int,
    val interactionMethod: InteractionMethod
) {

    fun toShortString(): String {
        return "[requestId=$requestId, contextId=$contextId]"
    }
}