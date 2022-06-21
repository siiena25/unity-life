package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.RecipePayload
import ru.mail.search.assistant.data.local.messages.converter.payload.RecipesCardPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.Recipe

internal class RecipesCardPayloadConverter :
    PayloadGsonConverter<MessageData.RecipesCard, RecipesCardPayload>() {

    override val type: String get() = MessageTypes.RECIPES_CARD

    override fun payloadToPojo(payload: String): MessageData.RecipesCard {
        return fromJson<RecipesCardPayload>(payload) {
            MessageData.RecipesCard(
                recipes = recipes.map { recipe ->
                    Recipe(
                        title = recipe.title,
                        text = recipe.text,
                        url = recipe.url,
                        urlShort = recipe.urlShort,
                        imageUrl = recipe.imageUrl
                    )
                },
                searchUrl = searchUrl
            )
        }
    }

    override fun pojoToPayload(data: MessageData.RecipesCard): String {
        return toJson(data) {
            RecipesCardPayload(
                recipes = recipes.map { recipe ->
                    RecipePayload(
                        title = recipe.title,
                        text = recipe.text,
                        url = recipe.url,
                        urlShort = recipe.urlShort,
                        imageUrl = recipe.imageUrl
                    )
                },
                searchUrl = searchUrl
            )
        }
    }
}