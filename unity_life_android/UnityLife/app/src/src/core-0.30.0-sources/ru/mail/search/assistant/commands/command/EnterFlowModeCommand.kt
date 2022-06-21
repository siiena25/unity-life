package ru.mail.search.assistant.commands.command

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.AssistantContextRepository
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.entities.InputMethod
import ru.mail.search.assistant.util.Tag

internal class EnterFlowModeCommand(
    private val flowModeModel: String,
    private val assistantContextRepository: AssistantContextRepository,
    private val clientStateRepository: ClientStateRepository,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        val phraseInfo = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing enter flow mode command $phraseInfo")
        if (context.assistant.isSilenced) return
        val requestMethod = InputMethod.FlowMode(flowModeModel)
        clientStateRepository.onFinishUserRequest(context.phrase.requestId)
        assistantContextRepository.requestUserInput(requestMethod)
    }

    override suspend fun notify(context: NotificationContext) {
        when (context.cause) {
            CommandNotification.Silence,
            CommandNotification.Revoke,
            is CommandNotification.UserInput.Initiated -> {
                context.assistant.revoke()
            }
        }
    }
}