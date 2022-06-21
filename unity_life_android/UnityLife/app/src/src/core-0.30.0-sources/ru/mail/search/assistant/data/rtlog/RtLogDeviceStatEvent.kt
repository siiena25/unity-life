package ru.mail.search.assistant.data.rtlog

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mail.search.assistant.api.statistics.rtlog.RtLogRepository

class RtLogDeviceStatEvent(private val repository: RtLogRepository) {

    private companion object {

        private const val EVENT_CODE = 1011
    }

    suspend fun send(type: String, time: Long, data: Map<String, JsonElement>) {
        val statType = JsonPrimitive(type)
        val statData = JsonObject().apply {
            data.forEach { (key, value) -> add(key, value) }
        }
        val fields = mapOf(
            "stat_type" to statType,
            "stat_data" to statData
        )
        repository.send(EVENT_CODE, null, time, fields)
    }
}