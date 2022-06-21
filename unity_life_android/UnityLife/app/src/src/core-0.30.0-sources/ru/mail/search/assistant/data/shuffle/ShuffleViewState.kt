package ru.mail.search.assistant.data.shuffle

sealed class ShuffleViewState {

    data class Show(val enabled: Boolean, val available: Boolean) : ShuffleViewState()
    object Loading : ShuffleViewState()
}