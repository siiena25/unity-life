package ru.mail.search.assistant.util.analytics

import android.os.SystemClock
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class LoggingAnalytics(private val logger: Logger) : Analytics() {

    companion object {

        private const val TAG = "ElAnalytics"
    }

    override fun log(event: AnalyticsEvent) {
        val message = buildString {
            append(event.name)
            val params = getParamsAsText(event)
            if (params.isNotEmpty()) {
                append(": { ")
                append(params)
                append(" }")
            }
        }
        logger.d(TAG, message)
    }

    override fun getCurrentTime(): Long {
        return SystemClock.elapsedRealtime()
    }

    private fun getParamsAsText(event: AnalyticsEvent): String {
        return buildString {
            event.stringParams.forEach { entry -> appendEntry(entry) }
            event.intParams.forEach { entry -> appendEntry(entry) }
            event.longParams.forEach { entry -> appendEntry(entry) }
        }
    }

    private fun StringBuilder.appendEntry(entry: Map.Entry<String, Any>) {
        if (isNotEmpty()) append(", ")
        append("${entry.key}: ${entry.value}")
    }
}