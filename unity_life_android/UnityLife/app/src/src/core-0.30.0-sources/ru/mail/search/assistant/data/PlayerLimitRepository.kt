package ru.mail.search.assistant.data

import ru.mail.search.assistant.data.local.PlayerLimitDataSource
import ru.mail.search.assistant.data.local.auth.AssistantCipherAdapter
import ru.mail.search.assistant.data.local.auth.RawDataFallback
import ru.mail.search.assistant.entities.PlayerLimit

class PlayerLimitRepository(
    private val dataSource: PlayerLimitDataSource,
    private val cipher: AssistantCipherAdapter,
    private val rawDataFallback: RawDataFallback
) {

    var devMenuLimit
        get() = dataSource.devMenuLimit
        set(value) {
            dataSource.devMenuLimit = value
        }

    suspend fun getLimit(): PlayerLimit? {
        val timeSpent = dataSource.timeSpent ?: return null
        val refreshTime = dataSource.refreshTime ?: return null
        val data = listOf(timeSpent, refreshTime)
        return rawDataFallback.onDecrypt(data) { encrypted ->
            cipher.decryptUserData(encrypted)
        }.let { decrypted ->
            PlayerLimit(
                timeSpent = decrypted[0].toLong(),
                refreshTime = decrypted[1].toLong()
            )
        }
    }

    suspend fun saveLimit(limit: PlayerLimit) {
        val data = listOf(
            limit.timeSpent.toString(),
            limit.refreshTime.toString()
        )
        rawDataFallback.onEncrypt(data, "player limit") { decrypted ->
            cipher.encryptUserData(decrypted)
        }.let { encrypted ->
            dataSource.timeSpent = encrypted[0]
            dataSource.refreshTime = encrypted[1]
        }
    }
}