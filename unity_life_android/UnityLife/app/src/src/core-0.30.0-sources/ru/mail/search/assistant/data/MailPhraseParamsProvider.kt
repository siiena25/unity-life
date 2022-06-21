package ru.mail.search.assistant.data

import ru.mail.search.assistant.api.phrase.MailPhraseParams

interface MailPhraseParamsProvider {

    fun getMailPhraseParams(): MailPhraseParams?
}