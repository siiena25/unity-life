package ru.mail.search.assistant.data.remote.parser

import ru.mail.search.assistant.dependencies.session.LazyModificationsProvider

internal class ExternalParsersProvider(private val lazyModificationsProvider: LazyModificationsProvider) {

    val externalParsers: Map<String, ExternalDataParser> by lazy { createParsers() }

    private fun createParsers(): Map<String, ExternalDataParser> {
        return lazyModificationsProvider.modifications
            .map { modification ->
                modification.getDataParsers()
            }
            .fold(emptyList<ExternalDataParser>()) { parsers1, parsers2 ->
                parsers1 + parsers2
            }
            .associateBy { parser ->
                parser.getCommandName()
            }
    }
}