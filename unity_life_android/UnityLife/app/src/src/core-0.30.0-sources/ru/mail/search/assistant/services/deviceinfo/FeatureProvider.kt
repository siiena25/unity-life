package ru.mail.search.assistant.services.deviceinfo

interface FeatureProvider {

    val isChildrenModeAvailable: Boolean get() = false
    val isRtLogDeviceChunksEnabled: Boolean get() = false
}