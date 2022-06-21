package ru.mail.search.assistant.services.deviceinfo

/**
 * Created by kirillf on 10/13/16.
 */

interface DeviceInfoProvider {
    val deviceId: String
    val capabilities: Map<String, Boolean>
    val dialogMode: String?
}
