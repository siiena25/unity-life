package ru.mail.search.assistant.commands.command.userinput

import com.google.gson.JsonObject
import ru.mail.search.assistant.commands.CommandsPhraseAdapter
import ru.mail.search.assistant.commands.command.CancelableContextCommand
import ru.mail.search.assistant.commands.command.message.AddMessage
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.event.AssistantEvent
import ru.mail.search.assistant.util.Tag

internal class SendStartAppEvent(
    private val isResultIgnored: Boolean,
    private val isStartAppListenEnabled: Boolean?,
    private val phraseAdapter: CommandsPhraseAdapter,
    private val publicCommandsFactory: PublicCommandsFactory,
    private val logger: Logger?
) : CancelableContextCommand<PhraseResult>(logger) {

    override suspend fun executeInContext(context: ExecutionContext): PhraseResult {
        val phraseString = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing StartAppEvent $phraseString")
        val result = phraseAdapter.sendEvent(
            context = context,
            event = AssistantEvent.START_APP,
            clientData = createClientData(),
        )
        return mapResult(result)
    }

    private fun createClientData(): String? {
        return if (isStartAppListenEnabled != null) {
            val json = JsonObject()
            json.addProperty("startapp_listen", isStartAppListenEnabled)
            json.toString()
        } else {
            null
        }
    }

    override suspend fun notify(context: NotificationContext) {
        if (context.cause !is CommandNotification.UserInput.Initiated) {
            super.notify(context)
        }
    }

    private fun mapResult(result: PhraseResult): PhraseResult {
        return if (isResultIgnored) {
            result.copy(commands = emptyList())
        } else {
            val commands = result.commands.map { data ->
                if (data.command is AddMessage) {
                    val command = publicCommandsFactory.showWelcomeMessage(data.command.data)
                    data.copy(command = command)
                } else {
                    data
                }
            }
            result.copy(commands = commands)
        }
    }
}