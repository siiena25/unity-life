package com.example.unitylife.ext

import android.content.Context
import com.example.unitylife.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val BASE_FILES = "https://files.aceplace.ru"

val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK)
val dateFormatWithSec = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK)

fun String.toUrl(): String {
    return BASE_FILES.plus(this.trim())
}

fun prepareTitle(title: String): String {
    return title.substring(0, 1).uppercase(Locale.getDefault()) + title.substring(1)
}

fun String.toTimeFromISO(): Long {
    val tz: TimeZone = TimeZone.getTimeZone("GMT")
    df.timeZone = tz

    try {
        return df.parse(this).time
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return 0
}