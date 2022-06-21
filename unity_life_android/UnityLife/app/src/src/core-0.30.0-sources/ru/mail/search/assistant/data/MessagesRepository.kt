package ru.mail.search.assistant.data

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.mail.search.assistant.data.local.MessageUuidProvider
import ru.mail.search.assistant.data.local.messages.MessagesStorage
import ru.mail.search.assistant.entities.SuggestsEntity
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData
import java.util.*

class MessagesRepository(
    private val storage: MessagesStorage,
    private val messageUuidProvider: MessageUuidProvider
) {

    private val messagesChangesChannel = BroadcastChannel<MessageOps>(20)
    private val suggestsChannel = ConflatedBroadcastChannel<SuggestsEntity?>(null)
    private val loadingMessageChannel = BroadcastChannel<LoadingMessage>(1)

    @Volatile
    private var welcomeDialogMessages = emptyList<DialogMessage>()

    fun observeSuggests(): Flow<SuggestsEntity?> = suggestsChannel.asFlow()

    fun observeLoading(): Flow<LoadingMessage> =
        loadingMessageChannel.asFlow().distinctUntilChanged()

    fun setSuggests(suggests: SuggestsEntity) {
        suggestsChannel.offer(suggests)
    }

    fun clearSuggests() {
        suggestsChannel.offer(null)
    }

    fun addWelcomeMessage(data: MessageData) {
        synchronized(welcomeDialogMessages) {
            val message = createMessage("", data)
            welcomeDialogMessages = welcomeDialogMessages + message
            messagesChangesChannel.offer(MessageOps.Add(message))
        }
    }

    fun setWelcomeMessageIfAbsent(data: MessageData) {
        synchronized(welcomeDialogMessages) {
            if (welcomeDialogMessages.isEmpty()) {
                val message = createMessage("", data)
                welcomeDialogMessages = listOf(message)
                messagesChangesChannel.offer(MessageOps.Add(message))
            }
        }
    }

    fun offerLoadingMessage(msg: LoadingMessage) {
        loadingMessageChannel.offer(msg)
    }

    fun getMessage(messageId: Long): DialogMessage? {
        return storage.getMessage(messageId)
    }

    fun addMessage(phraseId: String, data: MessageData): DialogMessage {
        return putMessage(createMessage(phraseId, data))
    }

    fun clearDialogData() {
        synchronized(welcomeDialogMessages) {
            welcomeDialogMessages = emptyList()
        }
        storage.clearDialogData()
        messagesChangesChannel.offer(MessageOps.Clear)
        clearSuggests()
    }

    fun removeAllMessagesExceptLastN(keptMessagesCount: Int) {
        require(keptMessagesCount >= 0)
        storage.removeAllMessagesExceptLastN(keptMessagesCount)
    }

    fun observeMessagesChanges(): ReceiveChannel<MessageOps> {
        return messagesChangesChannel.openSubscription()
    }

    fun getLastMessages(count: Int): List<DialogMessage> {
        return storage.getLastMessages(count)
            .let { messages ->
                welcomeDialogMessages
                    .takeIf { it.isNotEmpty() }
                    ?.let { welcomeScreen -> insertWelcomeScreen(welcomeScreen, messages, count) }
                    ?: messages
            }
    }

    fun getLastMessagesFromId(id: Long, count: Int): List<DialogMessage> {
        return storage.getLastMessagesFromId(id, count)
            .let { messages ->
                welcomeDialogMessages
                    .takeIf { messageList -> messageList.isNotEmpty() }
                    ?.takeIf { welcomeScreen -> welcomeScreen.first().id < id }
                    ?.let { welcomeScreen -> insertWelcomeScreen(welcomeScreen, messages, count) }
                    ?: messages
            }
    }

    fun getNewMessageUuid(): UUID {
        return messageUuidProvider.get()
    }

    fun findLast(predicate: (DialogMessage) -> Boolean): DialogMessage? {
        return storage.findLast(predicate)
    }

    private fun createMessage(phraseId: String, data: MessageData): DialogMessage {
        val uuid = (data as? MessageData.OutgoingData)?.uuid ?: getNewMessageUuid()
        val timestamp = uuid.timestamp()
        return DialogMessage(timestamp, phraseId, Date(timestamp), data)
    }

    private fun putMessage(message: DialogMessage): DialogMessage {
        return storage.addMessage(message)
            .also { messagesChangesChannel.offer(MessageOps.Add(message)) }
    }

    private fun insertWelcomeScreen(
        welcomeScreen: List<DialogMessage>,
        messages: List<DialogMessage>,
        limit: Int
    ): List<DialogMessage> {
        if (messages.isEmpty()) return welcomeScreen
        val resultList = messages.toMutableList()
        welcomeScreen.forEach { welcomeMessage ->
            if (resultList.size < limit || messages.first().id < welcomeMessage.id) {
                val index =
                    messages.indexOfFirst { message -> message.id > welcomeMessage.id }
                if (index < 0) {
                    resultList.add(welcomeMessage)
                } else {
                    resultList.add(index, welcomeMessage)
                }
                if (resultList.size > limit) {
                    resultList.removeAt(0)
                }
            }
        }
        return resultList
    }
}