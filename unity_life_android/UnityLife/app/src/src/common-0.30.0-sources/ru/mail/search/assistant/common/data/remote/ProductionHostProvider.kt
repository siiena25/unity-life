package ru.mail.search.assistant.common.data.remote

import ru.mail.search.assistant.common.R
import ru.mail.search.assistant.common.util.ResourceManager

class ProductionHostProvider(private val resourceManager: ResourceManager) : HostProvider {

    override val hostUrl: String
        get() = resourceManager.getString(R.string.myAssistant_url_prod)
}