package ru.mail.search.assistant.common.util.coroutines

import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

fun CoroutineContext.requireJob(): Job = requireNotNull(this[Job.Key])
