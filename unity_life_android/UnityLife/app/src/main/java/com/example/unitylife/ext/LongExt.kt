package com.example.unitylife.ext

import android.content.Context
import android.os.Build
import com.example.unitylife.R
import java.text.SimpleDateFormat
import java.util.*

fun Long.toBirthDate(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(Date(this))
}

fun Long.toTimestamp(): String {
    val formatted = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        .format(this)
    return formatted.substring(0, 22) + ":" + formatted.substring(22)
}

fun Long.toISO8601(): String {
    val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    } else {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    }
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.format(Date(this))

//    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK)
//    return sdf.format(Date(this))
}

fun Long.toCommentTime(context: Context): String {
    val formattedTime: String
    val currentTimeInSec = System.currentTimeMillis() / 1000
    val start = Calendar.getInstance()
    val end = Calendar.getInstance()
    val diff = Calendar.getInstance()

    start.time = Date(currentTimeInSec)
    end.time = Date(this)

    val diffTime = end.time.time - start.time.time
    diff.time = Date(diffTime)

    formattedTime = if (diff.get(Calendar.HOUR_OF_DAY) < 0) {
        String.format("%d %s", diff.get(Calendar.MINUTE), context.getString(R.string.short_minute))
    } else if (diff.get(Calendar.HOUR_OF_DAY) > 0 && diff.get(Calendar.WEEK_OF_YEAR) < 1) {
        String.format("%d %s", diff.get(Calendar.HOUR_OF_DAY), context.getString(R.string.short_hour))
    } else {
        String.format("%d %s", diff.get(Calendar.WEEK_OF_YEAR), context.getString(R.string.short_week))
    }

    return formattedTime
}