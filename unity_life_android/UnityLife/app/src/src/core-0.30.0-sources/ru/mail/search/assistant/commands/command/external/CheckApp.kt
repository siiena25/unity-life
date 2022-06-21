package ru.mail.search.assistant.commands.command.external

import ru.mail.search.assistant.commands.command.CancelableExecutableCommand
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.remote.dto.external.Intents
import ru.mail.search.assistant.services.deviceinfo.PackageManagerChecker
import ru.mail.search.assistant.util.Tag

internal class CheckApp(
    private val responseYes: String,
    private val responseNo: String,
    private val intents: List<Intents>,
    private val packageManagerChecker: PackageManagerChecker,
    private val publicCommandsFactory: PublicCommandsFactory,
    private val logger: Logger?
) : CancelableExecutableCommand<Unit>(logger = logger) {

    override val commandName: String = "CheckApp"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        val resolvedIntent = intents.firstOrNull { packageManagerChecker.checkServerIntent(it, true) }
        if (isCancelled) return
        runCatching {
            if (resolvedIntent != null) {
                context.sync(
                    publicCommandsFactory.sendEvent(
                        event = responseYes,
                        params = resolvedIntent.params?.appPackage?.let { mapOf("app_id" to it) }
                            ?: emptyMap()
                    )
                ).await()
            } else {
                context.sync(publicCommandsFactory.sendEvent(responseNo)).await()
            }
        }
    }
}