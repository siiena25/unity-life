package ru.mail.search.assistant

class EmptyModificationsProvider : ModificationsProvider {

    override val modifications: List<Modification> get() = emptyList()
}