package ru.mail.search.assistant.entities

import ru.mail.search.assistant.api.suggests.Suggest

sealed class InternalIntent {

    object ShowFullSkillList : InternalIntent()

    data class OpenUrl(val url: String) : InternalIntent()

    data class OpenApp(val app: String, val fallbackUrl: String?) : InternalIntent()

    data class ExecuteSuggest(val suggest: Suggest) : InternalIntent()

    data class Share(val text: String) : InternalIntent()

    data class Call(val phoneNumber: String) : InternalIntent()
}