package ru.mail.search.assistant.common.util.coroutines.operator

import kotlinx.coroutines.CancellationException

/**
 * Alternative for internal [kotlinx.coroutines.flow.internal.AbortFlowException]
 */
class FlowInterruptionException :
    CancellationException("Flow was aborted, no more elements needed")