package ru.mail.search.assistant.data.news

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClient
import ru.mail.search.assistant.common.http.assistant.setupJsonBody
import ru.mail.search.assistant.common.util.getInt
import ru.mail.search.assistant.common.util.getObject

class NewsSourcesRemoteDataSource(
    private val sessionProvider: SessionCredentialsProvider,
    private val httpClient: AssistantHttpClient
) {

    private val gson = Gson()
    private val listTypeToken = object : TypeToken<List<NewsSource>>() {}.type

    suspend fun getNewsSources(): List<NewsSource> {
        val sources = httpClient
            .getWithResult("news/sources/list", sessionProvider.getCredentials())
            .getJsonBody()
            ?.getObject("result")
            ?.get("sources")
            ?: throw ResultParsingException("Failed to parse response")
        return gson.fromJson(sources, listTypeToken)
    }

    suspend fun getCurrentNewsSource(): Int {
        return httpClient
            .getWithResult("news/sources", sessionProvider.getCredentials())
            .getJsonBody()
            ?.getObject("result")
            ?.getInt("choice")
            ?: throw ResultParsingException("Missing choice")
    }

    suspend fun putCurrentNewsSource(choice: Int) {
        httpClient.put("news/sources", sessionProvider.getCredentials()) {
            setupJsonBody {
                addProperty("choice", choice)
            }
        }
    }
}