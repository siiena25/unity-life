package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.common.data.exception.parsingError
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.message.AppRef

internal class AppRefDto(
    @SerializedName("app") val app: String?,
    @SerializedName("market") val market: String?,
    @SerializedName("name") val name: String?
) : DataEntity<AppRef> {
    override fun toDomain(): AppRef {
        return AppRef(
            app = app ?: parsingError("missing reference app"),
            market = market ?: parsingError("missing reference market"),
            name = name ?: parsingError("missing reference name")
        )
    }
}