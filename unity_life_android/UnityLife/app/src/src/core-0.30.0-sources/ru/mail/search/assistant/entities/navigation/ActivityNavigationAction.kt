package ru.mail.search.assistant.entities.navigation

sealed interface ActivityNavigationAction {

    data class OpenUrl(val url: String) : ActivityNavigationAction

    data class OpenPhoneCall(val number: String) : ActivityNavigationAction

    data class OpenSms(val number: String, val text: String) : ActivityNavigationAction
}