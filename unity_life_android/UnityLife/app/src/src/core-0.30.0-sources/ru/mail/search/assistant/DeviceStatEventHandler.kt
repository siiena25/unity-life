package ru.mail.search.assistant

import ru.mail.search.assistant.core.RtLog
import ru.mail.search.assistant.data.DeviceStatEventRepository
import ru.mail.search.assistant.entities.DeviceStatEvent

class DeviceStatEventHandler(
    val rtLog: RtLog,
    private val deviceStatEventRepository: DeviceStatEventRepository
) {

    fun sendEvent(statEvent: DeviceStatEvent) {
        deviceStatEventRepository.sendEvent(statEvent)
    }
}