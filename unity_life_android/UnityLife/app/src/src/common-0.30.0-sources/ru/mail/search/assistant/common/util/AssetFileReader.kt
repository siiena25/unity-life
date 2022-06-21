package ru.mail.search.assistant.common.util

import android.content.Context

/**
 * Created by Grigory Fedorov on 04.09.18.
 */
class AssetFileReader(val context: Context) {
    fun read(fileName: String): ByteArray {
        val inputStream = context.assets.open(fileName)
        val data = ByteArray(inputStream.available())
        inputStream.read(data)
        return data
    }
}
