package ru.mail.search.assistant

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.device.IntentAction
import ru.mail.search.assistant.data.device.Payload
import ru.mail.search.assistant.services.deviceinfo.PackageManagerChecker
import ru.mail.search.assistant.services.deviceinfo.PackageManagerCheckerImpl

class StartIntentController(
    context: Context,
    private val externalApplicationNavigation: ExternalApplicationNavigation,
    private val logger: Logger?
) {

    val packageManagerChecker: PackageManagerChecker = PackageManagerCheckerImpl(context, logger)

    fun application(packageName: String): Boolean {
        return if (packageManagerChecker.checkApp(packageName)) {
            val intent = packageManagerChecker.getLaunchIntentForPackage(packageName)
            intent?.let {
                externalApplicationNavigation.startActivity(intent)
                true
            } ?: false
        } else {
            false
        }
    }

    fun startBrowser(ulr: String) {
        externalApplicationNavigation.startBrowser(ulr)
    }

    fun doAction(actionType: String, options: Map<String, Payload<out Any>>) {
        val intent = IntentAction(actionType).invoke(options)
        externalApplicationNavigation.startActivity(intent)
    }

    fun startIntent(intent: Intent) {
        runCatching {
            externalApplicationNavigation.startActivity(intent)
        }.onFailure {
            if (it is ActivityNotFoundException) {
                tryOpenIntentByPackageName(intent)
            }
        }
    }

    private fun tryOpenIntentByPackageName(intent: Intent) {
        packageManagerChecker.getPackageNameFromIntent(intent)?.let {
            val launchIntent = packageManagerChecker.getLaunchIntentForPackage(it)
            launchIntent?.let { newIntent -> externalApplicationNavigation.startActivity(newIntent) }
        }
    }
}