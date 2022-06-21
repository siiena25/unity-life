package ru.mail.search.assistant.entities.message.mailru

data class Mail(
    val type: Type,
    val avatar: String?,
    val senderName: String?,
    val subject: String?,
    val content: String?,
    val hasAttach: Boolean,
    val event: String?,
    val payload: String?,
    val date: Long
) {

    enum class Type {

        MAIL, NEWSLETTER
    }
}
