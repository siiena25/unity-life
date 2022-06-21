package ru.mail.search.assistant.interactor

import java.util.*
import kotlin.collections.ArrayList

class UnixtimeToAlarm : (Long) -> AlarmSetup {
    override fun invoke(timestamp: Long): AlarmSetup {
        val calendar = Calendar.getInstance().apply { this.time = Date(timestamp * 1000L) }
        return AlarmSetup(
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minutes = calendar.get(Calendar.MINUTE),
            daysOfWeek = arrayListOf()) //need server info about repeat
    }
}

data class AlarmSetup(
    val hour: Int,
    val minutes: Int,
    val daysOfWeek: ArrayList<Int>,
    val skipUi: Boolean = true
)