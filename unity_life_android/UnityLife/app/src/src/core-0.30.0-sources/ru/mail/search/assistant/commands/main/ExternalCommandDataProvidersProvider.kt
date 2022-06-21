package ru.mail.search.assistant.commands.main

import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.external.ExternalCommandDataProvider
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.dependencies.session.LazyModificationsProvider
import ru.mail.search.assistant.interactor.PhraseInteractor

internal class ExternalCommandDataProvidersProvider(
    private val lazyModificationsProvider: LazyModificationsProvider
) {

    fun getProviders(
        commandsFactory: PublicCommandsFactory,
        commandsAdapter: CommandsAdapter,
        phraseInteractor: PhraseInteractor
    ): List<ExternalCommandDataProvider> {
        return lazyModificationsProvider.modifications.mapNotNull { modification ->
            modification.getCommandDataProvider(
                commandsFactory,
                commandsAdapter,
                phraseInteractor
            )
        }
    }
}