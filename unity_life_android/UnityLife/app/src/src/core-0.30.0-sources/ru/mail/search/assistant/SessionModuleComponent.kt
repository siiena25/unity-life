package ru.mail.search.assistant

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.dependencies.session.SessionScopeModule

internal class SessionModuleComponent(
    private val core: AssistantCore,
    private val modificationsProvider: ModificationsProvider,
    private val intentHandlerProvider: AssistantIntentHandlerProvider?,
    private val logger: Logger?
) : LazyComponent<SessionScopeModule>() {

    private companion object {

        private const val TAG = "SessionModuleComponent"
    }

    override fun create(): SessionScopeModule {
        logger?.d(TAG, "On create assistant session component")
        return core.module.createSessionScope(modificationsProvider, intentHandlerProvider).apply {
            sessionMusicController.connect()
            commandsMusicController.connect()
        }
    }

    override fun onClose(component: SessionScopeModule) {
        logger?.d(TAG, "On close assistant session component")
        GlobalScope.launch {
            component.commandsAdapter.cancel()
            component.commandsMusicController.disconnect()
            component.sessionMusicController.disconnect()
        }
    }
}