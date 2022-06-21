package ru.mail.search.assistant.services.deviceinfo

import ru.mail.search.assistant.Assistant

class SuggestedCapabilitiesProvider : CapabilitiesProvider {

    override val capabilities: Map<String, Boolean>
        get() = Assistant.SUGGESTED_CAPABILITIES
}