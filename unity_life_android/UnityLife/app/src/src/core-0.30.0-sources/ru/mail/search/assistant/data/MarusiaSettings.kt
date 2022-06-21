package ru.mail.search.assistant.data

import ru.mail.search.assistant.data.news.NewsSourceItem
import ru.mail.search.assistant.data.news.NewsSourcesRepository
import ru.mail.search.assistant.data.shuffle.data.ShuffleRemoteDataSource

class MarusiaSettings(
    private val newsSourcesRepository: NewsSourcesRepository,
    private val shuffleRemoteDataSource: ShuffleRemoteDataSource
) {

    suspend fun isShuffleEnabled(): Boolean {
        return shuffleRemoteDataSource.isEnabled()
    }

    suspend fun setShuffleEnabled(value: Boolean) {
        shuffleRemoteDataSource.setEnabled(value)
    }

    suspend fun getNewsSources(): List<NewsSourceItem> {
        return newsSourcesRepository.getNewsSources()
    }

    fun getStoredNewsSource(): NewsSourceItem {
        return newsSourcesRepository.getStoredNewsSource()
    }

    suspend fun setCurrentNewsSource(newSourceId: Int): List<NewsSourceItem> {
        return newsSourcesRepository.setCurrentNewsSource(newSourceId)
    }
}