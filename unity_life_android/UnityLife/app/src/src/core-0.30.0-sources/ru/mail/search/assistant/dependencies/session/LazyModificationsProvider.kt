package ru.mail.search.assistant.dependencies.session

import ru.mail.search.assistant.Modification
import ru.mail.search.assistant.ModificationsProvider

internal class LazyModificationsProvider(val modificationsProvider: ModificationsProvider) {

    val modifications: List<Modification> by lazy { modificationsProvider.modifications }
}