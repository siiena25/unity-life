package ru.mail.search.assistant.data.news

class NewsSourcesRepository(
    private val localDataSource: NewsSourcesLocalDataSource,
    private val remoteDataSource: NewsSourcesRemoteDataSource
) {

    suspend fun getNewsSources(): List<NewsSourceItem> {
        val newsSources = NewsSources(getCurrentNewsSource(), getNewsSourcesList())
        val mapper = NewsSourcesMapper(newsSources.choice)
        return newsSources.sources.map {
            mapper.map(it).also { newsSources ->
                if (newsSources.selected) {
                    setStoredNewsSource(newsSources)
                }
            }
        }
    }

    suspend fun setCurrentNewsSource(id: Int): List<NewsSourceItem> {
        remoteDataSource.putCurrentNewsSource(id).also {
            return getNewsSources()
        }
    }

    fun getStoredNewsSource(): NewsSourceItem {
        return localDataSource.currentSource
    }

    fun setStoredNewsSource(item: NewsSourceItem) {
        localDataSource.currentSource = item
    }

    private suspend fun getNewsSourcesList(): List<NewsSource> {
        return remoteDataSource.getNewsSources()
    }

    private suspend fun getCurrentNewsSource(): Int {
        return remoteDataSource.getCurrentNewsSource()
    }
}