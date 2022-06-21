package ru.mail.search.assistant.commands.command

import ru.mail.search.assistant.api.phrase.ActivationType
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.commands.processor.model.InteractionMethod
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.AssistantContextRepository
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.entities.InputMethod
import ru.mail.search.assistant.util.Tag

internal class WaitForUserInput(
    private val assistantContextRepository: AssistantContextRepository,
    private val clientStateRepository: ClientStateRepository,
    private val minWaitingTime: Int?,
    private val muteActivationSound: Boolean,
    private val logger: Logger?,
    private val callbackData: String?
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        val phraseInfo = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing listen command $phraseInfo")
        if (context.refuseDialogInteraction()) return
        val requestMethod = when (context.phrase.interactionMethod) {
            InteractionMethod.TEXT -> InputMethod.Text
            InteractionMethod.VOICE -> InputMethod.Voice(
                startedManually = true,
                minWaitingTime = minWaitingTime,
                muteActivationSound = muteActivationSound,
                activationType = ActivationType.LISTEN_COMMAND,
                callbackData = callbackData
            )
        }
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