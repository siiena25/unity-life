package ru.mail.search.assistant.entities.message.widgets

class WidgetsCurrency(
    val code: String?,
    val symbol: String,
    val rate: Float,
    val movement: Movement?
) {

    enum class Movement {

        UPWARD, DOWNWARD
    }
}