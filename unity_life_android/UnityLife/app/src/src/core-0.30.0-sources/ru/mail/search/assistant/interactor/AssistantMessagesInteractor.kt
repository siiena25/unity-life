package ru.mail.search.assistant.interactor

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import ru.mail.search.assistant.AssistantMusicController
import ru.mail.search.assistant.data.LoadingMessage
import ru.mail.search.assistant.data.MessageOps
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.SuggestsEntity
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData
import java.util.*

class AssistantMessagesInteractor(
    private val messagesRepository: MessagesRepository,
    private val musicController: AssistantMusicController
) {

    fun showDialogMessage(text: String) {
        messagesRepository.addMessage("", MessageData.IncomingText(text))
    }

    fun setWelcomeMessageIfAbsent(data: MessageData) {
        messagesRepository.setWelcomeMessageIfAbsent(data)
    }

    fun clearDialogData() {
        musicController.stop()
        messagesRepository.clearDialogData()
    }

    fun getMessage(messageId: Long): DialogMessage? {
        return messagesRepository.getMessage(messageId)
    }

    fun clearSuggests() {
        messagesRepository.clearSuggests()
    }

    fun observeSuggests(): Flow<SuggestsEntity?> {
        return messagesRepository.observeSuggests()
    }

    fun addMessage(phraseId: String, data: MessageData): DialogMessage {
        return messagesRepository.addMessage(phraseId, data)
    }

    fun observeMessagesChanges(): ReceiveChannel<MessageOps> {
        return messagesRepository.observeMessagesChanges()
    }

    fun getLastMessages(count: Int): List<DialogMessage> {
        return messagesRepository.getLastMessages(count)
    }

    fun getLastMessagesFromId(id: Long, count: Int): List<DialogMessage> {
        return messagesRepository.getLastMessagesFromId(id, count)
    }

    fun observeLoading(): Flow<LoadingMessage> {
        return messagesRepository.observeLoading()
    }
}