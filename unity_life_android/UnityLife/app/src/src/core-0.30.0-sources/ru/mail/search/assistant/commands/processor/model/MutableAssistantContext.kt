package ru.mail.search.assistant.commands.processor.model

internal class MutableAssistantContext(
    private val parentContext: AssistantContext?
) : AssistantContext {

    override val isSilenced: Boolean
        get() = _isRevoked ||
                _isSilenced ||
                parentContext?.isSilenced ?: false

    override val isRevoked: Boolean
        get() = _isRevoked ||
                parentContext?.isRevoked ?: false

    @Volatile
    private var _isSilenced: Boolean = false

    @Volatile
    private var _isRevoked: Boolean = false

    override fun silence() {
        _isSilenced = true
    }

    override fun revoke() {
        _isRevoked = true
    }
}