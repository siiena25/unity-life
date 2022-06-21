package ru.mail.search.assistant.data.news

class NewsSourcesMapper(private val choice: Int) {

    fun map(newsSource: NewsSource): NewsSourceItem {
        return NewsSourceItem(
            newsSource.id,
            newsSource.name,
            newsSource.id == choice
        )
    }
}