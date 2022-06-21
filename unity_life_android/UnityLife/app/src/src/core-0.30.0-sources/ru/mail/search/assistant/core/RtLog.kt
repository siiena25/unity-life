package ru.mail.search.assistant.core

import ru.mail.search.assistant.data.rtlog.RtLogDeviceStatEvent
import ru.mail.search.assistant.data.rtlog.RtLogDeviceTOSEvent

class RtLog(val deviceStatEvent: RtLogDeviceStatEvent, val deviceTOSEvent: RtLogDeviceTOSEvent)