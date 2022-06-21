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

fun getCashbackSum(actCashback: Int): CharSequence {
    return "+$actCashback ₽"
}

fun getUserBalanceText(text: String): CharSequence {
    return "$text ₽"
}

fun getTransactionSum(sum: String): CharSequence {
    return "-$sum ₽"
}

fun String.parseTags(): List<String>? {
    if (!this.contains("#")) {
        return null
    }

    val tags = mutableListOf<String>()
    val chars = this.toCharArray()

    for (i in chars.indices) {
        val character = chars[i]
        if (character == '#') {
            val tagBuilder = StringBuilder()
            for (j in i + 1 until chars.size) {
                val inChar = chars[j]
                if (inChar.isWhitespace()) {
                    break
                }
                if (inChar.isLetterOrDigit()) {
                    tagBuilder.append(inChar)
                }
            }
            tags.add(tagBuilder.toString())
        }
    }
    return tags
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

fun String.toBirthday(): String {
    df.timeZone = TimeZone.getTimeZone("GMT")

    val date = df.parse(this) ?: Date()
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return outputFormat.format(date)
}

fun String.toAgoTime(context: Context): String {
    val formattedTime: String
    dateFormatWithSec.timeZone = TimeZone.getTimeZone("GMT")
    val date = dateFormatWithSec.parse(this) ?: Date()

    val currentTimeInSec = System.currentTimeMillis() / 1000
    val start = Calendar.getInstance()
    val end = Calendar.getInstance()

    start.time = Date(currentTimeInSec)
    end.timeInMillis = date.time / 1000

    val diffTime = start.time.time - end.time.time

    val hours = diffTime / (60 * 60)
    val minutes = diffTime / 60
    val days = diffTime / (60 * 60 * 24)
    val weeks = diffTime / (60 * 60 * 24 * 7)

    formattedTime = when {
        weeks > 0 -> {
            String.format("%d %s", weeks, context.getString(R.string.short_week))
        }
        days > 0 -> {
            String.format("%d %s", days, context.getString(R.string.hint_days))
        }
        hours > 0 -> {
            String.format("%d %s", hours, context.getString(R.string.short_hour))
        }
        else -> {
            String.format("%d %s", minutes, context.getString(R.string.short_minute))
        }
    }

    return formattedTime
}