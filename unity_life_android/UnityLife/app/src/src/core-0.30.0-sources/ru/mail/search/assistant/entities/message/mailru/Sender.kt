package ru.mail.search.assistant.entities.message.mailru

data class Sender(
    val type: Type,
    val avatar: String?,
    val email: String?,
    val name: String?,
    val messageCount: Int,
    val payload: String?,
    val event: String?
) {

    enum class Type {

        SENDER, NEWSLETTER
    }
}