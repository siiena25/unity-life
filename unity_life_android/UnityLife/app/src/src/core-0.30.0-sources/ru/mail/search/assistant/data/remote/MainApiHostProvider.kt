package ru.mail.search.assistant.data.remote

import ru.mail.search.assistant.common.data.remote.HostProvider
import ru.mail.search.assistant.common.util.ResourceManager
import ru.mail.search.assistant.data.DeveloperConfig

class MainApiHostProvider(
    private val developerConfig: DeveloperConfig,
    private val resourceManager: ResourceManager
) : HostProvider {

    override val hostUrl: String
        get() = resourceManager.getString(developerConfig.getApiHost().urlRes)
}