package ru.mail.search.assistant.entities.message

import ru.mail.search.assistant.api.suggests.SkillIcons
import ru.mail.search.assistant.api.suggests.Suggest

data class EventItemDescription(
    val title: String,
    val subtitle: String?,
    val suggest: Suggest?,
    val icon: String?,
    val icons: SkillIcons
)