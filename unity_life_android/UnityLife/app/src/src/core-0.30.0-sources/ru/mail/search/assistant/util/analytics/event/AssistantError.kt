package ru.mail.search.assistant.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class AssistantError(module: Module, cause: String) : AnalyticsEvent("assistant_error") {

    init {
        stringParams["module"] = module.id
        stringParams["cause"] = cause
    }

    enum class Module(val id: String) {

        COMMON("common"),
        COMMANDS_PARSING("commands_parsing"),
        MIGRATION("data_migration"),
        CIPHER("cipher"),
        MEDIA("media")
    }
}