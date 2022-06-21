package ru.mail.search.assistant.data.device

import ru.mail.search.assistant.StartIntentController

internal class AndroidDeviceAction(
    private val startIntentController: StartIntentController
) : PlatformAction {

    override fun doAction(actionType: String, options: Map<String, Payload<out Any>>) {
        startIntentController.doAction(actionType, options)
    }
}