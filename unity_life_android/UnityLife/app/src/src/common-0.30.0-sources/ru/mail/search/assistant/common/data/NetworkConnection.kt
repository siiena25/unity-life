package ru.mail.search.assistant.common.data

import kotlinx.coroutines.flow.Flow

interface NetworkConnection {

    fun observeNetworkAvailability(): Flow<Boolean>

    fun hasNetworkAvailability(): Boolean
}