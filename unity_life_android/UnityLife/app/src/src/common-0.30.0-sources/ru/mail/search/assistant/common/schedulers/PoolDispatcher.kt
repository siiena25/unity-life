package ru.mail.search.assistant.common.schedulers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher

class PoolDispatcher(
    val main: MainCoroutineDispatcher,
    val work: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val unconfined: CoroutineDispatcher
)