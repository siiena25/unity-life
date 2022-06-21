package ru.mail.search.assistant.commands.command.external

import android.content.Intent
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.StartIntentController
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.remote.dto.external.Intents
import ru.mail.search.assistant.util.Tag

internal class OpenApp(
    private val appId: String,
    private val path: String,
    private val fallbackUrl: String,
    private val intents: List<Intents>,
    private val startIntentController: StartIntentController,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    companion object {

        private val appsIdsMap = hashMapOf(
            "facebook" to "com.facebook.katana",
            "twitter" to "com.twitter.android",
            "instagram" to "com.instagram.android",
            "vk" to "com.vkontakte.android",
            "ok" to "ru.ok.android",
            "youtube" to "com.google.android.youtube",
            "boom" to "com.uma.musicvk"
        )
    }

    override val commandName: String = "OpenApp"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        withContext(poolDispatcher.main) {
            val validServerIntent = intents.firstOrNull {
                startIntentController.packageManagerChecker.checkServerIntent(it)
            }
            if (validServerIntent != null) {
                openServerIntent(validServerIntent)
            } else {
                oldOpenAppHandle()
            }
        }
    }

    private fun oldOpenAppHandle() {
        if (path.isNotEmpty()) {
            openInBrowser()
        } else {
            val androidAppId = appsIdsMap[appId]
            if (androidAppId != null) {
                if (!startIntentController.application(androidAppId)) {
                    openInBrowser()
                }
            } else {
                openInBrowser()
            }
        }
    }

    private fun openServerIntent(intent: Intents) {
        if (intent.params?.appPackage != null) {
            if (!startIntentController.application(intent.params.appPackage)) {
                oldOpenAppHandle()
            }
        } else {
            intent.params?.action?.let {
                val androidIntent = startIntentController.packageManagerChecker.actionToIntent(
                    action = it,
                    category = intent.params.category,
                    type = intent.params.type,
                    data = intent.params.data
                )
                if (startIntentController.packageManagerChecker.checkIntent(androidIntent)) {
                    openIntent(androidIntent)
                } else {
                    oldOpenAppHandle()
                }
            } ?: oldOpenAppHandle()
        }
    }

    private fun openIntent(intent: Intent) {
        startIntentController.startIntent(intent)
    }

    private fun openInBrowser() {
        if (fallbackUrl.isNotEmpty()) {
            startIntentController.startBrowser(fallbackUrl)
        }
    }
}