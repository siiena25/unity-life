package ru.mail.search.assistant.common.ui

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class ViewModelScope(
    protected val poolDispatcher: PoolDispatcher
) : ViewModel(), CoroutineScope {

    final override val coroutineContext: CoroutineContext get() = viewModelContext + poolDispatcher.main
    private val viewModelContext: Job = SupervisorJob()

    @CallSuper
    override fun onCleared() {
        cancel()
    }

    protected fun createChildContext(): Job {
        return SupervisorJob(viewModelContext)
    }

    protected fun CoroutineScope.launchImmediate(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(context = context + poolDispatcher.main.immediate, block = block)
    }

    protected fun single(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) {
        context[Job.Key]?.let { job ->
            if (job.isActive && job.children.count() == 0) {
                launch(context = context + poolDispatcher.main.immediate, block = block)
            }
        }
    }

    protected fun singleWithDebounce(
        context: Job,
        time: Long,
        block: suspend CoroutineScope.() -> Unit
    ) {
        single(context) {
            val operation = launchImmediate { block() }
            val debounce = launchImmediate { delay(time) }
            joinAll(operation, debounce)
        }
    }
}
