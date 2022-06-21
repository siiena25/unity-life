package ru.mail.search.assistant.common.schedulers

import kotlinx.coroutines.Dispatchers

class PoolDispatcherFactory {
    fun createPoolDispatcher(): PoolDispatcher {
        return PoolDispatcher(
            main = Dispatchers.Main,
            work = Dispatchers.Default,
            io = Dispatchers.IO,
            unconfined = Dispatchers.Unconfined
        )
    }
}