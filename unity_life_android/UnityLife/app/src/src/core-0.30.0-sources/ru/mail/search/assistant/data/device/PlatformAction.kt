package ru.mail.search.assistant.data.device

import android.content.Intent

interface PlatformAction {
    fun doAction(actionType: String, options: Map<String, Payload<out Any>>)
}

class IntentAction(private val actionType: String) : (Map<String, Payload<out Any>>) -> Intent {
    override fun invoke(map: Map<String, Payload<out Any>>): Intent {
        val intent = Intent(actionType)
        map.entries.forEach { entry ->
            when (val v = entry.value.value) {
                is String -> intent.putExtra(entry.key, v)
                is Int -> intent.putExtra(entry.key, v)
                is Long -> intent.putExtra(entry.key, v)
                is Boolean -> intent.putExtra(entry.key, v)
                is ArrayList<*> -> intent.putExtra(entry.key, v)
            }
        }
        return intent
    }
}

data class Payload<T>(val value: T)