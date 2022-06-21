package ru.mail.search.assistant.data.rtlog

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import ru.mail.search.assistant.api.statistics.rtlog.RtLogRepository

class RtLogDeviceTOSEvent(private val repository: RtLogRepository) {

    suspend fun sendEvent(eventCode: Int, deviceId: String, accepted: Boolean?) {
        val params = mutableMapOf<String, JsonElement>().apply {
            put("device_id", JsonPrimitive(deviceId))
            if (accepted != null) {
                val jsonNotAccepted = JsonPrimitive(0)
                val jsonAccepted = JsonPrimitive(1)
                put("old_accept_adv_mail", if (accepted) jsonNotAccepted else jsonAccepted)
                put("new_accept_adv_mail", if (accepted) jsonAccepted else jsonNotAccepted)
            }
        }
        repository.send(eventCode, properties = params)
    }
}