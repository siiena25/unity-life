package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes.NEWS_CARD
import ru.mail.search.assistant.data.local.messages.converter.payload.NewsCardPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.NewsPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.News

internal class NewsCardPayloadConverter : PayloadGsonConverter<MessageData.NewsCard, NewsCardPayload>() {

    override val type: String get() = NEWS_CARD

    override fun payloadToPojo(payload: String): MessageData.NewsCard {
        return fromJson<NewsCardPayload>(payload) {
            MessageData.NewsCard(
                title,
                news.map { newsItem -> News(newsItem.title, newsItem.text, newsItem.url) }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.NewsCard): String {
        return toJson(data) {
            NewsCardPayload(
                title,
                news.map { newsItem -> NewsPayload(newsItem.title, newsItem.text, newsItem.url) }
            )
        }
    }
}