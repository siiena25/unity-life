package ru.mail.search.assistant.data.local.auth

import kotlinx.coroutines.CancellationException
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

class RawDataFallback(private val logger: Logger?) {

    companion object {

        private const val RAW_DATA_POSTFIX = "[raw]"
    }

    suspend fun onEncrypt(
        data: List<String>,
        dataTag: String = "",
        encrypt: suspend (decryptedData: List<String>) -> List<String>
    ): List<String> {
        val toEncrypt = ArrayList<String>(data.size)
        val encryptedIndexes = IntArray(data.size)
        val result = ArrayList<String>(data.size)
        for (idx in data.indices) {
            result.add("")
        }
        data.forEachIndexed { index, value ->
            when {
                value.isEmpty() -> {
                    result[index] = value
                }
                else -> {
                    encryptedIndexes[toEncrypt.size] = index
                    toEncrypt.add(value)
                }
            }
        }
        if (toEncrypt.isNotEmpty()) {
            runCatching { encrypt(toEncrypt) }
                .getOrElse { error ->
                    if (error !is CancellationException) {
                        logger?.e(
                            Tag.ASSISTANT_CIPHER,
                            error,
                            "Failed to encrypt $dataTag, raw data used"
                        )
                        toEncrypt.map { value -> value + RAW_DATA_POSTFIX }
                    } else {
                        throw error
                    }
                }
                .forEachIndexed { index, value ->
                    val resultIndex = encryptedIndexes[index]
                    result[resultIndex] = value
                }
        }
        return result
    }

    suspend fun onEncrypt(
        data: String,
        dataTag: String = "",
        encrypt: suspend (decryptedData: String) -> String
    ): String {
        return if (data.isNotEmpty()) {
            runCatching { encrypt(data) }
                .getOrElse { error ->
                    if (error !is CancellationException) {
                        logger?.e(
                            Tag.ASSISTANT_CIPHER,
                            error,
                            "Failed to encrypt $dataTag, raw data used"
                        )
                        data + RAW_DATA_POSTFIX
                    } else {
                        throw error
                    }
                }
        } else {
            data
        }
    }

    suspend fun onDecrypt(
        data: List<String>,
        decrypt: suspend (encryptedData: List<String>) -> List<String>
    ): List<String> {
        val encrypted = ArrayList<String>()
        val encryptedIndexes = IntArray(data.size)
        val result = ArrayList<String>(data.size)
        repeat(data.indices.count()) { result.add("") }
        data.forEachIndexed { index, value ->
            when {
                value.isEmpty() -> {
                    result[index] = value
                }
                value.endsWith(RAW_DATA_POSTFIX) -> {
                    result[index] = value.dropLast(RAW_DATA_POSTFIX.length)
                }
                else -> {
                    encryptedIndexes[encrypted.size] = index
                    encrypted.add(value)
                }
            }
        }
        if (encrypted.isNotEmpty()) {
            decrypt(encrypted).forEachIndexed { index, value ->
                val resultIndex = encryptedIndexes[index]
                result[resultIndex] = value
            }
        }
        return result
    }

    suspend fun onDecrypt(
        data: String,
        decrypt: suspend (encryptedData: String) -> String
    ): String {
        return when {
            data.isEmpty() ->
                data
            data.endsWith(RAW_DATA_POSTFIX) ->
                data.dropLast(RAW_DATA_POSTFIX.length)
            else ->
                decrypt(data)
        }
    }
}