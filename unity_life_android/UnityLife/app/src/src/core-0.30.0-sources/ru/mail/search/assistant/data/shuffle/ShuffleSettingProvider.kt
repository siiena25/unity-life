package ru.mail.search.assistant.data.shuffle

import ru.mail.search.assistant.data.shuffle.data.ShuffleRemoteDataSource

class ShuffleSettingProvider(
    private val shuffleRemoteDataSource: ShuffleRemoteDataSource
) {

    suspend fun isEnabled(): Boolean {
        return shuffleRemoteDataSource.isEnabled()
    }

    suspend fun setEnabled(enabled: Boolean) {
        return shuffleRemoteDataSource.setEnabled(enabled)
    }
}
