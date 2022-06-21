package ru.mail.search.assistant.entities.message.mailru

data class MailBox(
    val header: String?,
    val messageCount: Int,
    val userEmail: String?,
    val senders: List<Sender>,
    val moreSenders: MailBoxMoreSenders?
)