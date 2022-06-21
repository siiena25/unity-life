package ru.mail.search.assistant.entities

import ru.mail.search.assistant.api.suggests.Suggest

class SuggestsEntity(
    val phraseId: String,
    val suggests: List<Suggest>
)